package com.gm.hrms.repository;

import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.entity.UserAuth;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;

import java.util.Optional;

public interface UserAuthRepository extends JpaRepository<UserAuth, Long> {

    Optional<UserAuth> findByUsername(String username);

    boolean existsByUsername(String username);

    boolean existsByPersonalInformation(PersonalInformation personalInformation);

    boolean existsByUsernameIgnoreCase(String username);

}
