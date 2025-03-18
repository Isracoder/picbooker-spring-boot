package com.example.picbooker.user;

import static java.util.Objects.isNull;

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
import com.example.picbooker.client.Client;
import com.example.picbooker.photographer.Photographer;
import com.example.picbooker.security.SecurityConfig;

@Service
public class UserService {

    @Autowired
    private UserRepository userRepository;

    public User findByIdThrow(Long userId) {
        return userRepository.findById(userId)
                .orElseThrow(() -> new ApiException(HttpStatus.NOT_FOUND, "User not found"));

    }

    public UserResponse findUserResponseById(Long id) {
        UserResponse userResponse = UserMapper.toResponse(findByIdThrow(id));
        return userResponse;
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

    public void changePassword(PasswordChangeDTO passwordChangeDTO, User user) {

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

    public boolean checkEmailOwnership(String email, Long userId) {
        return userRepository.emailIsForUserId(email, userId);
    }

    public User saveOauthUser(UserRequest userRequest) {
        // change to user oauth request for profile pic or add it as field
        User user = UserMapper.toEntity(userRequest); // can add type of oauth provider to users
        user.setIsEmailVerified(true);
        return userRepository.save(user);
    }

    public UserDetails loadUserByEmail(String usernameOrEmail) throws UsernameNotFoundException {

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

    @Transactional
    public UserResponse updateAccountById(Long id, UserRequest userRequest) {

        if (isNull(id) || !userRepository.existsById(id)) {
            throw new IllegalArgumentException("User ID must be set and valid for update.");
        }
        User actualUser = findByIdThrow(id);
        return (save(UserMapper.merge(userRequest, actualUser)));
    }

    // @Transactional could not commit jpa transaction
    public UserResponse updateAccount(User user, UserRequest userRequest) {

        User updatedUser = UserMapper.merge(userRequest, user);
        return UserMapper.toResponse(userRepository.saveAndFlush(updatedUser));
    }

    public static User getLoggedInUserThrow() {
        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        if (authentication == null || !authentication.isAuthenticated()) {
            throw new ApiException(HttpStatus.UNAUTHORIZED, "User not authenticated");
        }
        return (User) authentication.getPrincipal();
    }

    public static Photographer getPhotographerFromUserThrow(User user) {
        Photographer photographer = user.getPhotographer();
        if (photographer == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Photographer not found. User not photographer.");
        }
        return photographer;
    }

    public static Client getClientFromUserThrow(User user) {
        Client client = user.getClient();
        if (client == null) {
            throw new ApiException(HttpStatus.NOT_FOUND, "Client not found. User not client.");
        }
        return client;
    }

    @Transactional
    public void updateAccessToken(String refreshToken, String accessToken, LocalDateTime expiresAt) {
        // to think of returning number of modified rows to check if anything was
        // changed
        userRepository.updateAccessToken(refreshToken, accessToken, expiresAt);

    }

}
