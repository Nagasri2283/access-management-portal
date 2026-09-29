package com.accessportal.dto;

import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class CreateResourceDto {

    @NotBlank(message = "Name is required")
    private String name;

    @NotBlank(message = "Description is required")
    private String description;

    @NotBlank(message = "Resource type is required")
    private String resourceType;
}
