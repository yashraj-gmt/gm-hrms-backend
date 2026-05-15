package com.gm.hrms.repository;

import com.gm.hrms.entity.Trainee;
import com.gm.hrms.entity.TraineeTrainingDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TraineeTrainingRepository extends JpaRepository<TraineeTrainingDetails, Long> {
    Optional<TraineeTrainingDetails> findByTrainee(Trainee trainee);
}