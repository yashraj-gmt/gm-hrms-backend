package com.gm.hrms.service;

import com.gm.hrms.enums.TransferOtpPurpose;

public interface TransferOtpService {

    /** Generates a 6-digit OTP and emails it to the requesting HR/Admin user */
    void generateOtp(String username, TransferOtpPurpose purpose);

    /**
     * Verifies the OTP.
     * Throws InvalidRequestException if invalid / expired / already used.
     */
    void verifyOtp(String username, String otp, TransferOtpPurpose purpose);

    /** Marks the most recent valid OTP as used after a successful transfer. */
    void consumeOtp(String username, TransferOtpPurpose purpose);
}