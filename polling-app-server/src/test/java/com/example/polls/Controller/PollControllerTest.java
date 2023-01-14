package com.example.polls.Controller;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.http.MediaType;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.setup.MockMvcBuilders;

import com.example.polls.TestData;
import com.example.polls.controller.PollController;
import com.example.polls.payload.PollRequest;
import com.example.polls.payload.VoteRequest;
import com.example.polls.service.PollService;
import com.fasterxml.jackson.databind.ObjectMapper;

@ComponentScan(basePackages = "com.example.polls")
@SpringBootTest
@AutoConfigureMockMvc
class PollControllerTest {
    
    @InjectMocks
    PollController pollController;

    @Autowired
    MockMvc mockMvc;

    @Autowired
    ObjectMapper objectMapper;

    @Mock
    PollService pollService;

    PollControllerTest(){

    }
    
    @BeforeEach
    public void setup() {
        mockMvc = MockMvcBuilders.standaloneSetup(pollController).build();
    }

    @Test
    void testCreatePoll() throws Exception{
        when(pollService.createPoll(any(PollRequest.class))).thenReturn(TestData.pollData());
        this.mockMvc.perform(post("/api/polls")
                    .header("Authorization", "Bearer token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(TestData.pollRequest())))
            .andExpect(status().isOk())
            .andExpect(jsonPath(".success").value(true))
            .andExpect(jsonPath(".message").value(TestData.apiResponseData().getMessage()));
    }

    @Test
    void testGetAllPost() throws Exception{
        when(pollService.getAllPoll(any())).thenReturn(TestData.pollResponseList());
        this.mockMvc.perform(get("/api/polls/poll")
                .header("Authorization", "Bearer token"))
			.andExpect(status().isOk());
    }

    @Test
    void testGetPollById() throws Exception{
        when(pollService.getPollById(anyLong(), any())).thenReturn(TestData.pollResponse());
        this.mockMvc.perform(get("/api/polls/{pollId}", 1l)
                .header("Authorization", "Bearer token"))
			.andExpect(status().isOk());
    }

    @Test
    void testCastVote() throws Exception{
        when(pollService.castVoteAndGetUpdatedPoll(anyLong(), any(VoteRequest.class),any())).thenReturn(TestData.pollResponse());
        this.mockMvc.perform(post("/api/polls/{pollId}/votes", 1l)
                    .header("Authorization", "Bearer token")
                    .contentType(MediaType.APPLICATION_JSON)
                    .content(objectMapper.writeValueAsString(TestData.voteRequest())))
            .andExpect(status().isOk());
    }
}
