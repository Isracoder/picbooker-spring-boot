package com.example.picbooker.user;

import java.time.LocalDateTime;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpStatus;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import com.example.picbooker.ApiException;
import com.example.picbooker.security.SecurityConfig;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findById(Long userId) {
        Optional<User> opt = userRepository.findById(userId);
        if (!opt.isPresent())
            throw new RuntimeException("User not found");
        return opt.get();
    }

    public UserResponse findUserResponseById(Long id) {
        UserResponse userres = UserMapper.toResponse(findById(id));
        return userres;
    }

    public User findByUsername(String username) {
        Optional<User> opt = userRepository.findByUsername(username);
        if (!opt.isPresent())
            throw new RuntimeException("User not found");
        return opt.get();
    }

    public User findByEmail(String email) {
        Optional<User> opt = userRepository.findByEmail(email);
        // if (!opt.isPresent())
        // throw new RuntimeException("User not found");
        // return opt.get();
        return opt.isPresent() ? opt.get() : null;
    }

    // replace by static function to encrypt
    public void updatePassword(User user, String password) {
        user.setPassword(new BCryptPasswordEncoder().encode(password));
        System.out.println("password updated");
        userRepository.save(user);
    }

    public void changePassword(PasswordChangeDTO passwordChangeDTO) {
        // verify user identity
        User user = findByEmail(passwordChangeDTO.getEmail());
        if (user == null)
            throw new ApiException(HttpStatus.BAD_REQUEST, "No user");
        if (!SecurityConfig.passwordEncoder().matches(passwordChangeDTO.getOldPassword(), user.getPassword())) {
            throw new RuntimeException("Invalid credentials.");
        }
        updatePassword(user, passwordChangeDTO.getNewPassword());

    }

    public UserResponse createAndSave(UserRequest userRequest) {
        User user = UserMapper.toEntity(userRequest);
        user.setPassword(new BCryptPasswordEncoder().encode(user.getPassword()));
        return save(user);
    }

    public UserResponse save(User user) {
        return UserMapper.toResponse(userRepository.save(user));
    }

    public User saveOauthUser(UserRequest userRequest) {
        // change to user oauth request for profile pic or add it as field
        User user = UserMapper.toEntity(userRequest); // can add type of oauth provider to users
        user.setIsEnabled(true);
        return userRepository.save(user);
    }

    public UserDetails loadUserByUsername(String usernameOrEmail) throws UsernameNotFoundException {

        User user = userRepository.findByUsernameOrEmail(usernameOrEmail, usernameOrEmail)
                .orElseThrow(() -> new UsernameNotFoundException("User not exists by Username or Email"));
        return new org.springframework.security.core.userdetails.User(usernameOrEmail, user.getPassword(), null);

    }

    public Page<UserResponse> findAll(Pageable page) {
        Page<User> userPages = userRepository.findAll(page);
        return UserMapper.toResponsePage(userPages);

    }

    public void delete(long id) {
        userRepository.deleteById(id);
    }

    public UserResponse update(UserRequest userRequest, Long id) {
        User userWithUpdates = UserMapper.toEntity(userRequest);
        userWithUpdates.setId(id);
        if (userWithUpdates.getId() == null || !userRepository.existsById(userWithUpdates.getId())) {
            throw new IllegalArgumentException("User ID must be set and valid for update.");
        }
        User actualUser = findById(userWithUpdates.getId());
        return UserMapper.toResponse((UserMapper.merge(userWithUpdates, actualUser)));
    }

    public static User getLoggedInUser() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        return (User) authentication.getPrincipal();
    }

    @Transactional
    public void updateAccessToken(String refreshToken, String accessToken, LocalDateTime expiresAt) {
        // to think of returning number of modified rows to check if anything was
        // changed
        userRepository.updateAccessToken(refreshToken, accessToken, expiresAt);

    }

}
