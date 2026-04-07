package com.marketplace.auth.dto.request;

import jakarta.validation.constraints.NotBlank;

public class TokenRefreshRequest {
    @NotBlank(message = "Refresh Token cannot be blank")
    private String refreshToken;

    public String getRefreshToken() { return refreshToken; }
    public void setRefreshToken(String refreshToken) { this.refreshToken = refreshToken; }
}
