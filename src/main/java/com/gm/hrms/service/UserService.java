package com.gm.hrms.service;

import com.gm.hrms.dto.request.ProfileUpdateRequestDTO;
import com.gm.hrms.dto.request.UserCreateRequestDTO;
import com.gm.hrms.dto.response.UserProfileResponseDTO;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

public interface UserService {

    // New (controller call)
    Object create(
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

}