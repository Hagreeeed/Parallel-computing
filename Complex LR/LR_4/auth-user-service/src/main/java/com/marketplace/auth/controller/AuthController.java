package com.marketplace.auth.controller;

import com.marketplace.auth.dto.request.LoginRequest;
import com.marketplace.auth.dto.request.RegisterUserRequest;
import com.marketplace.auth.dto.request.TokenRefreshRequest;
import com.marketplace.auth.dto.response.JwtAuthenticationResponse;
import com.marketplace.auth.dto.response.TokenRefreshResponse;
import com.marketplace.auth.exception.TokenRefreshException;
import com.marketplace.auth.model.RefreshToken;
import com.marketplace.auth.model.User;
import com.marketplace.auth.security.JwtUtil;
import com.marketplace.auth.service.RefreshTokenService;
import com.marketplace.auth.service.UserService;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.web.bind.annotation.*;

@RestController
@RequestMapping("/api/auth")
public class AuthController {
    private final AuthenticationManager authenticationManager;
    private final JwtUtil jwtUtil;
    private final UserService userService;
    private final RefreshTokenService refreshTokenService;

    public AuthController(AuthenticationManager authenticationManager, JwtUtil jwtUtil, UserService userService, RefreshTokenService refreshTokenService) {
        this.authenticationManager = authenticationManager; this.jwtUtil = jwtUtil;
        this.userService = userService; this.refreshTokenService = refreshTokenService;
    }

    @PostMapping("/login")
    public ResponseEntity<JwtAuthenticationResponse> authenticateUser(@Valid @RequestBody LoginRequest loginRequest) {
        Authentication authentication = authenticationManager.authenticate(new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));
        SecurityContextHolder.getContext().setAuthentication(authentication);
        User userDetails = (User) authentication.getPrincipal();
        String jwt = jwtUtil.generateToken(userDetails);
        RefreshToken refreshToken = refreshTokenService.createRefreshToken(userDetails.getId());
        return ResponseEntity.ok(new JwtAuthenticationResponse(jwt, refreshToken.getToken(), userDetails.getId(), userDetails.getEmail(), userDetails.getRole().name()));
    }

    @PostMapping("/register")
    public ResponseEntity<User> registerUser(@Valid @RequestBody RegisterUserRequest signUpRequest) {
        return ResponseEntity.status(HttpStatus.CREATED).body(userService.register(signUpRequest));
    }

    @PostMapping("/refresh")
    public ResponseEntity<TokenRefreshResponse> refreshtoken(@Valid @RequestBody TokenRefreshRequest request) {
        return refreshTokenService.findByToken(request.getRefreshToken())
                .map(refreshTokenService::verifyExpiration)
                .map(RefreshToken::getUser)
                .map(user -> { String token = jwtUtil.generateToken(user); return ResponseEntity.ok(new TokenRefreshResponse(token, request.getRefreshToken())); })
                .orElseThrow(() -> new TokenRefreshException(request.getRefreshToken(), "Refresh token is not in database!"));
    }

    @PostMapping("/logout")
    public ResponseEntity<String> logoutUser(@AuthenticationPrincipal User user) {
        if (user != null) refreshTokenService.deleteByUserId(user.getId());
        return ResponseEntity.ok("Log out successful");
    }
}
