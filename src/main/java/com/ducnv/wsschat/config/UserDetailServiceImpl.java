package com.ducnv.wsschat.config;

import java.util.Collection;
import java.util.Collections;

import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;

import com.ducnv.wsschat.entity.User;
import com.ducnv.wsschat.repository.UserRepository;

@Component("userDetailService")
public class UserDetailServiceImpl implements UserDetailsService{
    private final UserRepository userRepository;

    public UserDetailServiceImpl(UserRepository userRepository) {
        this.userRepository = userRepository;
    }

    @Override
    public CustomUserDetails loadUserByUsername(String username) {
        User currentUser = this.userRepository.findByUsernameAndDeletedAtIsNull(username).orElseThrow(() -> 
            new IllegalArgumentException("User not found")
        );

        return CustomUserDetails.builder()
            .id(currentUser.getId())
            .email(currentUser.getEmail())
            .password(currentUser.getPassword())
            .authorities(Collections.singletonList(
                new SimpleGrantedAuthority("ROLE_USER")
            ))
            .build();
    }
}
