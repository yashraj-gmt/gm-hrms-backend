package com.gm.hrms.service.impl;

import com.gm.hrms.config.JwtService;
import com.gm.hrms.dto.request.ChangePasswordRequestDTO;
import com.gm.hrms.dto.request.LoginRequestDTO;
import com.gm.hrms.dto.request.ResetPasswordRequestDTO;
import com.gm.hrms.dto.response.LoginResponseDTO;
import com.gm.hrms.entity.PersonalInformation;
import com.gm.hrms.entity.UserAuth;
import com.gm.hrms.enums.RoleType;
import com.gm.hrms.exception.InvalidRequestException;
import com.gm.hrms.exception.ResourceNotFoundException;
import com.gm.hrms.mapper.LoginMapper;
import com.gm.hrms.repository.UserAuthRepository;
import com.gm.hrms.service.AuthService;
import com.gm.hrms.service.EmailService;
import com.gm.hrms.service.RefreshTokenService;
import lombok.RequiredArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.DisabledException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;

@Service
@RequiredArgsConstructor
@Transactional
public class AuthServiceImpl implements AuthService {

    private final UserAuthRepository    authRepository;
    private final PasswordEncoder       passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtService            jwtService;
    private final RefreshTokenService   refreshTokenService;
    private final EmailService          emailService;

    // ═══════════════════════ CREATE AUTH ═════════════════════════════════════

    @Override
    public void createAuthForPerson(PersonalInformation person,
                                    RoleType role,
                                    String rawPassword) {

        String username = resolveUsername(person);

        if (authRepository.existsByUsername(username)) {
            throw new InvalidRequestException(
                    "An account with this email already exists.");
        }

        UserAuth auth = UserAuth.builder()
                .personalInformation(person)
                .username(username)
                .passwordHash(passwordEncoder.encode(rawPassword))
                .role(role)
                .active(true)
                .accountLocked(false)
                .failedLoginAttempts(0)
                .isLoggedIn(false)
                .build();

        authRepository.save(auth);

        // Notify user of their credentials
        emailService.sendCredentials(username, person.getFirstName(), rawPassword);
    }

    @Override
    public boolean existsByPerson(PersonalInformation person) {
        return authRepository.existsByPersonalInformation(person);
    }

    // ═══════════════════════════ LOGIN ═══════════════════════════════════════

    @Override
    public LoginResponseDTO login(LoginRequestDTO request) {

        //  Normalise so case differences never cause BadCredentials
        String username = request.getUsername().trim().toLowerCase();

        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(username, request.getPassword())
            );
        } catch (DisabledException ex) {
            throw new InvalidRequestException("Your account is inactive. Please contact HR.");
        } catch (BadCredentialsException ex) {
            throw new InvalidRequestException("Invalid email or password. Please try again.");
        }

        UserAuth auth = authRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (Boolean.FALSE.equals(auth.getActive())) {
            throw new InvalidRequestException(
                    "Your account is inactive. Please contact HR.");
        }

        if (Boolean.TRUE.equals(auth.getAccountLocked())) {
            throw new InvalidRequestException(
                    "Your account is locked. Please contact the administrator.");
        }

        String accessToken  = jwtService.generateToken(auth.getUsername(), auth.getRole().name());
        String refreshToken = jwtService.generateRefreshToken(auth.getUsername());

        refreshTokenService.deleteByAuth(auth);
        refreshTokenService.create(auth, refreshToken);

        auth.setIsLoggedIn(true);
        auth.setLastLoginAt(LocalDateTime.now());
        auth.setFailedLoginAttempts(0);

        return LoginMapper.toResponse(accessToken, refreshToken, auth);
    }

    // ══════════════════════ REFRESH TOKEN ════════════════════════════════════

    @Override
    public LoginResponseDTO refreshToken(String refreshToken) {

        var refreshEntity = refreshTokenService.verify(refreshToken);
        UserAuth auth     = refreshEntity.getUserAuth();

//        if (Boolean.FALSE.equals(auth.getIsLoggedIn())) {
//            throw new InvalidRequestException(
//                    "Session expired. Please log in again.");
//        }

        String newAccessToken  = jwtService.generateToken(auth.getUsername(), auth.getRole().name());
        String newRefreshToken = jwtService.generateRefreshToken(auth.getUsername());

        refreshTokenService.deleteByAuth(auth);
        refreshTokenService.create(auth, newRefreshToken);

        auth.setIsLoggedIn(true);

        return LoginMapper.toResponse(newAccessToken, newRefreshToken, auth);
    }

    // ═══════════════════════════ LOGOUT ══════════════════════════════════════

    @Override
    public void logout(String refreshToken) {

        var refreshEntity = refreshTokenService.verify(refreshToken);
        UserAuth auth     = refreshEntity.getUserAuth();

        auth.setIsLoggedIn(false);
        refreshTokenService.deleteByAuth(auth);
    }

    // ══════════════════════ CHANGE PASSWORD ══════════════════════════════════

    @Override
    public void changePassword(ChangePasswordRequestDTO request) {

        String username = SecurityContextHolder
                .getContext()
                .getAuthentication()
                .getName();

        UserAuth auth = authRepository.findByUsername(username)
                .orElseThrow(() -> new ResourceNotFoundException("User not found."));

        if (!passwordEncoder.matches(request.getOldPassword(), auth.getPasswordHash())) {
            throw new InvalidRequestException(
                    "The current password you entered is incorrect.");
        }

        if (request.getNewPassword().length() < 8) {
            throw new InvalidRequestException(
                    "New password must be at least 8 characters.");
        }

        if (passwordEncoder.matches(request.getNewPassword(), auth.getPasswordHash())) {
            throw new InvalidRequestException(
                    "New password must be different from the current password.");
        }

        auth.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));

        // Invalidate all active refresh tokens — force re-login on all devices
        refreshTokenService.deleteByAuth(auth);
    }

    // ═══════════════════════ RESET PASSWORD ══════════════════════════════════

    @Override
    public void resetPassword(ResetPasswordRequestDTO request) {

        UserAuth auth = authRepository.findByUsername(request.getEmail())
                .orElseThrow(() ->
                        new ResourceNotFoundException(
                                "No account found for this email address."));

        if (request.getNewPassword().length() < 8) {
            throw new InvalidRequestException(
                    "Password must be at least 8 characters.");
        }

        auth.setPasswordHash(passwordEncoder.encode(request.getNewPassword()));
        auth.setIsLoggedIn(false);

        // Invalidate all refresh tokens after password reset
        refreshTokenService.deleteByAuth(auth);
    }

    // ══════════════════════ USERNAME RESOLVER ═════════════════════════════════

    private String resolveUsername(PersonalInformation person) {

        if (person.getContact() != null) {
            String office   = person.getContact().getOfficeEmail();
            String personal = person.getContact().getPersonalEmail();
            if (office != null && !office.isBlank())   return office;
            if (personal != null && !personal.isBlank()) return personal;
        }
        throw new InvalidRequestException(
                "No valid email found for this person.");
    }
}