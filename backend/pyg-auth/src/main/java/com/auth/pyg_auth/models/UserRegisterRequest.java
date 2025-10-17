package com.auth.pyg_auth.models;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import java.util.List;

@Data
@Builder
@NoArgsConstructor
@AllArgsConstructor
public class UserRegisterRequest {
    String username;
    String password;
    String firstname;
    String lastname;
    String country;
    String rolename;
    List<PetProfile> pets;
}