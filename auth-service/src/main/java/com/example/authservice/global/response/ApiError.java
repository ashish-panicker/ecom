package com.example.authservice.global.response;

import java.time.LocalDateTime;
import java.util.List;

public record ApiError(
        boolean success,
        String errorCode,
        String message,
        List<String> details,
        LocalDateTime timestamp
) {
    public static ApiError of(
            String errorCode,
            String message,
            List<String> details
    ) {
        return new ApiError(
                false,
                errorCode,
                message,
                details,
                LocalDateTime.now()
        );
    }
}
