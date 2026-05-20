package com.e_commerce.project.controller;

import com.e_commerce.project.model.Role;
import com.e_commerce.project.model.User;
import com.e_commerce.project.repository.UserRepository;
import com.e_commerce.project.security.request.SignUpRequest;
import com.e_commerce.project.security.response.LoginResponse;
import com.e_commerce.project.security.jwt.JwtUtils;
import com.e_commerce.project.security.request.LoginRequest;
import com.e_commerce.project.security.response.MessageResponse;
import com.e_commerce.project.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.*;

@RestController
@RequestMapping("/api")
public class AuthController {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtUtils jwtUtils, AuthenticationManager authenticationManager, UserRepository userRepository, PasswordEncoder passwordEncoder) {
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.passwordEncoder = passwordEncoder;
    }

    @PostMapping("/signIn")
    public ResponseEntity<?> authenticationUser(@RequestBody LoginRequest loginRequest){

        Authentication authentication;
        try {

                authentication=authenticationManager
                        .authenticate(new UsernamePasswordAuthenticationToken(
                                loginRequest.getUserName(),
                                loginRequest.getPassword()
                        ));
        }catch (AuthenticationException authenticationException){
            Map<String, Object> map=new HashMap<>();
            map.put("message","Bad Credentials");
            map.put("status",false);
            return new ResponseEntity<>(map, HttpStatus.NOT_FOUND);

        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails= (UserDetailsImpl) authentication.getPrincipal();
        String token = jwtUtils.generateTokenFromUserName(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(
                GrantedAuthority::getAuthority
        ).toList();

        LoginResponse loginResponse=new LoginResponse(userDetails.getId(), token,userDetails.getUsername(),roles);

        return new ResponseEntity<>(loginResponse,HttpStatus.OK);

    }

    @PostMapping("/signUp")
    public ResponseEntity<?> registerUser(@Valid @RequestBody SignUpRequest signUpRequest){

        if (userRepository.existsByUserName(signUpRequest.getUserName())){
            return new ResponseEntity<>(new MessageResponse("Error: Username is already exist."),HttpStatus.BAD_REQUEST);
        }

        if (userRepository.existsByEmail(signUpRequest.getEmail())){
            return new ResponseEntity<>(new MessageResponse("Error: Email is already exist."),HttpStatus.BAD_REQUEST);
        }
        User user=new User(
                signUpRequest.getUserName(),
                signUpRequest.getEmail(),
              passwordEncoder.encode(signUpRequest.getPassword())
        );
        Set<String> strRoles=signUpRequest.getRoles();
        Set<Role> roles=new HashSet<>();

    }
}
