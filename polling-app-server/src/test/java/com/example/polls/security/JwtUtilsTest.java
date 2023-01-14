package com.example.polls.security;

import static org.junit.jupiter.api.Assertions.assertFalse;

import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class JwtUtilsTest {

    @InjectMocks
    JwtTokenProvider jwtUtilsMock;

    private String token = "eyJhbGciOiJIUzUxMiJ9.eyJzdWIiOiI2MzNiZGVjMjZjNmQ5ZjIyNDcwYmIxOGYsc2hpdmFuYW5kLnByYWRoYW5AcHVibGljaXNzYXBpZW50LmNvbSIsInJvbGVzIjpbIlVTRVIiXSwiaWF0IjoxNjY1NzY4OTQ3LCJleHAiOjE2NjU3NzI1NDd9.a5hDJGzwPWSWWU5DzGMWlCX5S51Wf9E7TZBYQczgm4-uTTqfyh7NX2ay6WXYE0ZQ8neUrbEYJnPwIiWV7Mwb3Q";


    @Test
    void validateJwtTokenTestIllegalArgumentException(){
        assertFalse(jwtUtilsMock.validateToken(token));
        
    }

    @Test
    void validateJwtToken(){
        assertFalse(jwtUtilsMock.validateToken("a.b.c"));
        
    }
}
