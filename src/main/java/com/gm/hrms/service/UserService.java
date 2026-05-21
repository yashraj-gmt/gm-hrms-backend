package com.gm.hrms.service;

import com.gm.hrms.dto.request.ProfileUpdateRequestDTO;
import com.gm.hrms.dto.response.UserCreateResponseDTO;
import com.gm.hrms.dto.response.UserProfileResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UserService {

    UserCreateResponseDTO create(
            String personalInformationJson,
            String internJson,
            String employeeJson,
            String traineeJson,
            MultipartFile profileImage,
            Map<String, MultipartFile> documents,
            Map<String, String> reasons
    ) throws Exception;

    UserProfileResponseDTO getMe(String username);

    UserProfileResponseDTO updateMe(String username, ProfileUpdateRequestDTO dto);

    UserProfileResponseDTO updateAvatar(String username, MultipartFile image) throws Exception;

    boolean isEmailAvailable(String email, String type);

    boolean isEmployeeCodeAvailable(String code);
}