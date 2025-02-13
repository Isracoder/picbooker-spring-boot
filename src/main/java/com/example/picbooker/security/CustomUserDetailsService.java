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
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {

        User user = userRepository.findByUsernameOrEmail(username, username)
                .orElseThrow(() -> new UsernameNotFoundException(
                        "User not exists by Username:" + username + " or Email: " + username));

        return user;

        // if user doesn't implement userDetails use this
        // return new org.springframework.security.core.userdetails.User(
        // usernameOrEmail,
        // user.getPassword(),
        // new ArrayList<>());
    }
}