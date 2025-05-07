package com.training.service;

import com.training.convertor.Mp3MetadataConvertor;
import com.training.exception.BadRequestException;
import com.training.model.Resource;
import com.training.model.ResourceDTO;
import com.training.model.ResourceMetadata;
import com.training.model.SongDto;
import com.training.repository.ResourceRepository;
import org.apache.tika.exception.TikaException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.stream.Collectors;

@Service
public class ResourceService {


    private ResourceRepository resourceRepository;
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ResourceService.class);

    @Autowired
    public ResourceService(ResourceRepository resourceRepository, RestTemplate restTemplate) {
        this.resourceRepository = resourceRepository;
        this.restTemplate = restTemplate;
    }

    public void isMp3File(byte[] file) {
        if (file == null || file.length < 3) {
            throw new BadRequestException("The uploaded file is too short to be valid.");
        }

        // Check for the ID3 tag (MP3 files often start with this)
        if (file[0] == 'I' && file[1] == 'D' && file[2] == '3') {
            return;
        }

        // Check for the MPEG-1 header (alternative method)
        if (file[0] == (byte) 0xFF && (file[1] & 0xF0) == 0xF0) {
            return;
        }

        throw new BadRequestException("The uploaded file is not a valid MP3.");
    }

    /**
     * Upload new resource by saving file details into DB.
     */
    @Transactional
    public ResourceDTO saveResource(MultipartFile fileData) throws TikaException, IOException, SAXException {
        if (fileData == null || fileData.getSize() == 0) {
            throw new IllegalArgumentException("The uploaded file is empty.");
        }
        byte[] fileBytes = fileData.getBytes(); // Get raw binary data
        isMp3File(fileBytes); // Validate MP3 data

        ResourceMetadata resourceMetadata = Mp3MetadataConvertor.convert(fileData.getInputStream());
        logger.info("Song metadata:" + resourceMetadata);
        String filename = resourceMetadata.getName();

        Resource resource = new Resource(filename, "audio/mpeg", fileData.getBytes());
        Resource savedResource = resourceRepository.save(resource);

        // REST call to song-service to delete associated metadata
        try {
            postSongMetadata(savedResource.getId(), resourceMetadata);
            logger.info("Song metadata posted successfully for Resource ID: {}", savedResource.getId());
        } catch (Exception ex) {
            logger.error("Failed to post Song metadata for Resource ID {}: {}", savedResource.getId(), ex.getMessage());
            throw new RuntimeException("Failed to post metadata to song-service", ex);
        }

        return new ResourceDTO(resource.getId());
    }

    private void postSongMetadata(Integer resourceId, ResourceMetadata metadata) {
        String songServiceUrl = "http://localhost:8081/songs";
        SongDto songDto = new SongDto(
                resourceId, // The ID should match the Resource ID
                metadata.getName(),
                metadata.getArtist(),
                metadata.getAlbum(),
                metadata.getLength(),
                metadata.getYear()
        );

        // Make POST request to song-service
        restTemplate.postForEntity(songServiceUrl, songDto, Void.class);
    }

    /**
     * Fetch a resource from the database by ID.
     */
    public Resource getResource(Integer id) {
        if (id <= 0) { // Check for negative IDs
            throw new BadRequestException("IDs are not allowed: " + id);
        }
        return resourceRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Resource with ID=" + id + " not found.")
        );
    }

    /**
     * Delete all specified resources and their associated song metadata.
     */
    @Transactional
    public List<Integer> removeFiles(String ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BadRequestException("IDs parameter cannot be empty");
        }
        if (ids.length() > 200) {
            throw new BadRequestException("The CSV string exceeds the maximum allowed length of 200 characters");
        }

        List<Integer> idList = Arrays.stream(ids.split(","))
                .map(Integer::parseInt)
                .collect(Collectors.toList());
        List<Integer> notFoundIds = idList.stream()
                .filter(id -> !resourceRepository.existsById(id))
                .toList();
        idList.removeAll(notFoundIds);
        List<Resource> resources = resourceRepository.findAllById(idList);

        logger.info("Resources found:" + resources);

        if (resources.isEmpty()) {
            return List.of();
        }

        // REST call to song-service to delete associated metadata
        try {
            restTemplate.delete("http://localhost:8081/songs?id=" + ids); // Adjust host/port accordingly
        } catch (Exception ex) {
            throw new RuntimeException("Failed to delete song metadata for Resource ID=" + ids, ex);
        }

        // Delete the resource records
        resourceRepository.deleteAllInBatch(resources);

        // Return successfully deleted resource IDs
        return resources.stream().map(Resource::getId).collect(Collectors.toList());
    }
}