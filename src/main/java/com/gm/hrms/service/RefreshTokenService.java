package com.gm.hrms.service;

import com.gm.hrms.entity.UserAuth;
import com.gm.hrms.entity.RefreshToken;

public interface RefreshTokenService {

    RefreshToken create(UserAuth auth, String token);

    RefreshToken verify(String token);

    void deleteByAuth(UserAuth auth);
}