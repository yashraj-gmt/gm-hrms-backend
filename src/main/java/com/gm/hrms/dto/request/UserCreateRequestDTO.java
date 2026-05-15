package com.gm.hrms.dto.request;

import com.gm.hrms.entity.WorkProfile;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotNull;
import lombok.Data;
import org.springframework.web.multipart.MultipartFile;

import java.util.Map;

@Data
public class UserCreateRequestDTO {

    @Valid
    @NotNull
    private PersonalInformationRequestDTO personalInformation;

    private EmployeeRequestDTO employee;
    private InternRequestDTO intern;
    private TraineeRequestDTO trainee;

    // documents key = documentType.key
    private Map<String, MultipartFile> documents;

    // optional reason if document not uploaded
    private Map<String, String> documentReasons;
}