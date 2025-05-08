package com.training.controller;

import com.training.model.Resource;
import com.training.model.ResourceDTO;
import com.training.service.ResourceService;
import org.apache.tika.exception.TikaException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.xml.sax.SAXException;

import java.io.IOException;
import java.util.Collections;
import java.util.List;
import java.util.Map;

@RestController
public class ResourceController {


    private final ResourceService resourceService;

    @Autowired
    public ResourceController(ResourceService resourceService) {
        this.resourceService = resourceService;
    }

    @PostMapping(path = "/resources", consumes = "audio/mpeg", produces = "application/json")
    public ResponseEntity<ResourceDTO> uploadResource(@RequestBody byte[] file) throws TikaException, IOException, SAXException {
        return ResponseEntity.ok().body(resourceService.saveResource(file));
    }
    @GetMapping(path = "/resources/{id}", produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public ResponseEntity<byte[]> getResource(@PathVariable Integer id) {
        Resource resource = resourceService.getResource(id);
        return ResponseEntity.ok().contentType(MediaType.valueOf(resource.getType())).body(resource.getData());
    }

    @DeleteMapping(path = "/resources", produces = MediaType.APPLICATION_JSON_VALUE)
    @Transactional
    public ResponseEntity<Map<String, List<Integer>>> deleteResources(@RequestParam String id) {
        return ResponseEntity.ok().body(Collections.singletonMap("ids", resourceService.removeFiles(id)));
    }
}