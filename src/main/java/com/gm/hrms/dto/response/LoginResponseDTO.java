package com.gm.hrms.dto.response;


import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class LoginResponseDTO {

    // Security
    private String token;
    private String refreshToken;


    // User Info
    private Long userId;
    private String username;
    private String fullName;
    private String role;

    // Optional UI Helpers
    private String department;
    private Boolean active;
}

