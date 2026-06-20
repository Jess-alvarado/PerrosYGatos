package com.auth.pyg_auth.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.BindingResult;
import org.springframework.validation.FieldError;
import org.springframework.web.bind.MethodArgumentNotValidException;

import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @Mock
    private MethodArgumentNotValidException validationException;

    @Mock
    private BindingResult bindingResult;

    @Test
    @DisplayName("Returns 404 with NOT_FOUND errorCode for NotFoundException")
    void handleNotFound_shouldReturn404() {
        when(request.getRequestURI()).thenReturn("/api/auth/role");
        when(request.getHeader("X-Trace-Id")).thenReturn("trace-abc-123");

        ResponseEntity<ErrorResponse> response = handler.handleNotFound(
                new NotFoundException("Role not found: VET"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo("Role not found: VET");
        assertThat(response.getBody().getTraceId()).isEqualTo("trace-abc-123");
        assertThat(response.getBody().getPath()).isEqualTo("/api/auth/role");
    }

    @Test
    @DisplayName("Returns 409 with ALREADY_EXISTS errorCode for AlreadyExistsException")
    void handleAlreadyExists_shouldReturn409() {
        when(request.getRequestURI()).thenReturn("/api/auth/register");

        ResponseEntity<ErrorResponse> response = handler.handleAlreadyExists(
                new AlreadyExistsException("Username already exists"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getErrorCode()).isEqualTo(ErrorCode.ALREADY_EXISTS);
        assertThat(response.getBody().getMessage()).isEqualTo("Username already exists");
    }

    @Test
    @DisplayName("Returns 403 with FORBIDDEN_ROLE errorCode for ForbiddenRoleException")
    void handleForbiddenRole_shouldReturn403() {
        when(request.getRequestURI()).thenReturn("/api/auth/admin");

        ResponseEntity<ErrorResponse> response = handler.handleForbiddenRole(
                new ForbiddenRoleException("Requires ADMIN role"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_ROLE);
    }

    @Test
    @DisplayName("Returns 401 with INVALID_CREDENTIALS errorCode for InvalidCredentialsException")
    void handleInvalidCredentials_shouldReturn401() {
        when(request.getRequestURI()).thenReturn("/api/auth/login");

        ResponseEntity<ErrorResponse> response = handler.handleInvalidCredentials(
                new InvalidCredentialsException("Invalid username or password"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.UNAUTHORIZED);
        assertThat(response.getBody().getErrorCode()).isEqualTo(ErrorCode.INVALID_CREDENTIALS);
        assertThat(response.getBody().getMessage()).isEqualTo("Invalid username or password");
    }

    @Test
    @DisplayName("Returns 400 with VALIDATION_ERROR for invalid request fields")
    void handleValidation_shouldReturn400WithFirstFieldError() {
        when(request.getRequestURI()).thenReturn("/api/auth/register");

        FieldError fieldError = new FieldError("userRegisterRequest", "username", "must not be blank");
        when(validationException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ErrorResponse> response = handler.handleValidation(
                validationException, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorCode()).isEqualTo(ErrorCode.VALIDATION_ERROR);
        assertThat(response.getBody().getMessage()).isEqualTo("username: must not be blank");
    }

    @Test
    @DisplayName("Returns 500 with generic message for unexpected exceptions, without leaking internal details")
    void handleGeneric_shouldReturn500WithoutLeakingMessage() {
        when(request.getRequestURI()).thenReturn("/api/auth/login");

        ResponseEntity<ErrorResponse> response = handler.handleGeneric(
                new RuntimeException("jdbc connection refused at 192.168.1.111:5432"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getErrorCode()).isEqualTo(ErrorCode.INTERNAL_ERROR);
        assertThat(response.getBody().getMessage()).isEqualTo("An unexpected error occurred");
        assertThat(response.getBody().getMessage()).doesNotContain("jdbc");
        assertThat(response.getBody().getMessage()).doesNotContain("192.168.1.111");
    }
}