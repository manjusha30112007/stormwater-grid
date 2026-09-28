package com.mentorconnect.repository;

import com.mentorconnect.entity.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;

@Repository
public interface StudentRepository extends JpaRepository<Student, Long> {
    Optional<Student> findByEmail(String email);
    boolean existsByEmail(String email);

    @Query("SELECT DISTINCT s FROM Student s LEFT JOIN FETCH s.interestTags")
    List<Student> findAllWithInterestTags();

    @Query("SELECT s FROM Student s LEFT JOIN FETCH s.interestTags WHERE s.id = :id")
    Optional<Student> findByIdWithInterestTags(Long id);
}
