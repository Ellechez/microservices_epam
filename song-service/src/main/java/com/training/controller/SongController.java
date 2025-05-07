package com.training.controller;

import com.training.model.SongDto;
import com.training.service.SongService;
import jakarta.validation.Valid;
import jakarta.validation.constraints.Positive;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;

@RestController
public class SongController {

    @Autowired
    private SongService songService;

    @PostMapping(value = "/songs", produces = "application/json")
    public ResponseEntity<Map<String, Integer>> createSong(@RequestBody @Valid SongDto songDTO) {
        return ResponseEntity.ok(Map.of("id", songService.store(songDTO).getId()));
    }

    @GetMapping(value="/songs/{id}", produces = "application/json")
    public ResponseEntity<SongDto> getSongByResourceId(@PathVariable @Positive Integer id) {
        return ResponseEntity.ok(songService.getSongByResourceId(id));
    }
    @GetMapping(value="/songs/get-last-song", produces = "application/json")
    public ResponseEntity<SongDto> getLastSong() {
        return ResponseEntity.ok(songService.findFirstByOrderByIdDesc());
    }

    @DeleteMapping(value = "/songs", produces = "application/json")
    public ResponseEntity<Object> deleteSongs(@RequestParam String id) {

        return ResponseEntity.ok( songService.deleteSongs(id));
    }
}