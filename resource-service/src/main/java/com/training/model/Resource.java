package com.training.model;

import lombok.Data;
import lombok.NoArgsConstructor;
import org.jetbrains.annotations.NotNull;

import javax.persistence.*;

@Entity
@Data
@NoArgsConstructor
@Table(name = "RESOURCE")
public class Resource {

    @Id
    @NotNull
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Integer id;

    private String fileName;

    private String type;

    @Column(columnDefinition = "BYTEA")
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