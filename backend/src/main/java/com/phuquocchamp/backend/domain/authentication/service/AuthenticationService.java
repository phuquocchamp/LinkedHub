package com.phuquocchamp.backend.domain.authentication.service;

import com.phuquocchamp.backend.domain.authentication.dto.AuthenticationRequest;
import com.phuquocchamp.backend.domain.authentication.dto.AuthenticationResponse;
import com.phuquocchamp.backend.domain.authentication.model.AuthUser;
import com.phuquocchamp.backend.domain.authentication.repository.AuthenticationRepository;
import com.phuquocchamp.backend.domain.authentication.utils.EmailService;
import com.phuquocchamp.backend.domain.authentication.utils.Encoder;
import com.phuquocchamp.backend.domain.authentication.utils.JsonWebToken;
import jakarta.mail.MessagingException;
import org.antlr.v4.runtime.Token;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Service;

import java.io.UnsupportedEncodingException;
import java.security.SecureRandom;
import java.time.LocalDateTime;
import java.util.Optional;

@Service
public class AuthenticationService {
    private static final int TOKEN_DURATION_MINUTES = 1;
    private static final int TOKEN_LENGTH = 5;
    private static final Logger LOGGER = LoggerFactory.getLogger(AuthenticationService.class);


    private final Encoder encoder;
    private final JsonWebToken jsonWebToken;
    private final EmailService emailService;
    private final AuthenticationRepository authenticationRepository;

    private enum TokenType {
        EMAIL_VERIFICATION,
        PASSWORD_RESET
    }

    public AuthenticationService(Encoder encoder, JsonWebToken jsonWebToken, EmailService emailService, AuthenticationRepository authenticationRepository) {
        this.encoder = encoder;
        this.jsonWebToken = jsonWebToken;
        this.emailService = emailService;
        this.authenticationRepository = authenticationRepository;
    }

    public static String generateRandomToken() {
        SecureRandom random = new SecureRandom();
        StringBuilder token = new StringBuilder(TOKEN_LENGTH);
        for (int i = 0; i < TOKEN_LENGTH; i++) {
            token.append(random.nextInt(10));
        }
        return token.toString();
    }
    public void sendEmailVerificationToken(String email) {
        AuthUser user = findUserByEmail(email);
        if(user.getEmailVerified()){
            throw new IllegalArgumentException("Email already verified");
        }

        String token = generateRandomToken();
        updateUserWithToken(user, token, TokenType.EMAIL_VERIFICATION);
        authenticationRepository.save(user);

        sendEmail(email,"Email Verification", buildVerificationEmailBody(token));
    }
    public void validateEmailVerificationToken(String token, String email) {
        AuthUser user = findUserByEmail(email);
        validateToken(token, user.getEmailVerificationToken(), user.getEmailVerificationTokenExpiryDate());
        user.setEmailVerified(true);
        user.setEmailVerificationToken(null);
        user.setEmailVerificationTokenExpiryDate(null);
        authenticationRepository.save(user);
    }
    public AuthenticationResponse register(AuthenticationRequest authenticationRequest) throws MessagingException, UnsupportedEncodingException {
        AuthUser user = authenticationRepository.save(new AuthUser(authenticationRequest.getEmail(), encoder.encode(authenticationRequest.getPassword())));

        String token = generateRandomToken();
        updateUserWithToken(user, token, TokenType.EMAIL_VERIFICATION);
        authenticationRepository.save(user);
        sendEmail(user.getEmail(), "Password Reset", buildPasswordResetEmailBody(token));

        String authToken = jsonWebToken.generateToken(authenticationRequest.getEmail());
        return new AuthenticationResponse(authToken, "User created successfully");
    }
    public AuthenticationResponse login(AuthenticationRequest authenticationRequest) {
        AuthUser user = findUserByEmail(authenticationRequest.getEmail());
        if (!encoder.matches(authenticationRequest.getPassword(), user.getPassword())) {
            throw new IllegalArgumentException("Incorrect password");
        }

        String token = jsonWebToken.generateToken(authenticationRequest.getEmail());
        return new AuthenticationResponse(token, "Authentication succeeded");
    }
    public AuthUser getUser(String email) {
        return authenticationRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException(("User not found")));
    }
    public void sendPasswordResetToken(String email) {
        AuthUser user = findUserByEmail(email);
        String token = generateRandomToken();
        updateUserWithToken(user, token, TokenType.PASSWORD_RESET);

        authenticationRepository.save(user);
        sendEmail(user.getEmail(), "Password Reset", buildPasswordResetEmailBody(token));
    }
    public void resetPassword(String email, String newPassword, String token) {
        AuthUser user = findUserByEmail(email);
        validateToken(token, user.getPasswordResetToken(), user.getPasswordResetTokenExpiryDate());

        user.setPasswordResetToken(null);
        user.setPasswordResetTokenExpiryDate(null);
        user.setPassword(encoder.encode(newPassword));
        authenticationRepository.save(user);
    }
    private AuthUser findUserByEmail(String email) {
        return authenticationRepository.findByEmail(email).orElseThrow(() -> new IllegalArgumentException("User not found"));
    }
    private void updateUserWithToken(AuthUser user, String token, TokenType tokenType) {
        String hashedToken = encoder.encode(token);
        LocalDateTime expiryDate = LocalDateTime.now().plusMinutes(TOKEN_DURATION_MINUTES);

        if(tokenType == TokenType.EMAIL_VERIFICATION){
            user.setEmailVerificationToken(hashedToken);
            user.setEmailVerificationTokenExpiryDate(expiryDate);
        }else if(tokenType == TokenType.PASSWORD_RESET){
            user.setPasswordResetToken(hashedToken);
            user.setPasswordResetTokenExpiryDate(expiryDate);
        }
    }
    private void validateToken(String rawToken, String storedToken, LocalDateTime expiryDate) {
        if(!encoder.matches(rawToken, storedToken)) {
            throw new IllegalArgumentException("Invalid token");
        }
        if(expiryDate.isBefore(LocalDateTime.now())) {
            throw new IllegalArgumentException("Token is expired");
        }
    }
    private String buildVerificationEmailBody(String token) {
        return String.format("""
                Only one step to take full advantage of LinkedHub.
                
                Enter this code to verify your email: %s. The code will expire in %s minutes.""",
                token, TOKEN_DURATION_MINUTES);
    }
    private String buildPasswordResetEmailBody(String token) {
        return String.format("""
                You requested a password reset.
                
                Enter this code to reset your password: %s. The code will expire in %s minutes.""",
                token, TOKEN_DURATION_MINUTES);
    }
    private void sendEmail(String to, String subject, String body) {
        try{
            emailService.sendEmail(to, subject, body);
        }catch (Exception e){
            LOGGER.info("Failed to send email to: {}: {}", to, e.getMessage());
        }
    }
}
