package com.training.model;

import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongDto {

    private Integer id;
    private String name;
    private String artist;
    private String album;
    private String duration;
    private String year;

}