package com.gm.hrms.service;

import com.gm.hrms.dto.request.ChangePasswordRequestDTO;
import com.gm.hrms.dto.request.LoginRequestDTO;
import com.gm.hrms.dto.request.ResetPasswordRequestDTO;
import com.gm.hrms.dto.response.LoginResponseDTO;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.enums.RoleType;

public interface AuthService {

    void createAuthForPerson(PersonalInformation person,
                             RoleType role,
                             String rawPassword);

    boolean existsByPerson(PersonalInformation person);

    LoginResponseDTO login(LoginRequestDTO request);

    LoginResponseDTO refreshToken(String refreshToken);

    void logout(String refreshToken);

    void changePassword(ChangePasswordRequestDTO request);

    /** Step 3 of forgot-password flow — persist the new hashed password */
    void resetPassword(ResetPasswordRequestDTO request);
}