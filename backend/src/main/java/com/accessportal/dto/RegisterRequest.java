package com.accessportal.dto;

import com.accessportal.entity.Role;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import lombok.Data;

@Data
public class RegisterRequest {

    @NotBlank(message = "Full name is required")
    private String fullName;

    @NotBlank(message = "Email is required")
    @Email(message = "Email must be valid")
    private String email;

    @NotBlank(message = "Password is required")
    private String password;

    @NotBlank(message = "Department is required")
    private String department;

    // Optional: defaults to EMPLOYEE if not provided.
    // In production, only ADMIN should be able to set MANAGER/ADMIN roles —
    // Phase 2 will lock this down behind an admin-only endpoint.
    private Role role;
}
