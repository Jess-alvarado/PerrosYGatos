package com.owner.pyg_owner.exceptions;

import lombok.Builder;
import lombok.Getter;

import java.time.Instant;

@Getter
@Builder
public class ErrorResponse {
    private Instant timestamp;
    private int status;
    private ErrorCode errorCode;
    private String message;
    private String path;
    private String traceId;
}