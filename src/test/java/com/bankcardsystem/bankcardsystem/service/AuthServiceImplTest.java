package com.bankcardsystem.bankcardsystem.service;

import com.bankcardsystem.bankcardsystem.dto.AuthRequest;
import com.bankcardsystem.bankcardsystem.entity.Role;
import com.bankcardsystem.bankcardsystem.entity.User;
import com.bankcardsystem.bankcardsystem.exception.AccessDeniedException;
import com.bankcardsystem.bankcardsystem.exception.NotFoundException;
import com.bankcardsystem.bankcardsystem.repository.UserRepository;
import com.bankcardsystem.bankcardsystem.security.JwtTokenProvider;
import com.bankcardsystem.bankcardsystem.service.impl.AuthServiceImpl;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.argThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;


@ExtendWith(MockitoExtension.class)
public class AuthServiceImplTest {
    @Mock
    private UserRepository userRepository;

    @Mock
    private PasswordEncoder passwordEncoder;

    @Mock
    private JwtTokenProvider jwtTokenProvider;

    @InjectMocks
    private AuthServiceImpl authService;

    // ---------- register ----------

    @Test
    void shouldRegisterUserSuccessfully() {
        // given
        AuthRequest request = AuthRequest.builder()
                .email("test@example.com")
                .password("1234")
                .build();

        when(userRepository.findByEmailIgnoreCase("test@example.com"))
                .thenReturn(Optional.empty());

        when(passwordEncoder.encode("1234"))
                .thenReturn("hashed-password");

        // when
        ResponseEntity<Void> response = authService.register(request);

        // then
        assertEquals(HttpStatus.CREATED, response.getStatusCode());
        verify(userRepository).save(argThat(user ->
                user.getEmail().equals("test@example.com") &&
                        user.getPassword().equals("hashed-password") &&
                        user.getRole() == Role.USER
        ));
    }

    @Test
    void shouldReturnConflictIfUserAlreadyExists() {
        // given
        AuthRequest request = AuthRequest.builder()
                .email("existing@example.com")
                .password("password")
                .build();

        when(userRepository.findByEmailIgnoreCase("existing@example.com"))
                .thenReturn(Optional.of(new User()));

        // when
        ResponseEntity<Void> response = authService.register(request);

        // then
        assertEquals(HttpStatus.CONFLICT, response.getStatusCode());
        verify(userRepository, never()).save(any());
    }

    // ---------- login ----------

    @Test
    void shouldLoginSuccessfully() {
        // given
        AuthRequest request = AuthRequest.builder()
                .email("user@example.com")
                .password("password")
                .build();

        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("encoded-password");
        user.setRole(Role.USER);

        when(userRepository.findByEmailIgnoreCase("user@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("password", "encoded-password"))
                .thenReturn(true);
        when(jwtTokenProvider.createToken("user@example.com", "USER"))
                .thenReturn("mocked-jwt");

        // when
        String token = authService.login(request);

        // then
        assertEquals("mocked-jwt", token);
    }

    @Test
    void shouldThrowIfUserNotFound() {
        // given
        AuthRequest request = AuthRequest.builder()
                .email("absent@example.com")
                .password("password")
                .build();
        when(userRepository.findByEmailIgnoreCase("absent@example.com"))
                .thenReturn(Optional.empty());

        // when & then
        assertThrows(NotFoundException.class, () -> authService.login(request));
    }

    @Test
    void shouldThrowIfPasswordIncorrect() {
        // given
        AuthRequest request = AuthRequest.builder()
                .email("user@example.com")
                .password("wrong-password")
                .build();

        User user = new User();
        user.setEmail("user@example.com");
        user.setPassword("encoded-password");

        when(userRepository.findByEmailIgnoreCase("user@example.com"))
                .thenReturn(Optional.of(user));
        when(passwordEncoder.matches("wrong-password", "encoded-password"))
                .thenReturn(false);

        // when & then
        assertThrows(AccessDeniedException.class, () -> authService.login(request));
    }

}
