package com.accessportal.controller;

import com.accessportal.dto.AccessRequestResponseDto;
import com.accessportal.dto.CreateAccessRequestDto;
import com.accessportal.dto.ReviewRequestDto;
import com.accessportal.entity.AccessStatus;
import com.accessportal.service.AccessRequestService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.access.prepost.PreAuthorize;
import org.springframework.security.core.Authentication;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/api/requests")
@RequiredArgsConstructor
public class AccessRequestController {

    private final AccessRequestService accessRequestService;

    // Any authenticated user (typically EMPLOYEE) can file a request for themselves
    @PostMapping
    public ResponseEntity<AccessRequestResponseDto> createRequest(Authentication authentication,
                                                                   @Valid @RequestBody CreateAccessRequestDto dto) {
        String email = authentication.getName();
        return ResponseEntity.status(HttpStatus.CREATED).body(accessRequestService.createRequest(email, dto));
    }

    @GetMapping("/my")
    public ResponseEntity<List<AccessRequestResponseDto>> getMyRequests(Authentication authentication) {
        return ResponseEntity.ok(accessRequestService.getMyRequests(authentication.getName()));
    }

    @PutMapping("/{id}/cancel")
    public ResponseEntity<AccessRequestResponseDto> cancelRequest(Authentication authentication, @PathVariable Long id) {
        return ResponseEntity.ok(accessRequestService.cancelRequest(id, authentication.getName()));
    }

    // MANAGER and ADMIN review queue
    @GetMapping
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<List<AccessRequestResponseDto>> getAllRequests(
            @RequestParam(required = false) AccessStatus status) {
        return ResponseEntity.ok(accessRequestService.getAllRequests(status));
    }

    @PutMapping("/{id}/approve")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<AccessRequestResponseDto> approveRequest(Authentication authentication,
                                                                    @PathVariable Long id,
                                                                    @RequestBody(required = false) ReviewRequestDto dto) {
        String comment = dto != null ? dto.getComment() : null;
        return ResponseEntity.ok(accessRequestService.approveRequest(id, authentication.getName(), comment));
    }

    @PutMapping("/{id}/reject")
    @PreAuthorize("hasAnyRole('MANAGER', 'ADMIN')")
    public ResponseEntity<AccessRequestResponseDto> rejectRequest(Authentication authentication,
                                                                   @PathVariable Long id,
                                                                   @RequestBody(required = false) ReviewRequestDto dto) {
        String comment = dto != null ? dto.getComment() : null;
        return ResponseEntity.ok(accessRequestService.rejectRequest(id, authentication.getName(), comment));
    }
}
