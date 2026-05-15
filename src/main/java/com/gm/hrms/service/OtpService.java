package com.gm.hrms.service;

public interface OtpService {

    void generateAndSend(String email);

    void verify(String email, String otp);

    void markUsed(String email, String otp);

}