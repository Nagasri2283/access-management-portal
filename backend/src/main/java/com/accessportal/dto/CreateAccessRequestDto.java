package com.accessportal.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import lombok.Data;

@Data
public class CreateAccessRequestDto {

    @NotNull(message = "Resource id is required")
    private Long resourceId;

    @NotBlank(message = "Justification is required")
    private String justification;
}
