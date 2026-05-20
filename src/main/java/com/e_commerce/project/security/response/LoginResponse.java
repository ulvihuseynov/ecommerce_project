package com.e_commerce.project.security.response;

import lombok.*;

import java.util.List;

@Data
@AllArgsConstructor
@NoArgsConstructor
public class LoginResponse {

    private Long id;
    private String jwtToken;
    private String userName;
    private List<String> roles;


    public LoginResponse(String jwtToken, String userName, List<String> roles) {
        this.jwtToken = jwtToken;
        this.userName = userName;
        this.roles = roles;
    }
}
