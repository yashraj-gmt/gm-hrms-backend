package com.gm.hrms.util;

import java.security.SecureRandom;

public class PasswordGenerator {

    private static final String CHAR_POOL =
            "ABCDEFGHIJKLMNOPQRSTUVWXYZabcdefghijklmnopqrstuvwxyz0123456789@#";

    public static String generatePassword(int length) {

        SecureRandom random = new SecureRandom();
        StringBuilder password = new StringBuilder();

        for (int i = 0; i < length; i++) {
            password.append(CHAR_POOL.charAt(random.nextInt(CHAR_POOL.length())));
        }

        return password.toString();
    }
}
