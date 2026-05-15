package com.gm.hrms.service.impl;

import com.gm.hrms.entity.OtpToken;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.repository.OtpTokenRepository;
import com.gm.hrms.repository.UserAuthRepository;
import com.gm.hrms.service.EmailService;
import com.gm.hrms.service.OtpService;
import jakarta.transaction.Transactional;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;

import java.security.SecureRandom;
import java.time.LocalDateTime;

@Slf4j
@Service
@RequiredArgsConstructor
public class OtpServiceImpl implements OtpService {

    private static final int OTP_EXPIRY_MINUTES = 5;
    private static final SecureRandom RANDOM     = new SecureRandom();

    private final OtpTokenRepository    otpRepository;
    private final UserAuthRepository    authRepository;
    private final EmailService          emailService;

    // ─────────────────────────────── GENERATE & SEND ──────────────────────────

    @Override
    @Transactional
    public void generateAndSend(String email) {

        // Validate that the user actually exists in the system
        authRepository.findByUsername(email)
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No account found for email: " + email));

        // Purge any previous OTPs for this email
        otpRepository.deleteAllByEmail(email);

        // Generate cryptographically-safe 6-digit OTP
        String otp = String.format("%06d", RANDOM.nextInt(1_000_000));

        OtpToken token = OtpToken.builder()
                .email(email)
                .otp(otp)
                .expiryAt(LocalDateTime.now().plusMinutes(OTP_EXPIRY_MINUTES))
                .used(false)
                .purpose("FORGOT_PASSWORD")
                .build();

        otpRepository.save(token);

        // Extract display name from the auth record
        String displayName = authRepository.findByUsername(email)
                .map(a -> a.getPersonalInformation().getFirstName())
                .orElse("User");

        emailService.sendOtp(email, displayName, otp);

        log.info("OTP sent to {} (expires in {} min)", email, OTP_EXPIRY_MINUTES);
    }

    // ─────────────────────────────────── VERIFY ───────────────────────────────

    @Override
    public void verify(String email, String otp) {

        OtpToken token = otpRepository
                .findTopByEmailAndUsedFalseOrderByCreatedAtDesc(email)
                .orElseThrow(() ->
                        new InvalidRequestException(
                                "No pending OTP found for this email. Please request a new one."));

        if (token.getUsed()) {
            throw new InvalidRequestException(
                    "This OTP has already been used. Please request a new one.");
        }

        if (LocalDateTime.now().isAfter(token.getExpiryAt())) {
            throw new InvalidRequestException(
                    "OTP has expired. Please request a new one.");
        }

        if (!token.getOtp().equals(otp)) {
            throw new InvalidRequestException(
                    "Invalid OTP. Please check the code and try again.");
        }
    }

    // ──────────────────────────────────── MARK USED ───────────────────────────

    @Override
    @Transactional
    public void markUsed(String email, String otp) {

        otpRepository
                .findTopByEmailAndUsedFalseOrderByCreatedAtDesc(email)
                .ifPresent(token -> {
                    if (token.getOtp().equals(otp)) {
                        token.setUsed(true);
                    }
                });
    }
}