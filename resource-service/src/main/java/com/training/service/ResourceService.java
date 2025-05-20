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
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.client.RestTemplate;
import org.xml.sax.SAXException;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.util.Arrays;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.regex.Pattern;
import java.util.stream.Collectors;

@Service
public class ResourceService {


    private ResourceRepository resourceRepository;
    private final RestTemplate restTemplate;
    private static final Logger logger = LoggerFactory.getLogger(ResourceService.class);

    @Value("${song.service.url}")
    private String songServiceUrl;

    @Autowired
    public ResourceService(ResourceRepository resourceRepository, RestTemplate restTemplate) {
        this.resourceRepository = resourceRepository;
        this.restTemplate = restTemplate;
    }

    /**
     * Upload new resource by saving file details into DB.
     */
    @Transactional
    public ResourceDTO saveResource(byte[] fileBytes) throws TikaException, IOException, SAXException {
        isMp3File(fileBytes);

        ResourceMetadata resourceMetadata = Mp3MetadataConvertor.convert(new ByteArrayInputStream(fileBytes));
        logger.info("Song metadata:" + resourceMetadata);
        String filename = resourceMetadata.getName();

        Resource resource = new Resource(filename, "audio/mpeg", fileBytes);
        Resource savedResource = resourceRepository.save(resource);

        // REST call to song-service to post associated metadata
        try {
            postSongMetadata(savedResource.getId(), resourceMetadata, songServiceUrl + "/songs");
            logger.info("Song metadata posted successfully for Resource ID: {}", savedResource.getId());
        } catch (Exception ex) {
            logger.error("Failed to post Song metadata for Resource ID {}: {}", savedResource.getId(), ex.getMessage());
            throw new RuntimeException("Failed to post metadata to song-service", ex);
        }

        return new ResourceDTO(resource.getId());
    }

    private void isMp3File(byte[] file) {
        if (file == null || file.length < 3) {
            throw new BadRequestException("Invalid file format: application/json. Only MP3 files are allowed");
        }
        if (file[0] == 'I' && file[1] == 'D' && file[2] == '3') return;
        if (file[0] == (byte) 0xFF && (file[1] & 0xF0) == 0xF0) return;

        throw new BadRequestException("Invalid file format: application/json. Only MP3 files are allowed");
    }

    private void postSongMetadata(Integer resourceId, ResourceMetadata metadata, String songServiceUrl) {
        SongDto songDto = new SongDto(
                resourceId,
                metadata.getName(),
                metadata.getArtist(),
                metadata.getAlbum(),
                metadata.getLength(),
                metadata.getYear()
        );
        restTemplate.postForEntity(songServiceUrl, songDto, Void.class);
    }

    /**
     * Fetch a resource from the database by ID.
     */
    public Resource getResource(Integer id) {
        if (id <= 0) {
            throw new BadRequestException("Invalid value '" + id + "' for ID. Must be a positive integer");
        }
        return resourceRepository.findById(id).orElseThrow(() ->
                new NoSuchElementException("Resource with ID=" + id + " not found")
        );
    }

    /**
     * Delete all specified resources and their associated song metadata.
     */
    @Transactional
    public List<Integer> removeFiles(String ids) {
        checkIfValid(ids);
        List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        List<Integer> notFoundIds = idList.stream().filter(id -> !resourceRepository.existsById(id)).toList();
        idList.removeAll(notFoundIds);
        List<Resource> resources = resourceRepository.findAllById(idList);

        logger.info("Resources found:" + resources);

        if (resources.isEmpty()) return List.of();

        // REST call to song-service to delete associated metadata
        try {
            restTemplate.delete(songServiceUrl + "/songs?id=" + ids);
        } catch (Exception ex2) {
            throw new RuntimeException("Failed to delete song metadata for Resource ID=" + ids, ex2);
        }

        resourceRepository.deleteAllInBatch(resources);

        return resources.stream().map(Resource::getId).collect(Collectors.toList());
    }

    private void checkIfValid(String ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BadRequestException("IDs parameter cannot be empty");
        }
        if (ids.length() > 200) {
            throw new BadRequestException(
                    "CSV string is too long: received " + ids.length() + " characters, maximum allowed is 200");
        }
        Arrays.stream(ids.split(",")).forEach(str -> {
                    if (!isNumeric(str)) throw new BadRequestException(
                            "Invalid ID format: '" + str + "'. Only positive integers are allowed");
                });
    }

    public boolean isNumeric(String strNum) {
        Pattern pattern = Pattern.compile("-?\\d+(\\.\\d+)?");
        if (strNum == null) {
            return false;
        }
        return pattern.matcher(strNum).matches();
    }
}