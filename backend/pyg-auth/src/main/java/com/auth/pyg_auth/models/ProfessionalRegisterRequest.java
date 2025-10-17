package com.auth.pyg_auth.models;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class ProfessionalRegisterRequest {
    String userName;
    String password;
    String firstName;
    String lastName;
    String country;
    String roleName;
    Integer yearsExperience;
}