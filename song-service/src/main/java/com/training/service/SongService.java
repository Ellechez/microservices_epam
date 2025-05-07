package com.training.service;

import com.training.exception.AlreadyExistsException;
import com.training.exception.BadRequestException;
import com.training.exception.ConflictException;
import com.training.exception.ResourceNotFoundException;
import com.training.model.Song;
import com.training.model.SongDto;
import com.training.repository.SongRepository;
import jakarta.transaction.Transactional;
import org.apache.logging.log4j.message.StringFormattedMessage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

@Service
public class SongService {

    private final SongRepository songRepository;

    private static final Logger logger = LoggerFactory.getLogger(SongService.class);

    @Autowired
    public SongService(SongRepository songRepository) {
        this.songRepository = songRepository;
    }

    /**
     * Store a new song metadata record.
     */

    @Transactional
    public Song store(SongDto songDto) {

        if (songRepository.existsById(songDto.getId())) {
            throw new AlreadyExistsException("Song already exists with this resource ID: " +  songDto.getId().toString());
        }

        validateData(songDto);

        // Convert SongDto to Song entity
        Song song = new Song(
                songDto.getId(),
                songDto.getName(),
                songDto.getArtist(),
                songDto.getAlbum(),
                songDto.getDuration(),
                songDto.getYear()
        );

        // Ensure there's no existing song metadata for the given resource ID
        if (songRepository.findById(songDto.getId()).isPresent()) {
            throw new IllegalStateException("Metadata for this Resource ID already exists.");
        }

        return songRepository.save(song);
    }

    private static void validateData(SongDto songDto) {
        if (songDto.getId() == null || songDto.getName() == null || songDto.getArtist() == null) {
            throw new BadRequestException("Resource ID, name, and artist are required.");
        }
        if (songDto.getId() <= 0) {
            throw new BadRequestException("Resource ID must be a positive number.");
        }
        if (songDto.getName().length() > 100) {
            throw new BadRequestException("Name must be less than or equal to 100 characters.");
        }
        if (songDto.getArtist().length() > 100) {
            throw new BadRequestException("Artist must be less than or equal to 100 characters.");
        }
        if (songDto.getAlbum() != null && songDto.getAlbum().length() > 100) {
            throw new BadRequestException("Album must be less than or equal to 100 characters.");
        }
        if (songDto.getDuration() != null && !songDto.getDuration().matches("\\d{2}:\\d{2}")) {
            throw new BadRequestException("Duration must be in MM:SS format.");
        }
        if (songDto.getYear() != null && !songDto.getYear().matches("^\\d{4}$")) {
            throw new BadRequestException("Year must be in valid YYYY format (e.g., 1977)." + songDto.getYear());
        }
    }

    public SongDto getSongByResourceId(Integer id) {
        return new SongDto(songRepository.findById(id).orElseThrow(() -> new ResourceNotFoundException("Song with id: " + id + "not found")));
    }
    public SongDto findFirstByOrderByIdDesc() {
        return new SongDto(songRepository.findFirstByOrderByIdDesc().orElseThrow(() -> new ResourceNotFoundException("Song not found")));
    }

    /**
     * Delete song metadata records for specified resource IDs.
     */
    @Transactional
    public Map<String,List<Integer>> deleteSongs(String ids) {
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
                .filter(id -> !songRepository.existsById(id))
                .toList();
        idList.removeAll(notFoundIds);

        logger.info("Metadata found:" + idList);

        songRepository.deleteAllById(idList);
        return Map.of("ids", idList);
    }

}