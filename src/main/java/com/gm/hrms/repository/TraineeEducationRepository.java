package com.gm.hrms.repository;

import com.gm.hrms.entity.Trainee;
import com.gm.hrms.entity.TraineeEducationDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TraineeEducationRepository extends JpaRepository<TraineeEducationDetails, Long> {
    Optional<TraineeEducationDetails> findByTrainee(Trainee trainee);
}