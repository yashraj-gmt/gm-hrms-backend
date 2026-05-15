package com.gm.hrms.repository;

import com.gm.hrms.entity.TransferOtpToken;
import com.gm.hrms.enums.TransferOtpPurpose;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.Optional;

@Repository
public interface TransferOtpTokenRepository extends JpaRepository<TransferOtpToken, Long> {

    Optional<TransferOtpToken> findTopByUsernameAndPurposeAndUsedFalseAndExpiryAtAfterOrderByCreatedAtDesc(
            String username,
            TransferOtpPurpose purpose,
            LocalDateTime now);

    Optional<TransferOtpToken> findTopByUsernameAndOtpAndPurposeAndUsedFalseAndExpiryAtAfter(
            String username,
            String otp,
            TransferOtpPurpose purpose,
            LocalDateTime now);

    @Modifying
    @Transactional
    @Query("UPDATE TransferOtpToken t SET t.used = true WHERE t.username = :username AND t.purpose = :purpose AND t.used = false")
    void invalidateAll(@Param("username") String username, @Param("purpose") TransferOtpPurpose purpose);
}