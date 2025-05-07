package com.training.model;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Pattern;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@AllArgsConstructor
@Getter
@Setter
@NoArgsConstructor
public class ResourceMetadata {

    @NotBlank(message = "'name' is required")
    private String name;

    @NotBlank(message = "'artist' is required")
    private String artist;

    @NotBlank(message = "'album' is required")
    private String album;

    @Pattern(regexp = "\\d{2}:\\d{2}", message = "'length' must be in format 'MM:SS'")
    private String length;

    @NotBlank(message = "'resourceId' is required")
    private String resourceId;

    @Pattern(regexp = "\\d{4}", message = "'year' must be in YYYY format")
    private String year;
}