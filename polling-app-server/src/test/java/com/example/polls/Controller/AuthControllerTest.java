package com.example.polls.Controller;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Import;
import org.springframework.http.MediaType;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContext;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.doNothing;
import static org.mockito.Mockito.mockStatic;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import com.example.polls.TestData;
import com.example.polls.controller.AuthController;
import com.example.polls.exception.AppException;
import com.example.polls.model.RoleName;
import com.example.polls.payload.SignUpRequest;
import com.example.polls.repository.RoleRepository;
import com.example.polls.repository.UserRepository;
import com.example.polls.security.JwtTokenProvider;
import com.example.polls.security.UserPrincipal;
import com.fasterxml.jackson.databind.ObjectMapper;

// @ComponentScan(basePackages = "com.example.polls")
@Import(AuthController.class)
@SpringBootTest
@AutoConfigureMockMvc
class AuthControllerTest {
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @InjectMocks
    AuthController authController;

    @Mock
    AuthenticationManager authenticationManager;

    @Mock
    Authentication mockAuth;

    @Mock
    SecurityContext security;

    @Mock
    JwtTokenProvider jwtTokenProvider;

    @Mock 
    UserRepository userRepository;

    @Mock
    RoleRepository roleRepository;

    @Mock
    PasswordEncoder passwordEncoder;


    AuthControllerTest() {

    }

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(authController).build();
    }

    @Test
    void testSignIn() throws Exception {
        when(authenticationManager.authenticate(any())).thenReturn(mockAuth);
        UserPrincipal userPrincipal = TestData.userPrincipal();
        when(mockAuth.getPrincipal()).thenReturn(userPrincipal);
        Mockito.mockStatic(
          SecurityContextHolder.class
        );
        when(SecurityContextHolder.getContext()).thenReturn(security);
        doNothing().when(security).setAuthentication(mockAuth);
        when(jwtTokenProvider.generateToken(mockAuth)).thenReturn("password");
        this.mockMvc.perform(post("/api/auth/signin")
                    .header("Authorization", "Bearer token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(TestData.loginRequest())))
            .andExpect(status().isOk());
    }

    @Test
    void testSignUpTwo() throws Exception{
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("password");
        when(roleRepository.findByName(any())).thenReturn(Optional.of(TestData.role()));
        when(userRepository.save(any())).thenReturn(TestData.user());
        this.mockMvc.perform(post("/api/auth/signup")
                    .header("Authorization", "Bearer token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(TestData.signUpRequest())))
            .andExpect(status().isOk())
            .andExpect(jsonPath(".success").value(true))
            .andExpect(jsonPath(".message").value("User registered successfully"));
    }

    @Test
    void testSignUpThree() throws Exception{
        SignUpRequest signUpRequest = TestData.signUpRequest();
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("password");
        when(roleRepository.findByName(any())).thenReturn(Optional.empty());
        assertThrows(AppException.class, ()->{
            authController.registerUser(signUpRequest);
        });
    }

    @Test
    void testSignUpFour() throws Exception{
        when(userRepository.existsByUsername(any())).thenReturn(true);
        when(userRepository.existsByEmail(any())).thenReturn(false);
        when(passwordEncoder.encode(any())).thenReturn("password");
        when(roleRepository.findByName(any())).thenReturn(Optional.of(TestData.role()));
        when(userRepository.save(any())).thenReturn(TestData.user());
        this.mockMvc.perform(post("/api/auth/signup")
                    .header("Authorization", "Bearer token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(TestData.signUpRequest())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(".success").value(false))
            .andExpect(jsonPath(".message").value("Username is already taken!"));
    }

    @Test
    void testSignUp() throws Exception{
        when(userRepository.existsByUsername(any())).thenReturn(false);
        when(userRepository.existsByEmail(any())).thenReturn(true);
        when(passwordEncoder.encode(any())).thenReturn("password");
        when(roleRepository.findByName(any())).thenReturn(Optional.of(TestData.role()));
        when(userRepository.save(any())).thenReturn(TestData.user());
        this.mockMvc.perform(post("/api/auth/signup")
                    .header("Authorization", "Bearer token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(TestData.signUpRequest())))
            .andExpect(status().isBadRequest())
            .andExpect(jsonPath(".success").value(false))
            .andExpect(jsonPath(".message").value("Email Address already in use!"));
    }

}
