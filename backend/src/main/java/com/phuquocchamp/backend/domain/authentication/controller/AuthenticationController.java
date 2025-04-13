package com.phuquocchamp.backend.domain.authentication.controller;

import java.io.UnsupportedEncodingException;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestAttribute;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.phuquocchamp.backend.domain.authentication.dto.AuthenticationRequest;
import com.phuquocchamp.backend.domain.authentication.dto.AuthenticationResponse;
import com.phuquocchamp.backend.domain.authentication.model.AuthUser;
import com.phuquocchamp.backend.domain.authentication.service.AuthenticationService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;

@RestController
@RequestMapping("/api/v1/auth")
public class AuthenticationController {
    private final AuthenticationService authenticationService;

    public AuthenticationController(AuthenticationService authenticationService) {
        this.authenticationService = authenticationService;
    }


    @GetMapping("/user")
    public AuthUser getUser(@RequestAttribute("authenticated-user") AuthUser authUser) {
        return authenticationService.getUser(authUser.getEmail());
    }

    @PostMapping("/register")
    public ResponseEntity<AuthenticationResponse> register(@RequestBody @Valid AuthenticationRequest authenticationRequest) throws MessagingException, UnsupportedEncodingException {
        return new ResponseEntity<>(authenticationService.register(authenticationRequest), HttpStatus.CREATED);
    }

    @PostMapping("/login")
    public ResponseEntity<AuthenticationResponse> login(@RequestBody @Valid AuthenticationRequest authenticationRequest) {
        return new ResponseEntity<>(authenticationService.login(authenticationRequest), HttpStatus.OK);
    }

    @GetMapping("/send-email-verification-token")
    public ResponseEntity<String> sendEmailVerificationToken(@RequestAttribute("authenticated-user") AuthUser authUser) {
        authenticationService.sendEmailVerificationToken(authUser.getEmail());
        return new ResponseEntity<>("Email verification token sent successfully", HttpStatus.OK);
    }

    @PutMapping("/validate-email-verification-token")
    public ResponseEntity<String> validateEmailVerificationToken(@RequestParam String token, @RequestAttribute("authenticated-user") AuthUser authUser) {
        authenticationService.validateEmailVerificationToken(token, authUser.getEmail());
        return new ResponseEntity<>("Email verification token validated successfully", HttpStatus.OK);
    }

    @PutMapping("/send-password-reset-token")
    public ResponseEntity<String> sendPasswordResetToken(@RequestParam String email) {
        authenticationService.sendPasswordResetToken(email);
        return new ResponseEntity<>("Password reset token sent successfully", HttpStatus.OK);
    }

    @PutMapping("/reset-password")
    public ResponseEntity<String> resetPassword(@RequestParam String email, @RequestParam String newPassword, @RequestParam String token) {
        authenticationService.resetPassword(email, newPassword, token);
        return new ResponseEntity<>("Password reset successfully", HttpStatus.OK);
    }
}
