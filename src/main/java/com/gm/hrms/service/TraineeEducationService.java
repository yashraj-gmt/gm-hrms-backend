package com.gm.hrms.service;

import com.gm.hrms.dto.request.TraineeEducationRequestDTO;
import com.gm.hrms.entity.Trainee;

public interface TraineeEducationService {
    void saveOrUpdate(Trainee trainee, TraineeEducationRequestDTO dto);
}