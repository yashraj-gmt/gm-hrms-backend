package com.gm.hrms.service;

import com.gm.hrms.dto.request.BankDetailsRequestDTO;
import com.gm.hrms.entity.PersonalInformation;

public interface BankDetailsService {

    void saveOrUpdate(PersonalInformation personalInformation,
                      BankDetailsRequestDTO dto);
}