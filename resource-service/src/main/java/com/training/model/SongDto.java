package com.training.model;

import jakarta.validation.constraints.*;
import lombok.*;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class SongDto {

    @NotNull(message = "ID is required and must be a number.")
    @Min(value = 1, message = "ID must be greater than 0.")
    private Integer id; // Song metadata must have a valid ID

    @NotBlank(message = "Name is required.")
    @Size(max = 100, message = "Name must be less than or equal to 100 characters.")
    private String name;

    @NotBlank(message = "Artist is required.")
    @Size(max = 100, message = "Artist must be less than or equal to 100 characters.")
    private String artist;

    @NotBlank(message = "Album is required.")
    @Size(max = 100, message = "Album must be less than or equal to 100 characters.")
    private String album;

    @NotBlank(message = "Duration is required.")
    @Pattern(regexp = "\\d{2}:\\d{2}", message = "Duration must be in MM:SS format.")
    private String duration;

    @NotBlank(message = "Year is required.")
    @Pattern(regexp = "\\d{4}", message = "Year must be in valid YYYY format (e.g., 1977).")
    private String year;

}