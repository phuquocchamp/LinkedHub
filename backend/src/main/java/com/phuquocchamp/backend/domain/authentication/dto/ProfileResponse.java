package com.phuquocchamp.backend.domain.authentication.dto;

import lombok.Data;

@Data
public class ProfileResponse {
    private Long id;
    private String email;
    private String firstName;
    private String lastName;
    private String company;
    private String position;
    private String location;
}
