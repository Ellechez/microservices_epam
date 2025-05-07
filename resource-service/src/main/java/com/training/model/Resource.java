package com.training.model;

import jakarta.persistence.*;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

@Entity
@Data
@NoArgsConstructor
@Table(name = "RESOURCE")
public class Resource {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY) // Auto-increment for PostgreSQL
    private Integer id;

    private String fileName;

    private String type;

    @Column(columnDefinition = "BYTEA") // PostgreSQL binary data type
    private byte[] data;

    public Resource(String name, String type, byte[] data) {
        if (data == null || data.length == 0) {
            throw new IllegalArgumentException("Data cannot be null or empty.");
        }
        this.fileName = name;
        this.type = type;
        this.data = data;
    }
}