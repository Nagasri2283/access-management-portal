package com.accessportal.controller;

import com.accessportal.dto.CreateResourceDto;
import com.accessportal.dto.ResourceResponseDto;
import com.accessportal.service.ResourceService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequiredArgsConstructor
public class ResourceController {

    private final ResourceService resourceService;

    // Any authenticated user can view the resource catalog (needed to file a request)
    @GetMapping("/api/resources")
    public ResponseEntity<List<ResourceResponseDto>> listResources() {
        return ResponseEntity.ok(resourceService.listResources());
    }

    @PostMapping("/api/admin/resources")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<ResourceResponseDto> createResource(@Valid @RequestBody CreateResourceDto dto) {
        return ResponseEntity.status(HttpStatus.CREATED).body(resourceService.createResource(dto));
    }

    @DeleteMapping("/api/admin/resources/{id}")
    @PreAuthorize("hasRole('ADMIN')")
    public ResponseEntity<Void> deleteResource(@PathVariable Long id) {
        resourceService.deleteResource(id);
        return ResponseEntity.noContent().build();
    }
}
