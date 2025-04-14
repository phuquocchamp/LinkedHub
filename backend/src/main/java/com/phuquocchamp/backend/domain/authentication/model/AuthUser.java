package com.phuquocchamp.backend.domain.authentication.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import java.time.LocalDateTime;

@Entity
@Table(name = "user")
@Getter @Setter
@NoArgsConstructor
@AllArgsConstructor
public class AuthUser {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;
    @Column(unique = true)
    private String email;
    private Boolean emailVerified = false;
    private String emailVerificationToken;
    private LocalDateTime emailVerificationTokenExpiryDate = null;

    @JsonIgnore
    private String password;
    private String passwordResetToken;
    private LocalDateTime passwordResetTokenExpiryDate = null;

    private String firstName;
    private String lastName;
    private String company;
    private String position;
    private String location;

    private Boolean profileComplete = false;

    public AuthUser(String email, String password) {
        this.email = email;
        this.password = password;
    }

    @PreUpdate()
    private void updateProfileCompleteStatus(){
        this.profileComplete = (
            this.firstName != null && this.firstName.trim().isEmpty()
            && this.lastName != null && this.lastName.trim().isEmpty()
            && this.company != null && this.company.trim().isEmpty()
            && this.position != null && this.position.trim().isEmpty()
            && this.location != null && this.location.trim().isEmpty()
        );
    }
}
