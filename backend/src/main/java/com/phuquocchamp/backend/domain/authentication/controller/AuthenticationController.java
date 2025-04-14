package com.phuquocchamp.backend.domain.authentication.controller;

import java.io.UnsupportedEncodingException;

import com.phuquocchamp.backend.domain.authentication.dto.ProfileResponse;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.phuquocchamp.backend.domain.authentication.dto.AuthenticationRequest;
import com.phuquocchamp.backend.domain.authentication.dto.AuthenticationResponse;
import com.phuquocchamp.backend.domain.authentication.model.AuthUser;
import com.phuquocchamp.backend.domain.authentication.service.AuthenticationService;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import org.springframework.web.server.ResponseStatusException;

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

    @PutMapping("/profile/{id}")
    public ResponseEntity<ProfileResponse> updateProfile(
        @PathVariable Long id,
        @RequestAttribute("authenticated-user") AuthUser authUser,
        @RequestParam(required = false) String firstName,
        @RequestParam(required = false) String lastName,
        @RequestParam(required = false) String company,
        @RequestParam(required = false) String position,
        @RequestParam(required = false) String location
    ) {
        if(!authUser.getId().equals(id)) {
            throw new ResponseStatusException(HttpStatus.FORBIDDEN, "User does not have permission to update this profile");
        }
        return new ResponseEntity<>(authenticationService.updateUserProfile(id, firstName, lastName, company, position, location), HttpStatus.OK);
    }
}
