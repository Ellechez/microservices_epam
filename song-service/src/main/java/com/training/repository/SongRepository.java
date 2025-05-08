package com.training.repository;

import com.training.model.Song;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, Integer> {

    @NotNull Optional<Song> findById(@NotNull Integer id);
    boolean existsById(@NotNull Integer id);
    Optional<Song> findFirstByOrderByIdDesc();

}