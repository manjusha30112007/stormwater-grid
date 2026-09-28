package com.mentorconnect.controller;

import com.mentorconnect.dto.AlumniRequestDto;
import com.mentorconnect.dto.AlumniResponseDto;
import com.mentorconnect.service.AlumniService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/alumni")
@CrossOrigin(origins = "*")
public class AlumniController {

    private final AlumniService alumniService;

    public AlumniController(AlumniService alumniService) {
        this.alumniService = alumniService;
    }

    @PostMapping
    public ResponseEntity<AlumniResponseDto> registerAlumni(@Valid @RequestBody AlumniRequestDto dto) {
        AlumniResponseDto created = alumniService.registerAlumni(dto);
        return new ResponseEntity<>(created, HttpStatus.CREATED);
    }

    @GetMapping
    public ResponseEntity<List<AlumniResponseDto>> getAllAlumni() {
        return ResponseEntity.ok(alumniService.getAllAlumni());
    }

    @GetMapping("/{id}")
    public ResponseEntity<AlumniResponseDto> getAlumniById(@PathVariable Long id) {
        return ResponseEntity.ok(alumniService.getAlumniById(id));
    }

    @PutMapping("/{id}")
    public ResponseEntity<AlumniResponseDto> updateAlumni(@PathVariable Long id,
                                                          @Valid @RequestBody AlumniRequestDto dto) {
        return ResponseEntity.ok(alumniService.updateAlumni(id, dto));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<Void> deleteAlumni(@PathVariable Long id) {
        alumniService.deleteAlumni(id);
        return ResponseEntity.noContent().build();
    }
}
