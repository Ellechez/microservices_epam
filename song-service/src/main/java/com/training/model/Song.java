package com.training.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Entity
@Data
@Getter
@Setter
@Table(name = "SONG")
@NoArgsConstructor
public class Song {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;
    private String name;
    private String artist;
    private String album;
    private String length;
    private String year;

    public Song(Integer id, String name, String artist, String album, String length, String year) {
        this.id = id;
        this.name = name;
        this.artist = artist;
        this.album = album;
        this.length = length;
        this.year = year;
    }




}
