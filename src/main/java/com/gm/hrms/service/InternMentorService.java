package com.gm.hrms.service;

import com.gm.hrms.dto.request.InternMentorRequestDTO;
import com.gm.hrms.entity.Intern;

public interface InternMentorService {

    void saveOrUpdate(Intern intern, InternMentorRequestDTO dto);
}