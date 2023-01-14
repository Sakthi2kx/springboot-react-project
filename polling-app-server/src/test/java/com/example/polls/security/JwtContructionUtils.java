package com.example.polls.security;

import java.util.Date;
import java.util.List;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

//  The class constructs specific JWT tokens
 class JwtContructionUtils {

    private static String jwtSecret = "fakesecretdjjjjjjjjAJDFBHafbhjFBJAkfNvDNNskjJsjdSjngJKdgnjnjgnJKnNJKnfjkNnfKNmvDMdjDJFJJKanjf";
    private static int jwtExpirationMs = 10000;

    public static String generateAuthorizationToken(String userId, String userName, String role) {
        return "Bearer " + Jwts.builder()
                .setSubject(String.format("%s,%s", userId, userName))
                .claim("roles", List.of(role))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }

    // prepares the JWT token with emptyClaims
    public static String generateAuthorizationToken_withEmptyClaim(String userId, String userName) {
        return "Bearer " + Jwts.builder()
                .setSubject(String.format("%s,%s", userId, userName))
                // .claim("roles", List.of("vjgv"))
                .setIssuedAt(new Date())
                .setExpiration(new Date((new Date()).getTime() + jwtExpirationMs))
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
   
    // prepares the expired JWT token (issueTime = expiration)
    public static String generateExpiredAuthorizationToken(String userId, String userName) {
        return "Bearer " + Jwts.builder()
                .setSubject(String.format("%s,%s", userId, userName))
                .setIssuedAt(new Date())
                .setExpiration(new Date())
                .signWith(SignatureAlgorithm.HS512, jwtSecret)
                .compact();
    }
}
