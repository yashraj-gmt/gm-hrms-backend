package com.gm.hrms.repository;

import com.gm.hrms.entity.Trainee;
import com.gm.hrms.entity.TraineeMentorDetails;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.Optional;

public interface TraineeMentorRepository extends JpaRepository<TraineeMentorDetails, Long> {
    Optional<TraineeMentorDetails> findByTrainee(Trainee trainee);
}