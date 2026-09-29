package com.accessportal.service;

import com.accessportal.dto.AccessRequestResponseDto;
import com.accessportal.dto.CreateAccessRequestDto;
import com.accessportal.entity.AccessRequest;
import com.accessportal.entity.AccessStatus;
import com.accessportal.entity.Resource;
import com.accessportal.entity.User;
import com.accessportal.exception.BadRequestException;
import com.accessportal.exception.ForbiddenException;
import com.accessportal.exception.ResourceNotFoundException;
import com.accessportal.repository.AccessRequestRepository;
import com.accessportal.repository.ResourceRepository;
import com.accessportal.repository.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class AccessRequestService {

    private final AccessRequestRepository accessRequestRepository;
    private final ResourceRepository resourceRepository;
    private final UserRepository userRepository;

    public AccessRequestResponseDto createRequest(String requesterEmail, CreateAccessRequestDto dto) {
        User requester = getUserOrThrow(requesterEmail);

        Resource resource = resourceRepository.findById(dto.getResourceId())
                .orElseThrow(() -> new ResourceNotFoundException("Resource not found with id: " + dto.getResourceId()));

        AccessRequest request = AccessRequest.builder()
                .requester(requester)
                .resource(resource)
                .justification(dto.getJustification())
                .status(AccessStatus.PENDING)
                .build();

        return toDto(accessRequestRepository.save(request));
    }

    public List<AccessRequestResponseDto> getMyRequests(String requesterEmail) {
        User requester = getUserOrThrow(requesterEmail);
        return accessRequestRepository.findByRequesterOrderByCreatedAtDesc(requester).stream()
                .map(this::toDto)
                .toList();
    }

    public List<AccessRequestResponseDto> getAllRequests(AccessStatus statusFilter) {
        List<AccessRequest> requests = statusFilter != null
                ? accessRequestRepository.findByStatusOrderByCreatedAtDesc(statusFilter)
                : accessRequestRepository.findAllByOrderByCreatedAtDesc();

        return requests.stream().map(this::toDto).toList();
    }

    public AccessRequestResponseDto approveRequest(Long requestId, String reviewerEmail, String comment) {
        return reviewRequest(requestId, reviewerEmail, comment, AccessStatus.APPROVED);
    }

    public AccessRequestResponseDto rejectRequest(Long requestId, String reviewerEmail, String comment) {
        return reviewRequest(requestId, reviewerEmail, comment, AccessStatus.REJECTED);
    }

    public AccessRequestResponseDto cancelRequest(Long requestId, String requesterEmail) {
        AccessRequest request = getRequestOrThrow(requestId);

        if (!request.getRequester().getEmail().equalsIgnoreCase(requesterEmail)) {
            throw new ForbiddenException("You can only cancel your own requests");
        }

        if (request.getStatus() != AccessStatus.PENDING) {
            throw new BadRequestException("Only pending requests can be cancelled");
        }

        request.setStatus(AccessStatus.CANCELLED);
        return toDto(accessRequestRepository.save(request));
    }

    private AccessRequestResponseDto reviewRequest(Long requestId, String reviewerEmail, String comment, AccessStatus newStatus) {
        AccessRequest request = getRequestOrThrow(requestId);

        if (request.getStatus() != AccessStatus.PENDING) {
            throw new BadRequestException("This request has already been reviewed (" + request.getStatus() + ")");
        }

        User reviewer = getUserOrThrow(reviewerEmail);

        request.setStatus(newStatus);
        request.setReviewedBy(reviewer);
        request.setReviewComment(comment);

        return toDto(accessRequestRepository.save(request));
    }

    private AccessRequest getRequestOrThrow(Long id) {
        return accessRequestRepository.findById(id)
                .orElseThrow(() -> new ResourceNotFoundException("Access request not found with id: " + id));
    }

    private User getUserOrThrow(String email) {
        return userRepository.findByEmail(email)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + email));
    }

    private AccessRequestResponseDto toDto(AccessRequest request) {
        return AccessRequestResponseDto.builder()
                .id(request.getId())
                .requesterName(request.getRequester().getFullName())
                .requesterEmail(request.getRequester().getEmail())
                .resourceName(request.getResource().getName())
                .justification(request.getJustification())
                .status(request.getStatus())
                .reviewedByName(request.getReviewedBy() != null ? request.getReviewedBy().getFullName() : null)
                .reviewComment(request.getReviewComment())
                .createdAt(request.getCreatedAt())
                .updatedAt(request.getUpdatedAt())
                .build();
    }
}
