package com.gm.hrms.service;

import com.gm.hrms.dto.request.TraineeWorkDetailsRequestDTO;
import com.gm.hrms.entity.Trainee;

public interface TraineeWorkService {
    void saveOrUpdate(Trainee trainee, TraineeWorkDetailsRequestDTO dto);
}
