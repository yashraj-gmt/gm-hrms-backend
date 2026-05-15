package com.gm.hrms.payload;

import lombok.*;

import java.time.LocalDateTime;
import java.util.Map;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ApiError {

    private String errorCode;
    private String errorMessage;
    private LocalDateTime timestamp;
    private Map<String, String> validationErrors;
}
