package com.gm.hrms.repository;

import com.gm.hrms.entity.Trainee;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TraineeRepository extends JpaRepository<Trainee, Long> {

    Optional<Trainee> findByPersonalInformationId(Long personalInformationId);

    boolean existsByTraineeCode(String traineeCode);

    boolean existsByTraineeCodeAndIdNot(String traineeCode, Long id);
}