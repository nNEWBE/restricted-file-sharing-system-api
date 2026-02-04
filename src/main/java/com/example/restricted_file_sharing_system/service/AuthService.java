package com.example.restricted_file_sharing_system.service;

import com.example.restricted_file_sharing_system.dto.AuthResponse;
import com.example.restricted_file_sharing_system.dto.LoginRequest;
import com.example.restricted_file_sharing_system.dto.MessageResponse;
import com.example.restricted_file_sharing_system.dto.RegisterRequest;
import com.example.restricted_file_sharing_system.entity.User;
import com.example.restricted_file_sharing_system.exception.EmailAlreadyExistsException;
import com.example.restricted_file_sharing_system.exception.EmailNotVerifiedException;
import com.example.restricted_file_sharing_system.exception.InvalidVerificationTokenException;
import com.example.restricted_file_sharing_system.exception.UserNotFoundException;
import com.example.restricted_file_sharing_system.repository.UserRepository;
import com.example.restricted_file_sharing_system.security.JwtTokenProvider;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

@Service
@RequiredArgsConstructor
@Slf4j
public class AuthService {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final AuthenticationManager authenticationManager;
    private final JwtTokenProvider jwtTokenProvider;
    private final EmailService emailService;

    @Value("${app.verification.expiry-minutes:10}")
    private int verificationExpiryMinutes;

    @Value("${app.verification.base-url:http://localhost:8080}")
    private String baseUrl;

    @Transactional
    public MessageResponse register(RegisterRequest request) {
        if (userRepository.existsByEmail(request.getEmail())) {
            throw new EmailAlreadyExistsException("Email address is already in use");
        }

        String token = UUID.randomUUID().toString();
        LocalDateTime tokenExpiry = LocalDateTime.now().plusMinutes(verificationExpiryMinutes);

        User user = User.builder()
                .email(request.getEmail())
                .password(passwordEncoder.encode(request.getPassword()))
                .verificationToken(token)
                .verificationTokenExpiry(tokenExpiry)
                .emailVerified(false)
                .build();

        userRepository.save(user);

        String verificationLink = baseUrl + "/api/auth/verify?token=" + token;
        emailService.sendVerificationEmail(request.getEmail(), verificationLink);

        log.info("User registered: {}. Verification email sent.", request.getEmail());

        return MessageResponse.builder()
                .message("User registered successfully. Please check your email to verify your account.")
                .success(true)
                .build();
    }

    public AuthResponse login(LoginRequest request) {
        User user = userRepository.findByEmail(request.getEmail())
                .orElseThrow(() -> new UserNotFoundException("User not found"));

        if (!user.getEmailVerified()) {
            throw new EmailNotVerifiedException("Email is not verified. Please verify your email first.");
        }

        Authentication authentication = authenticationManager.authenticate(
                new UsernamePasswordAuthenticationToken(request.getEmail(), request.getPassword()));

        String token = jwtTokenProvider.generateToken(authentication);

        log.info("User logged in successfully: {}", request.getEmail());

        return AuthResponse.builder()
                .accessToken(token)
                .email(user.getEmail())
                .message("Login successful")
                .build();
    }

    @Transactional
    public MessageResponse verifyEmail(String token) {
        User user = userRepository.findByVerificationToken(token)
                .orElseThrow(() -> new InvalidVerificationTokenException("Invalid verification token"));

        if (!user.isVerificationTokenValid()) {
            throw new InvalidVerificationTokenException("Verification token has expired");
        }

        user.setEmailVerified(true);
        user.setVerificationToken(null);
        user.setVerificationTokenExpiry(null);

        userRepository.save(user);

        log.info("Email verified for user: {}", user.getEmail());

        return MessageResponse.builder()
                .message("Email verified successfully. You can now login.")
                .success(true)
                .build();
    }
}
