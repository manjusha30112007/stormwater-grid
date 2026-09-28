package com.mentorconnect.repository;

import com.mentorconnect.entity.InterestTag;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import java.util.Collection;
import java.util.List;
import java.util.Optional;

@Repository
public interface InterestTagRepository extends JpaRepository<InterestTag, Long> {
    Optional<InterestTag> findByNameIgnoreCase(String name);
    boolean existsByNameIgnoreCase(String name);
    List<InterestTag> findByNameInIgnoreCase(Collection<String> names);
}
