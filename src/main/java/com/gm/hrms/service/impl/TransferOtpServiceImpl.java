package com.gm.hrms.service.impl;

import com.gm.hrms.entity.TransferOtpToken;
import com.gm.hrms.entity.UserAuth;
import com.gm.hrms.enums.TransferOtpPurpose;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.repository.TransferOtpTokenRepository;
import com.gm.hrms.repository.UserAuthRepository;
import com.gm.hrms.service.EmailService;
import com.gm.hrms.service.TransferOtpService;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Slf4j
public class TransferOtpServiceImpl implements TransferOtpService {

    private static final int OTP_EXPIRY_MINUTES = 5;

    private final TransferOtpTokenRepository otpRepo;
    private final UserAuthRepository          userAuthRepo;
    private final EmailService                emailService;

    @Override
    @Transactional
    public void generateOtp(String username, TransferOtpPurpose purpose) {

        UserAuth user = userAuthRepo.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found: " + username));

        // Invalidate any previous un-used OTPs for same user+purpose
        otpRepo.invalidateAll(username, purpose);

        String otp = generateSixDigit();

        otpRepo.save(TransferOtpToken.builder()
                .username(username)
                .otp(otp)
                .purpose(purpose)
                .expiryAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .used(false)
                .build());

        // Get email from user's contact
        String email = user.getPersonalInformation().getContact().getOfficeEmail();
        String name  = user.getPersonalInformation().getFirstName();

        emailService.sendTransferOtp(email, name, otp, purpose.name());
        log.info("Transfer OTP generated for {} | purpose={}", username, purpose);
    }

    @Override
    public void verifyOtp(String username, String otp, TransferOtpPurpose purpose) {

        otpRepo.findTopByUsernameAndOtpAndPurposeAndUsedFalseAndExpiryAtAfter(
                        username, otp, purpose, LocalDateTime.now())
                .orElseThrow(() -> new InvalidRequestException(
                        "OTP is invalid, expired, or already used. Please generate a new one."));
    }

    @Override
    @Transactional
    public void consumeOtp(String username, TransferOtpPurpose purpose) {
        otpRepo.invalidateAll(username, purpose);
    }

    // ── Helpers ──────────────────────────────────────────────────────────────

    private String generateSixDigit() {
        return String.format("%06d", new SecureRandom().nextInt(1_000_000));
    }
}