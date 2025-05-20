package com.training.controller;

import com.training.model.SongDto;
import com.training.service.SongService;
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
    public ResponseEntity<Map<String, Integer>> createSong(@RequestBody SongDto songDTO) {
        return ResponseEntity.ok(Map.of("id", songService.store(songDTO).getId()));
    }

    @GetMapping(value="/songs/{id}", produces = "application/json")
    public ResponseEntity<SongDto> getSongByResourceId(@PathVariable Integer id) {
        return ResponseEntity.ok(songService.getSongByResourceId(id));
    }

    @DeleteMapping(value = "/songs", produces = "application/json")
    public ResponseEntity<Map<String, List<Integer>>> deleteSongs(@RequestParam String id) {
        return ResponseEntity.ok(songService.deleteSongs(id));
    }
}