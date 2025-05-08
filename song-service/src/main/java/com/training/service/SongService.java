package com.training.service;

import com.training.exception.AlreadyExistsException;
import com.training.exception.BadRequestException;
import com.training.exception.ResourceNotFoundException;
import com.training.exception.ValidationNotPassedException;
import com.training.model.Song;
import com.training.model.SongDto;
import com.training.repository.SongRepository;
import jakarta.transaction.Transactional;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Pattern;
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
            throw new AlreadyExistsException("Metadata for resource ID=" + songDto.getId().toString() + " already exists.");
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

        return songRepository.save(song);
    }

    private static void validateData(SongDto songDto) {
        Map<String, String> errorDetails= new HashMap<>();
        if (songDto.getId() == null) {
            errorDetails.put("name", "Song name is required.");
        }
        if (songDto.getName() == null) {
            errorDetails.put("name", "Song name is required.");
        }
        if (songDto.getArtist() == null) {
            errorDetails.put("name", "Song name is required.");
        }
        if (songDto.getId() != null && songDto.getId() <= 0) {
            errorDetails.put("id", "Resource ID must be a positive number.");
        }
        if (songDto.getName() != null && songDto.getName().length() > 100) {
            errorDetails.put("name", "Name must be less than or equal to 100 characters.");
        }
        if (songDto.getArtist() != null && songDto.getArtist().length() > 100) {
            errorDetails.put("artist", "Artist must be less than or equal to 100 characters.");
        }
        if (songDto.getAlbum() != null && songDto.getAlbum().length() > 100) {
            errorDetails.put("album", "Album must be less than or equal to 100 characters.");
        }
        if (songDto.getDuration() != null
                && (!songDto.getDuration().matches("\\d{2}:\\d{2}")
                || Arrays.stream(songDto.getDuration().split(":")).anyMatch(n -> Integer.parseInt(n) > 60))) {
            errorDetails.put("duration", "Duration must be in mm:ss format with leading zeros.");
        }
        if (songDto.getYear() != null && !songDto.getYear().matches("^\\d{4}$")) {
            errorDetails.put("year", "Year must be between 1900 and 2099.");
        }
        if (!errorDetails.isEmpty()) {
            throw new ValidationNotPassedException("Validation error", errorDetails);
        }
    }

    /**
     * Fetch a song metadata from the database by ID.
     */
    public SongDto getSongByResourceId(Integer id) {
        return new SongDto(songRepository.findById(id).orElseThrow(() ->
                new ResourceNotFoundException("Song metadata for ID=" + id + " not found")));
    }

    /**
     * Delete song metadata records for specified resource IDs.
     */
    @Transactional
    public Map<String,List<Integer>> deleteSongs(String ids) {
        checkIfValid(ids);
        List<Integer> idList = Arrays.stream(ids.split(",")).map(Integer::parseInt).collect(Collectors.toList());
        List<Integer> notFoundIds = idList.stream().filter(id -> !songRepository.existsById(id)).toList();
        idList.removeAll(notFoundIds);

        logger.info("Metadata found:" + idList);

        songRepository.deleteAllById(idList);
        return Map.of("ids", idList);
    }

    private void checkIfValid(String ids) {
        if (ids == null || ids.isEmpty()) {
            throw new BadRequestException("IDs parameter cannot be empty");
        }
        if (ids.length() > 200) {
            throw new BadRequestException(
                    "CSV string is too long: received " + ids.length() + " characters, maximum allowed is 200.");
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