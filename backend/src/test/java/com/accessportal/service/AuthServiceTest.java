package com.accessportal.service;

import com.accessportal.dto.LoginRequest;
import com.accessportal.dto.RegisterRequest;
import com.accessportal.entity.Role;
import com.accessportal.entity.User;
import com.accessportal.exception.BadRequestException;
import com.accessportal.repository.UserRepository;
import com.accessportal.security.JwtUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
class AuthServiceTest {

    @Mock
    private UserRepository userRepository;

    @Mock
    private JwtUtil jwtUtil;

    private PasswordEncoder passwordEncoder;

    @InjectMocks
    private AuthService authService;

    @BeforeEach
    void setUp() {
        passwordEncoder = new BCryptPasswordEncoder();
        authService = new AuthService(userRepository, passwordEncoder, jwtUtil);
    }

    @Test
    void register_createsUser_whenEmailNotTaken() {
        RegisterRequest request = new RegisterRequest();
        request.setFullName("Jane Doe");
        request.setEmail("jane@company.com");
        request.setPassword("secret123");
        request.setDepartment("Engineering");

        when(userRepository.existsByEmail("jane@company.com")).thenReturn(false);
        when(userRepository.save(any(User.class))).thenAnswer(invocation -> {
            User u = invocation.getArgument(0);
            u.setId(1L);
            return u;
        });
        when(jwtUtil.generateToken(anyString(), anyString(), anyLong())).thenReturn("fake-jwt-token");

        var response = authService.register(request);

        assertThat(response.getToken()).isEqualTo("fake-jwt-token");
        assertThat(response.getRole()).isEqualTo(Role.EMPLOYEE);
        assertThat(response.getEmail()).isEqualTo("jane@company.com");
        verify(userRepository).save(any(User.class));
    }

    @Test
    void register_throws_whenEmailAlreadyExists() {
        RegisterRequest request = new RegisterRequest();
        request.setEmail("existing@company.com");
        request.setFullName("Someone");
        request.setPassword("pw");
        request.setDepartment("IT");

        when(userRepository.existsByEmail("existing@company.com")).thenReturn(true);

        assertThatThrownBy(() -> authService.register(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("already exists");

        verify(userRepository, never()).save(any());
    }

    @Test
    void login_succeeds_withCorrectPassword() {
        String rawPassword = "mypassword";
        User existingUser = User.builder()
                .id(5L)
                .fullName("Sam")
                .email("sam@company.com")
                .password(passwordEncoder.encode(rawPassword))
                .department("Sales")
                .role(Role.MANAGER)
                .build();

        LoginRequest request = new LoginRequest();
        request.setEmail("sam@company.com");
        request.setPassword(rawPassword);

        when(userRepository.findByEmail("sam@company.com")).thenReturn(Optional.of(existingUser));
        when(jwtUtil.generateToken(anyString(), anyString(), anyLong())).thenReturn("fake-jwt-token");

        var response = authService.login(request);

        assertThat(response.getToken()).isEqualTo("fake-jwt-token");
        assertThat(response.getRole()).isEqualTo(Role.MANAGER);
    }

    @Test
    void login_throws_withWrongPassword() {
        User existingUser = User.builder()
                .id(5L)
                .email("sam@company.com")
                .password(passwordEncoder.encode("correct-password"))
                .role(Role.EMPLOYEE)
                .build();

        LoginRequest request = new LoginRequest();
        request.setEmail("sam@company.com");
        request.setPassword("wrong-password");

        when(userRepository.findByEmail("sam@company.com")).thenReturn(Optional.of(existingUser));

        assertThatThrownBy(() -> authService.login(request))
                .isInstanceOf(BadRequestException.class)
                .hasMessageContaining("Invalid email or password");
    }
}
