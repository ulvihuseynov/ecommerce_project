package com.e_commerce.project.security.jwt;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private String jwtToken;
    private String userName;
    private List<String> roles;


}
