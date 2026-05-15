package com.gm.hrms.service;

import com.gm.hrms.dto.request.InternInternshipRequestDTO;
import com.gm.hrms.entity.Intern;

public interface InternshipDetailsService {

    void saveOrUpdate(Intern intern, InternInternshipRequestDTO dto);
}