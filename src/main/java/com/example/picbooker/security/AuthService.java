package com.example.picbooker.security;

import static java.util.Objects.isNull;

import java.time.LocalDateTime;
import java.util.HashMap;
import java.util.Map;
import java.util.UUID;

// import org.json.JSONObject;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Service;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import com.example.picbooker.ApiException;
import com.example.picbooker.security.OauthToken.OauthProviderType;
import com.example.picbooker.security.passwordReset.PasswordResetDTO;
import com.example.picbooker.security.passwordReset.PasswordResetService;
import com.example.picbooker.security.passwordReset.PasswordResetToken;
import com.example.picbooker.system_message.EmailService;
import com.example.picbooker.user.User;
import com.example.picbooker.user.UserMapper;
import com.example.picbooker.user.UserOTP;
import com.example.picbooker.user.UserRequest;
import com.example.picbooker.user.UserResponse;
import com.example.picbooker.user.UserService;
import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import jakarta.mail.MessagingException;
import jakarta.transaction.Transactional;
import lombok.AllArgsConstructor;
import lombok.NoArgsConstructor;
import net.minidev.json.JSONObject;

@AllArgsConstructor
@NoArgsConstructor
@Service
public class AuthService {

    @Value("${app.code-expiry-minutes}")
    private Long codeExpiryMinutes;

    @Value("${spring.security.oauth2.client.registration.google.client-id}")
    private String googleClientId;

    @Value("${spring.security.oauth2.client.registration.google.client-secret}")
    private String googleClientSecret;

    @Value("${spring.security.oauth2.client.registration.google.scope}")
    private String googleScope;

    @Value("${spring.security.oauth2.client.registration.google.redirect-uri}")
    private String googleRedirectUri;

    @Value("${spring.security.oauth2.client.provider.google.authorization-uri}")
    private String googleAuthorizationUri;

    @Value("${spring.security.oauth2.client.provider.google.refresh-token-uri}")
    private String googleRefreshTokenUri;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtUtil jwtUtil;

    @Autowired
    private EmailService emailService;

    @Autowired
    private PasswordResetService passwordResetService;

    @Autowired
    private UserService userService;

    // temporary , to do implement better way in prod use db
    private final Map<String, String> tempTokens = new HashMap<>(); // Email -> Token mapping

    public String getTempToken(String email) {
        return tempTokens.remove(email);
    }

