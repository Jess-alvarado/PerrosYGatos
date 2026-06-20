package com.professional.pyg_professional.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.Mockito.when;

@ExtendWith(MockitoExtension.class)
class GlobalExceptionHandlerTest {

    @InjectMocks
    private GlobalExceptionHandler handler;

    @Mock
    private HttpServletRequest request;

    @Test
    @DisplayName("Returns 404 for NotFoundException on Professional profile")
    void handleNotFound_shouldReturn404() {
        when(request.getRequestURI()).thenReturn("/professionals/profile");
        when(request.getHeader("X-Trace-Id")).thenReturn("trace-prof-123");

        ResponseEntity<ErrorResponse> response = handler.handleNotFound(
                new NotFoundException("Professional profile not found"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo("Professional profile not found");
    }

    @Test
    @DisplayName("Returns 409 for AlreadyExistsException on Professional profile")
    void handleAlreadyExists_shouldReturn409() {
        when(request.getRequestURI()).thenReturn("/professionals/profile");

        ResponseEntity<ErrorResponse> response = handler.handleAlreadyExists(
                new AlreadyExistsException("Professional profile already exists"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getErrorCode()).isEqualTo(ErrorCode.ALREADY_EXISTS);
    }
}