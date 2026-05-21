package com.e_commerce.project.security.services;

import com.e_commerce.project.model.User;
import com.e_commerce.project.repository.UserRepository;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;


@Service
public class UserDetailsServiceImpl implements UserDetailsService {

    private final UserRepository userRepository;

    public UserDetailsServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    @Transactional
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

       User user= userRepository.findByUserName(username)
                .orElseThrow(()->new UsernameNotFoundException("User not found wit username: " + username));
        System.out.println("DB USERNAME: " + user.getUserName());
        System.out.println("DB EMAIL: " + user.getEmail());
        System.out.println("DB PASSWORD: " + user.getPassword());
        return UserDetailsImpl.build(user);
    }
}
