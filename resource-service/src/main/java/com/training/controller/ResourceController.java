package com.training.controller;

import com.training.model.Resource;
import com.training.service.ResourceService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.NoSuchElementException;

@RestController
public class ResourceController {


    private ResourceService resourceService;
    private static final Logger logger = LoggerFactory.getLogger(ResourceController.class);


    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping(path = "/resources", consumes = MediaType.MULTIPART_FORM_DATA_VALUE, produces = "application/json")
    public ResponseEntity<Object> uploadResource(@RequestParam("file") MultipartFile file) {
        try {
            return ResponseEntity.ok().body(resourceService.saveResource(file));
        } catch (IOException ex) {
            return ResponseEntity.status(HttpStatus.valueOf(400)).body(Collections.singletonMap("errorMessage", "The uploaded file is not a valid MP3."));
        } catch (Exception e) {
            logger.error("Error during MP3 upload: {}", e.getMessage(), e);
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(Collections.singletonMap("errorMessage", e.getMessage()));
        }
    }
    @GetMapping(path = "/resources/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getResource(@PathVariable Integer id) {
        Resource resource = resourceService.getResource(id);
        if (resource != null) {
            return ResponseEntity.ok()
                    .contentType(MediaType.valueOf(resource.getType()))
                    .body(resource.getData());
        } else {
            throw new NoSuchElementException("Resource with ID=" + id + " not found.");
        }
    }

    @DeleteMapping(path = "/resources", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<Object> deleteResources(@RequestParam String id) {

        // Call the service to delete resources by IDs
        List<Integer> deletedIds = resourceService.removeFiles(id);

        // Always return 200 OK with "ids" key (even if it's an empty list)
        return ResponseEntity.status(HttpStatus.OK).body(Collections.singletonMap("ids", deletedIds));
    }
}