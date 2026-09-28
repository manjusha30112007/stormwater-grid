package com.mentorconnect.repository;

import com.mentorconnect.entity.Alumni;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface AlumniRepository extends JpaRepository<Alumni, Long> {
    Optional<Alumni> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT DISTINCT a FROM Alumni a LEFT JOIN FETCH a.expertiseTags")
    List<Alumni> findAllWithExpertiseTags();

    @Query("SELECT a FROM Alumni a LEFT JOIN FETCH a.expertiseTags WHERE a.id = :id")
    Optional<Alumni> findByIdWithExpertiseTags(Long id);
}
