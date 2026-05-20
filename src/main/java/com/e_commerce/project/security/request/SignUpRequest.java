package com.e_commerce.project.security.request;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.util.Set;

@Data
@NoArgsConstructor
@AllArgsConstructor
public class SignUpRequest {

    @NotBlank
    @Size(min=3,max=20)
    private String userName;

    @NotBlank
    @Size(max=50)
    @Email
    private String email;


    private Set<String > roles;

    @NotBlank
    @Size(min=3,max=20)
    private String password;
}
