package com.gm.hrms.mapper;

import com.gm.hrms.dto.response.LoginResponseDTO;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.entity.UserAuth;

public class LoginMapper {

    public static LoginResponseDTO toResponse(
            String accessToken,
            String refreshToken,
            UserAuth auth
    ) {

        PersonalInformation person = auth.getPersonalInformation();

        return LoginResponseDTO.builder()
                .token(accessToken)
                .refreshToken(refreshToken)
                .userId(person.getId())
                .username(auth.getUsername())
                .fullName(
                        person.getFirstName() + " " + person.getLastName()
                )
                .role(auth.getRole().name())
                .active(auth.getActive())
                .build();
    }
}