package com.accessportal.dto;

import lombok.Data;

@Data
public class ReviewRequestDto {
    // Optional comment explaining the approval/rejection decision.
    private String comment;
}
