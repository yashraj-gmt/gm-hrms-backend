package com.gm.hrms.service;

import com.gm.hrms.dto.request.InternCollegeRequestDTO;
import com.gm.hrms.entity.Intern;

public interface InternCollegeService {

    void saveOrUpdate(Intern intern, InternCollegeRequestDTO dto);
}