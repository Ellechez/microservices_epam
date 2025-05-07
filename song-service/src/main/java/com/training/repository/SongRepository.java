package com.training.repository;

import com.training.model.Song;
import org.jetbrains.annotations.NotNull;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Repository
public interface SongRepository extends JpaRepository<Song, Integer> {

    Optional<Song> findById(@NotNull Integer id);
    boolean existsById(@NotNull Integer id);
    Optional<Song> findFirstByOrderByIdDesc();

}