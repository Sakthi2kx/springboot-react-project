package com.example.polls.Controller;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.ArgumentMatchers.anyString;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import java.util.Optional;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.polls.TestData;
import com.example.polls.controller.UserController;
import com.example.polls.payload.UserSummary;
import com.example.polls.repository.PollRepository;
import com.example.polls.repository.UserRepository;
import com.example.polls.repository.VoteRepository;
import com.fasterxml.jackson.databind.ObjectMapper;

@ComponentScan(basePackages = "com.example.polls")
@SpringBootTest
@AutoConfigureMockMvc
class UserControllerTest {
    
    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @InjectMocks
    UserController userController;

    @Mock
    PollRepository pollRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    VoteRepository voteRepository;

    UserControllerTest(){
        
    }

    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(userController).build();
    }
    
    @Test
    void testGetCurrentUser() throws Exception{
        UserSummary userSummary = userController.getCurrentUser(TestData.userPrincipal());
        assertEquals(TestData.userSummary().getUsername(), userSummary.getUsername());
    }

    @Test
    void testCheckUsernameAvailability() throws Exception{
         when(userRepository.existsByUsername("name")).thenReturn(true);
        this.mockMvc.perform(get("/api/user/checkUsernameAvailability?username=name")
                .header("Authorization", "Bearer token"))
			.andExpect(status().isOk())
            .andExpect(jsonPath(".available").value(false));
    }

    @Test
    void testCheckUsernameAvailabilityTwo() throws Exception{
        when(userRepository.existsByUsername(anyString())).thenReturn(false);
        this.mockMvc.perform(get("/api/user/checkUsernameAvailability?username=name")
                .header("Authorization", "Bearer token"))
			.andExpect(status().isOk())
            .andExpect(jsonPath(".available").value(true));
    }

    @Test
    void testEmailAvailability() throws Exception{
        when(userRepository.existsByEmail(anyString())).thenReturn(false);
        this.mockMvc.perform(get("/api/user/checkEmailAvailability?email=email@gmail.com")
                .header("Authorization", "Bearer token"))
			.andExpect(status().isOk())
            .andExpect(jsonPath(".available").value(true));
    }

    @Test
    void testEmailAvailabilityTwo() throws Exception{
        when(userRepository.existsByEmail(anyString())).thenReturn(true);
        this.mockMvc.perform(get("/api/user/checkEmailAvailability?email=email@gmail.com")
                .header("Authorization", "Bearer token"))
			.andExpect(status().isOk())
            .andExpect(jsonPath(".available").value(false));
    }

    @Test
    void testGetUserProfile() throws Exception{
        when(userRepository.findByUsername(anyString())).thenReturn(Optional.of(TestData.user()));
        this.mockMvc.perform(get("/api/users/{username}", "username")
                .header("Authorization", "Bearer token"))
			.andExpect(status().isOk());
    }
}
