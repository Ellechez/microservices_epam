package com.training.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class ResourceMetadata {

    private String name;
    private String artist;
    private String album;
    private String length;
    private String resourceId;
    private String year;
}