package com.rose.models;

import lombok.Data;

import java.util.List;

@Data
public class JwtResponse {
    private String token;

    private final String type = "Bearer";
    private String refreshToken;

    public JwtResponse(String accessToken, String refreshToken) {
        this.token = accessToken;
        this.refreshToken = refreshToken;
    }
}
