package com.gm.hrms.dto.response;

import lombok.Builder;
import lombok.Data;

@Data
@Builder
public class UserProfileResponseDTO {
    private Long   id;
    private String fullName;
    private String email;          // officeEmail or personalEmail
    private String phone;
    private String designation;
    private String department;
    private String branch;
    private String profileImageUrl;
    private String role;
}