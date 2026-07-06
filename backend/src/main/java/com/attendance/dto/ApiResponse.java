package com.attendance.dto;

import com.fasterxml.jackson.annotation.JsonInclude;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.time.LocalDateTime;

/**
 * Standard API response wrapper for all endpoints.
 * Ensures consistent JSON structure across the entire API.
 *
 * @param <T> the type of the response data payload
 */
@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private int status;
    private String message;
    private T data;

    @Builder.Default
    private LocalDateTime timestamp = LocalDateTime.now();

    private String error;

    // ============================================================
    // Factory Methods
    // ============================================================

    public static <T> ApiResponse<T> success(String message, T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .status(200)
            .message(message)
            .data(data)
            .build();
    }

    public static <T> ApiResponse<T> success(T data) {
        return success("Operation successful", data);
    }

    public static <T> ApiResponse<T> created(String message, T data) {
        return ApiResponse.<T>builder()
            .success(true)
            .status(201)
            .message(message)
            .data(data)
            .build();
    }

    public static ApiResponse<Void> error(String message, int status) {
        return ApiResponse.<Void>builder()
            .success(false)
            .status(status)
            .message(message)
            .error(message)
            .build();
    }

    public static ApiResponse<Void> noContent(String message) {
        return ApiResponse.<Void>builder()
            .success(true)
            .status(204)
            .message(message)
            .build();
    }
}
