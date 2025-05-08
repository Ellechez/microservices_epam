package com.training.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

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

    public SongDto(Song song) {
        this.id = song.getId();
        this.name = song.getName();
        this.artist = song.getArtist();
        this.album = song.getAlbum();
        this.duration = song.getLength();
        this.year = song.getYear();
    }
}