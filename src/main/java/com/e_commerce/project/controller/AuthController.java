package com.e_commerce.project.controller;

import com.e_commerce.project.model.AppRole;
import com.e_commerce.project.model.Role;
import com.e_commerce.project.model.User;
import com.e_commerce.project.repository.RoleRepository;
import com.e_commerce.project.repository.UserRepository;
import com.e_commerce.project.security.request.SignUpRequest;
import com.e_commerce.project.security.response.LoginResponse;
import com.e_commerce.project.security.jwt.JwtUtils;
import com.e_commerce.project.security.request.LoginRequest;
import com.e_commerce.project.security.response.MessageResponse;
import com.e_commerce.project.security.services.UserDetailsImpl;
import jakarta.validation.Valid;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseCookie;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.*;

import java.util.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {

    private final JwtUtils jwtUtils;
    private final AuthenticationManager authenticationManager;
    private final UserRepository userRepository;
    private final RoleRepository roleRepository;
    private final PasswordEncoder passwordEncoder;

    public AuthController(JwtUtils jwtUtils, AuthenticationManager authenticationManager, UserRepository userRepository, RoleRepository roleRepository, PasswordEncoder passwordEncoder) {
        this.jwtUtils = jwtUtils;
        this.authenticationManager = authenticationManager;
        this.userRepository = userRepository;
        this.roleRepository = roleRepository;
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
            return new ResponseEntity<>(map, HttpStatus.UNAUTHORIZED);

        }
        SecurityContextHolder.getContext().setAuthentication(authentication);
        UserDetailsImpl userDetails= (UserDetailsImpl) authentication.getPrincipal();
        ResponseCookie jwtCookie = jwtUtils.generateJwtFromCookie(userDetails);

        List<String> roles = userDetails.getAuthorities().stream().map(
                GrantedAuthority::getAuthority
        ).toList();

        LoginResponse loginResponse=new LoginResponse(userDetails.getId(), jwtCookie.toString(),userDetails.getUsername(),roles);

        return  ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,jwtCookie.toString()).body(
                loginResponse
        );

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


        if (strRoles==null){
            Role role=roleRepository.findByRoleName(AppRole.ROLE_USER)
                    .orElseThrow(()->new RuntimeException("Error: Role not found. "));
            roles.add(role);
        }else {
            strRoles.forEach(role->{
                switch (role){
                    case "admin":
                        Role adminRole=roleRepository.findByRoleName(AppRole.ROLE_ADMIN)
                                .orElseThrow(()->new RuntimeException("Error: Role not found. "));
                        roles.add(adminRole);
                        break;
                    case "seller":
                        Role sellerRole=roleRepository.findByRoleName(AppRole.ROLE_SELLER)
                                .orElseThrow(()->new RuntimeException("Error: Role not found. "));
                        roles.add(sellerRole);
                        break;
                    default:
                        Role userRole=roleRepository.findByRoleName(AppRole.ROLE_USER)
                                .orElseThrow(()->new RuntimeException("Error: Role not found. "));
                        roles.add(userRole);
                }
            });

        }
        user.setRoles(roles);
        userRepository.save(user);
        return new ResponseEntity<>(new MessageResponse("User register successfully"),HttpStatus.OK);
    }


    @GetMapping("/userName")
    public String currentUserName(Authentication authentication){

                if(authentication.getName()!=null){

                    return authentication.getName();
                }else{
                    return null;
                }

    }

    @GetMapping("/user")
    public ResponseEntity<?> getUserDetails(Authentication authentication){

            UserDetailsImpl userDetails = (UserDetailsImpl) authentication.getPrincipal();
        List<String> roles = userDetails.getAuthorities().stream().map(
                GrantedAuthority::getAuthority
        ).toList();
        LoginResponse loginResponse=new LoginResponse(userDetails.getId(),null,userDetails.getUsername(),roles);

        return ResponseEntity.ok().body(loginResponse);
    }

    @PostMapping("/signOut")
    public ResponseEntity<?> signOutUser(){

        ResponseCookie cookie = jwtUtils.getCleanJwtCookie();

        return ResponseEntity.ok()
                .header(HttpHeaders.SET_COOKIE,cookie.toString())
                .body(new MessageResponse("You've been signed out."));
    }
}