    public String login(UserRequest loginDto) {

        try {
            // to think of extracting
            System.out.println(loginDto.getEmail() + " , " + loginDto.getPassword());
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getEmail(),
                    loginDto.getPassword()));
            SecurityContextHolder.getContext().setAuthentication(authentication);
            String token = jwtUtil.generateJwtToken(authentication);
            System.out.println("token : ");
            System.out.println(token);
            return token;
        } catch (Exception e) {
            System.out.println("Authentication failed: " + e.getMessage());
            throw e;
        }
        // return "";
    }

    public String initiateLogin(UserRequest loginDto) throws MessagingException {
        try {
            String email = loginDto.getEmail(), password = loginDto.getPassword();
            User user = userService.findByEmail(email);
            if (isNull(user))
                throw new ApiException(HttpStatus.NOT_FOUND, "No user");
            if (!user.getIsEmailVerified()) {
                throw new ApiException(HttpStatus.FORBIDDEN, "User isn't verified");
            }
            System.out.println(SecurityConfig.passwordEncoder().matches(password, user.getPassword()));
            Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(
                    loginDto.getEmail(),
                    loginDto.getPassword()));
            if (!SecurityConfig.passwordEncoder().matches(password, user.getPassword())) {
                throw new RuntimeException("Invalid credentials.");
            }

            String token = jwtUtil.generateJwtToken(authentication);
            return token;
            // tempTokens.put(email, token); // Temporarily store the token
            // changeAndResendCode(email, user);
        } catch (Exception e) {
            System.out.println(
                    "Authentication failed: " + e.getMessage() + " " + e.getLocalizedMessage() + " " + e.getClass());
            throw e;
        }
    }

    public String verifyLogin2FA(UserOTP verifyDto) {
        try {
            User user = verifyCode(verifyDto.getCode(), verifyDto.getEmail());

            if (!isNull(user)) {
                user.setTemp2FACode(null);
                user.setCodeExpiryTime(null);
                userService.save(user);
                return getTempToken(verifyDto.getEmail());
                // return completeLogin(verifyDto.getEmail());
            }
            throw new ApiException(HttpStatus.BAD_REQUEST, "Login Verification failed");
        } catch (Exception e) {
            System.out.println("Login verification failed: " + e.getMessage());
            throw e;
        }
    }

    private User verifyCode(String code, String email) {
        try {
            User user = userService.findByEmail(email);
            if (isNull(user))
                throw new ApiException(HttpStatus.NOT_FOUND, "No user");
            if (!isNull(user.getTemp2FACode()) && user.getTemp2FACode().equals(code) &&
                    user.getCodeExpiryTime().isAfter(LocalDateTime.now())) {
                return user;
            }
            return null;
        } catch (Exception e) {
            throw e;
        }
    }

    public String verifyRegister2FA(UserOTP verifyDto) {
        try {
            User user = verifyCode(verifyDto.getCode(), verifyDto.getEmail());

            if (!isNull(user)) {
                user.setIsEmailVerified(true);
                user.setTemp2FACode(null);
                user.setCodeExpiryTime(null);
                user.setRegisterDate(LocalDateTime.now());
                userService.save(user);
                // return login(new UserRequest(user.getUsername(), user.getEmail(),
                // user.getPassword()));
                return jwtUtil.generateJwtToken(user);

            }
            throw new ApiException(HttpStatus.BAD_REQUEST, "Bad Verification Credentials");
        } catch (Exception e) {
            System.out.println("Registration verification failed");
            throw e;
        }
    }

    public UserResponse initiateRegister(UserRequest userRequest) {
        try {

            User user = UserMapper.toEntity(userRequest);
            user.setIsEmailVerified(false); // Not enabled until 2FA is verified
            user.setPassword(SecurityConfig.passwordEncoder().encode(user.getPassword()));
            UserResponse response = userService.save(user);
            changeAndResendCode(user.getEmail(), user);
            return response;
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public void initiateChangePasswordRequest(String Email) {
        try {
            User user = userService.findByEmail(Email);
            if (isNull(user))
                throw new ApiException(HttpStatus.NOT_FOUND, "No user");
            if (!user.getIsEmailVerified())
                throw new ApiException(HttpStatus.FORBIDDEN, "Invalid password change request");
            changeAndResendCode(Email, user);
        } catch (Exception e) {
            System.out.println(e);
            System.out.println(e.getStackTrace());
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public PasswordResetToken verifyChangePasswordRequest(UserOTP userOTP) {
        try {

            User user = verifyCode(userOTP.getCode(), userOTP.getEmail());

            if (!isNull(user)) {
                user.setTemp2FACode(null);
                user.setCodeExpiryTime(null);
                userService.save(user);
                return passwordResetService.assignResetToken(user);

            }
            throw new ApiException(HttpStatus.BAD_REQUEST, "Bad Credentials");
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    public void resetPassword(PasswordResetDTO PasswordResetDTO) {
        try {
            System.out.println("IN reset password");
            User user = passwordResetService.validateResetToken(PasswordResetDTO.getResetToken());
            if (!isNull(user)) {
                userService.updatePassword(user, PasswordResetDTO.getNewPassword());
                passwordResetService.invalidateResetToken(PasswordResetDTO.getResetToken());
                return;

            }
            throw new ApiException(HttpStatus.BAD_REQUEST, "Bad Credentials");
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    @Transactional
    public void changeAndResendCode(String email, User user) {
        try {
            String code = jwtUtil.generateCode2FA();
            if (isNull(user)) {
                user = userService.findByEmail(email);
                if (isNull(user))
                    throw new ApiException(HttpStatus.NOT_FOUND, "User not found");
            }
            user.setTemp2FACode(code);
            user.setCodeExpiryTime(LocalDateTime.now().plusMinutes(codeExpiryMinutes));
            userService.save(user);
            emailService.send2FACode(email, code);
        } catch (Exception e) {
            throw new ApiException(HttpStatus.INTERNAL_SERVER_ERROR, e.getMessage());
        }
    }

    // oauth section
    // to clean : merge, srp, extract to other service if necessary

    public String getAuthUrl(OauthProviderType provider) {

        if (provider.equals(OauthProviderType.GOOGLE)) {
            // to variable
            String authUrl = googleAuthorizationUri +
                    "?client_id=" + googleClientId +
                    "&redirect_uri=" + googleRedirectUri +
                    "&response_type=code" +
                    "&scope="
                    // + scope;
                    +
                    "https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.email+https%3A%2F%2Fwww.googleapis.com%2Fauth%2Fuserinfo.profile+openid"
                    + "&prompt=consent&access_type=offline"; // to force consent screen and refresh token ,can
                                                             // probably
                                                             // get rid of later on after testing
            // "&scope=" + scope;
            return authUrl;

        }

        else
            throw new ApiException(HttpStatus.BAD_REQUEST, "Oauth type not supported");

    }

    public String processGrantCode(String code, OauthProviderType provider) {
        try {
            String accessToken;
            User oauthUser, user;
            if (provider.equals(OauthProviderType.GOOGLE)) {
                JsonNode response = getOauthAccessTokenGoogle(code);
                accessToken = response.get("access_token").asText();
                oauthUser = getProfileDetailsGoogle(accessToken);
                user = registerOauthUser(oauthUser, provider);
                saveTokenInfo(user, provider, accessToken, response.get("refresh_token").asText(),
                        LocalDateTime.now().plusSeconds(response.get("expires_in").asInt()));

            } else
                throw new ApiException(HttpStatus.BAD_REQUEST, "Oauth type not supported");

            return createJwtForUser(user);

        } catch (Exception e) {
            System.out.println(e.getMessage());
            throw new ApiException(HttpStatus.BAD_REQUEST, "Something went wrong processing grant code");
        }

    }

    private User registerOauthUser(User oauthUser, OauthProviderType provider) {
        try {
            User user = userService.findByEmail(oauthUser.getEmail());
            if (isNull(user)) {
                user = userService
                        .saveOauthUser(
                                UserRequest.builder().username(oauthUser.getUsername()).email(oauthUser.getEmail())
                                        .password(generateUUID()).build());

            }
            return user;
        } catch (Exception e) {
            throw e;
        }
    }

    public void saveTokenInfo(User user, OauthProviderType provider, String accessToken, String refreshToken,
            LocalDateTime expiresAt) {
        try {
            // to do encrypt all tokens before storing, decrypt before use , maybe override
            // setters and getters
            user.setProvider(provider);
            // currently don't need to store them
            // user.setAccessToken(accessToken);
            // user.setRefreshToken(refreshToken);
            // user.setExpiresAt(expiresAt);
            userService.save(user);

        } catch (Exception e) {
            throw e;
        }
    }

    private User getProfileDetailsGoogle(String accessToken) {
        System.out.println("IN get profile details google");
        RestTemplate restTemplate = new RestTemplate();
        HttpHeaders httpHeaders = new HttpHeaders();
        httpHeaders.setBearerAuth(accessToken);
        httpHeaders.set("Provider", OauthProviderType.GOOGLE.toString());

        HttpEntity<String> requestEntity = new HttpEntity<>(httpHeaders);

        String url = "https://www.googleapis.com/oauth2/v2/userinfo";
        ResponseEntity<String> response;

        try {
            response = restTemplate.exchange(url, HttpMethod.GET,
                    requestEntity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                // Parse the response JSON
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode responseBody = objectMapper.readTree(response.getBody());
                User user = new User();
                user.setEmail(responseBody.get("email").asText());
                user.setUsername(responseBody.get("name").asText("Anonymous")); // default value

                return user;
            } else {
                throw new RuntimeException("Failed to retrieve user profile. Status code: " +
                        response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while retrieving user profile: " +
                    e.getMessage(), e);
        }

    }

    public JsonNode getOauthAccessTokenGoogle(String code) {
        String tokenEndpoint = "https://oauth2.googleapis.com/token";
        String grantType = "authorization_code";

        // Create the request body
        JSONObject requestBody = new JSONObject();
        requestBody.put("code", code);
        requestBody.put("client_id", googleClientId);
        requestBody.put("client_secret", googleClientSecret);
        requestBody.put("redirect_uri", googleRedirectUri);
        requestBody.put("grant_type", grantType);

        // Set headers
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_JSON);

        // Build the request entity
        HttpEntity<String> requestEntity = new HttpEntity<>(requestBody.toString(), headers);

        // Use RestTemplate to send the request
        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response;

        try {
            response = restTemplate.exchange(tokenEndpoint, HttpMethod.POST, requestEntity, String.class);
            if (response.getStatusCode() == HttpStatus.OK) {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode responseBody = objectMapper.readTree(response.getBody());
                System.out.println(responseBody); // see what fields it includes , might need refresh token from here
                return responseBody;
            } else {
                throw new RuntimeException("Failed to retrieve access token. Status code: " + response.getStatusCode());
            }
        } catch (Exception e) {
            throw new RuntimeException("Error occurred while retrieving access token: " + e.getMessage(), e);
        }
    }

    public String refreshAccessToken(String refreshToken, OauthProviderType providerType) {
        // is this necessary ? since we only authenticate for login prob not but keep it
        // for now
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);
        String tokenEndpoint, clientId, clientSecret;
        if (providerType.equals(OauthProviderType.GOOGLE)) {
            tokenEndpoint = googleRefreshTokenUri;
            clientId = googleClientId;
            clientSecret = googleClientSecret;
        } else
            throw new ApiException(HttpStatus.BAD_REQUEST, "Unsupported oauth type");

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", clientId);
        body.add("client_secret", clientSecret);
        body.add("refresh_token", refreshToken);
        body.add("grant_type", "refresh_token");

        HttpEntity<MultiValueMap<String, String>> requestEntity = new HttpEntity<>(body, headers);

        RestTemplate restTemplate = new RestTemplate();
        ResponseEntity<String> response = restTemplate.exchange(tokenEndpoint, HttpMethod.POST, requestEntity,
                String.class);

        if (response.getStatusCode() == HttpStatus.OK) {
            try {
                ObjectMapper objectMapper = new ObjectMapper();
                JsonNode responseBody = objectMapper.readTree(response.getBody());

                String newAccessToken = responseBody.get("access_token").asText();
                int expiresIn = responseBody.get("expires_in").asInt();

                userService.updateAccessToken(refreshToken, newAccessToken, LocalDateTime.now().plusSeconds(expiresIn));

                return newAccessToken;
            } catch (Exception e) {
                throw new RuntimeException("Error refreshing token", e);
            }
        } else {
            throw new ApiException(HttpStatus.valueOf(response.getStatusCode().value()),
                    "Failed to refresh token. Status code: " + response.getStatusCode());
        }
    }

    // generating jwts, uuids , etc

    public static String generateUUID() {
        // to think of maybe add to jwt util and make static
        return UUID.randomUUID().toString();
    }

    private String createJwtForUser(User user) {
        return jwtUtil.generateJwtToken(user);
    }

}
