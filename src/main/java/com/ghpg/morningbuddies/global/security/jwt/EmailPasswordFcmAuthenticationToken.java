package com.ghpg.morningbuddies.global.security.jwt;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;

public class EmailPasswordFcmAuthenticationToken extends UsernamePasswordAuthenticationToken {
    private final String fcmToken;

    public EmailPasswordFcmAuthenticationToken(Object principal, Object credentials, String fcmToken) {
        super(principal, credentials);
        this.fcmToken = fcmToken;
    }

    public String getFcmToken() {
        return fcmToken;
    }
}