package com.behavior.pyg_behavior_case.exceptions;

import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.orm.ObjectOptimisticLockingFailureException;
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
    @DisplayName("Returns 404 with NOT_FOUND for NotFoundException")
    void handleNotFound_shouldReturn404() {
        when(request.getRequestURI()).thenReturn("/api/cases/99");
        when(request.getHeader("X-Trace-Id")).thenReturn("trace-001");

        ResponseEntity<ErrorResponse> response = handler.handleNotFound(
                new NotFoundException("Case not found"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.NOT_FOUND);
        assertThat(response.getBody().getErrorCode()).isEqualTo(ErrorCode.NOT_FOUND);
        assertThat(response.getBody().getMessage()).isEqualTo("Case not found");
        assertThat(response.getBody().getTraceId()).isEqualTo("trace-001");
    }

    @Test
    @DisplayName("Returns 409 with ALREADY_EXISTS for AlreadyExistsException")
    void handleAlreadyExists_shouldReturn409() {
        when(request.getRequestURI()).thenReturn("/api/cases/1/proposals");

        ResponseEntity<ErrorResponse> response = handler.handleAlreadyExists(
                new AlreadyExistsException("You already submitted a proposal"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getErrorCode()).isEqualTo(ErrorCode.ALREADY_EXISTS);
    }

    @Test
    @DisplayName("Returns 403 with FORBIDDEN_ROLE for ForbiddenRoleException")
    void handleForbiddenRole_shouldReturn403() {
        when(request.getRequestURI()).thenReturn("/api/cases");

        ResponseEntity<ErrorResponse> response = handler.handleForbiddenRole(
                new ForbiddenRoleException("Only owners can create cases"), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.FORBIDDEN);
        assertThat(response.getBody().getErrorCode()).isEqualTo(ErrorCode.FORBIDDEN_ROLE);
    }

    @Test
    @DisplayName("Returns 409 with INVALID_STATE for InvalidStateException")
    void handleInvalidState_shouldReturn409() {
        when(request.getRequestURI()).thenReturn("/api/cases/1/proposals/1/accept");

        ResponseEntity<ErrorResponse> response = handler.handleInvalidState(
                new InvalidStateException("Cannot accept — proposal is WITHDRAWN"),
                request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getErrorCode()).isEqualTo(ErrorCode.INVALID_STATE);
        assertThat(response.getBody().getMessage())
                .isEqualTo("Cannot accept — proposal is WITHDRAWN");
    }

    @Test
    @DisplayName("Returns 400 with VALIDATION_ERROR for MethodArgumentNotValidException")
    void handleValidation_shouldReturn400WithFirstFieldError() {
        when(request.getRequestURI()).thenReturn("/api/cases");

        FieldError fieldError = new FieldError(
                "behaviorCaseRequest", "title", "must not be blank");
        when(validationException.getBindingResult()).thenReturn(bindingResult);
        when(bindingResult.getFieldErrors()).thenReturn(List.of(fieldError));

        ResponseEntity<ErrorResponse> response = handler.handleValidation(
                validationException, request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.BAD_REQUEST);
        assertThat(response.getBody().getErrorCode()).isEqualTo(ErrorCode.VALIDATION_ERROR);
        assertThat(response.getBody().getMessage()).isEqualTo("title: must not be blank");
    }

    @Test
    @DisplayName("Returns 409 with INVALID_STATE for OptimisticLockingFailureException")
    void handleOptimisticLock_shouldReturn409() {
        when(request.getRequestURI()).thenReturn("/api/cases/1/proposals/1/accept");

        ResponseEntity<ErrorResponse> response = handler.handleOptimisticLock(
                new ObjectOptimisticLockingFailureException(Object.class, 1L), request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.CONFLICT);
        assertThat(response.getBody().getErrorCode()).isEqualTo(ErrorCode.INVALID_STATE);
        assertThat(response.getBody().getMessage())
                .isEqualTo("The case was modified by another request. Please try again.");
    }

    @Test
    @DisplayName("Returns 500 without leaking internal details for unexpected exceptions")
    void handleGeneric_shouldReturn500WithoutLeakingMessage() {
        when(request.getRequestURI()).thenReturn("/api/cases");

        ResponseEntity<ErrorResponse> response = handler.handleGeneric(
                new RuntimeException("jdbc connection refused at 192.168.1.1:5432"),
                request);

        assertThat(response.getStatusCode()).isEqualTo(HttpStatus.INTERNAL_SERVER_ERROR);
        assertThat(response.getBody().getErrorCode()).isEqualTo(ErrorCode.INTERNAL_ERROR);
        assertThat(response.getBody().getMessage())
                .isEqualTo("An unexpected error occurred");
        assertThat(response.getBody().getMessage())
                .doesNotContain("jdbc")
                .doesNotContain("192.168.1.1");
    }

    @Test
    @DisplayName("TraceId is included in error response when present in request")
    void buildResponse_withTraceId_shouldIncludeItInResponse() {
        when(request.getRequestURI()).thenReturn("/api/cases/99");
        when(request.getHeader("X-Trace-Id")).thenReturn("abc-123-xyz");

        ResponseEntity<ErrorResponse> response = handler.handleNotFound(
                new NotFoundException("Case not found"), request);

        assertThat(response.getBody().getTraceId()).isEqualTo("abc-123-xyz");
        assertThat(response.getBody().getPath()).isEqualTo("/api/cases/99");
        assertThat(response.getBody().getTimestamp()).isNotNull();
    }
}