package com.accessportal.dto;

import com.accessportal.entity.AccessStatus;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class AccessRequestResponseDto {
    private Long id;
    private String requesterName;
    private String requesterEmail;
    private String resourceName;
    private String justification;
    private AccessStatus status;
    private String reviewedByName;
    private String reviewComment;
    private LocalDateTime createdAt;
    private LocalDateTime updatedAt;
}
