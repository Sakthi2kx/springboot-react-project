package com.example.polls.security;

import static org.junit.Assert.assertTrue;

import java.io.IOException;

import org.junit.jupiter.api.Test;
import org.springframework.test.web.reactive.server.StatusAssertions;


class JwtTest extends ControllerTestBase{

    String userName = "superAdmin";
    String password = "passwordsuperAdmin";
    String authCode = "!! Generated and Sent Successfully !!";
    String timeToLive = "150";
    String message = "Success";
    String expectedUserName = "normalUser3";
    String userId = "1234567893";

    @Test
    void shouldThrowError_With_WrongTokenSignature() throws IOException{
        StatusAssertions adminResponseList = getAdmins_WrongAuthHeader(JwtContructionUtils.generateAuthorizationToken(userId, userName, "USER"));                 
        adminResponseList.is4xxClientError();
        assertTrue(true);
    }
  
    // jwt is prepared with emptyClaims
    @Test
    void shouldThrowError_IllegalArgumentException() throws IOException{
        StatusAssertions adminResponseList = getAdmins_WrongAuthHeader(JwtContructionUtils.generateAuthorizationToken_withEmptyClaim(userId, userName));                 
        adminResponseList.is4xxClientError();
        assertTrue(true);
    }

    // jwt is prepared with already same issuedTime and expirationTime
    @Test
    void shouldThrowError_ExpiredToken() throws IOException{
        StatusAssertions adminResponseList = getAdmins_WrongAuthHeader(JwtContructionUtils.generateExpiredAuthorizationToken(userId, userName));                 
        adminResponseList.is4xxClientError();
        assertTrue(true);
    }
  
    
    @Test
    void shouldThrowError_MalformedJwt() throws IOException{
        StatusAssertions adminResponseList = getAdmins_WrongAuthHeader("Bearer + ");                 
        adminResponseList.is4xxClientError();
        assertTrue(true);
    }   
}
