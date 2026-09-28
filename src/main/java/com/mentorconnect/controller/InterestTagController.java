package com.mentorconnect.controller;

import com.mentorconnect.dto.InterestTagDto;
import com.mentorconnect.service.InterestTagService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/tags")
@CrossOrigin(origins = "*")
public class InterestTagController {

    private final InterestTagService tagService;

    public InterestTagController(InterestTagService tagService) {
        this.tagService = tagService;
    }

    @PostMapping
    public ResponseEntity<InterestTagDto> createTag(@Valid @RequestBody InterestTagDto dto) {
        InterestTagDto created = tagService.createTag(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<InterestTagDto>> getAllTags() {
        return ResponseEntity.ok(tagService.getAllTags());
    }

    @GetMapping("/{id}")
    public ResponseEntity<InterestTagDto> getTagById(@PathVariable Long id) {
        return ResponseEntity.ok(tagService.getTagById(id));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteTag(@PathVariable Long id) {
        tagService.deleteTag(id);
        return ResponseEntity.noContent().build();
    }
}
