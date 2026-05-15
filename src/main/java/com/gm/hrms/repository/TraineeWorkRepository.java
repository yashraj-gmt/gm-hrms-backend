package com.gm.hrms.repository;

import com.gm.hrms.entity.Trainee;
import com.gm.hrms.entity.TraineeWorkDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TraineeWorkRepository
        extends JpaRepository<TraineeWorkDetails, Long> {

    Optional<TraineeWorkDetails> findByTrainee(Trainee trainee);
}
