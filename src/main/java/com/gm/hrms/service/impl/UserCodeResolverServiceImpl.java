package com.gm.hrms.service.impl;

import com.gm.hrms.entity.Employee;
import com.gm.hrms.entity.Intern;
import com.gm.hrms.entity.Trainee;
import com.gm.hrms.repository.EmployeeRepository;
import com.gm.hrms.repository.InternRepository;
import com.gm.hrms.repository.TraineeRepository;
import com.gm.hrms.service.UserCodeResolverService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class UserCodeResolverServiceImpl implements UserCodeResolverService {

    private final EmployeeRepository employeeRepository;
    private final InternRepository internRepository;
    private final TraineeRepository traineeRepository;

    @Override
    public String getCode(Long personalId) {

        // 🔹 EMPLOYEE
        Employee emp = employeeRepository
                .findByPersonalInformationId(personalId)
                .orElse(null);

        if (emp != null) return emp.getEmployeeCode();

        // 🔹 INTERN
        Intern intern = internRepository
                .findByPersonalInformationId(personalId)
                .orElse(null);

        if (intern != null) return intern.getInternCode();

        // 🔹 TRAINEE
        Trainee trainee = traineeRepository
                .findByPersonalInformationId(personalId)
                .orElse(null);

        if (trainee != null) return trainee.getTraineeCode();

        return "N/A";
    }
}