package com.example.picbooker.security;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import com.example.picbooker.user.User;
import com.example.picbooker.user.UserRepository;

@Service
// @AllArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not exists by Username or Email" + usernameOrEmail));

        return user;

        // if user doesn't implement userDetails use this
        // return new org.springframework.security.core.userdetails.User(
        // usernameOrEmail,
        // user.getPassword(),
        // new ArrayList<>());
    }
}