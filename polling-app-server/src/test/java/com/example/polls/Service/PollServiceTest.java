package com.example.polls.Service;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.ArgumentMatchers.anyList;
import static org.mockito.ArgumentMatchers.anyLong;
import static org.mockito.Mockito.when;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Optional;

import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.ComponentScan;

import com.example.polls.TestData;
import com.example.polls.exception.BadRequestException;
import com.example.polls.exception.ResourceNotFoundException;
import com.example.polls.model.ChoiceVoteCount;
import com.example.polls.model.Poll;
import com.example.polls.model.User;
import com.example.polls.model.Vote;
import com.example.polls.payload.PollResponse;
import com.example.polls.payload.VoteRequest;
import com.example.polls.repository.PollRepository;
import com.example.polls.repository.UserRepository;
import com.example.polls.repository.VoteRepository;
import com.example.polls.security.UserPrincipal;
import com.example.polls.service.PollService;
import com.example.polls.util.ModelMapper;

@ComponentScan(basePackages = "com.example.polls")
@SpringBootTest
class PollServiceTest {
    @Mock
    PollRepository pollRepository;

    @Mock
    UserRepository userRepository;

    @Mock
    ModelMapper modelMapper;
    
    @Mock
    VoteRepository voteRepository;

    @InjectMocks
    PollService pollService;

    PollServiceTest(){

    }

    @Test
    void testGetPollCreatorMap() throws Exception{
        User user = TestData.user();
        Map<Long, User> expectMap = new HashMap<>();
        expectMap.put(1L, user);
        when(userRepository.findByIdIn(anyList())).thenReturn(List.of(user));
        Map<Long,User> creatorMap = pollService.getPollCreatorMap(List.of(TestData.pollData()));
        assertEquals(expectMap, creatorMap);
    }

    @Test
    void testCreatePoll() throws Exception{
        Poll expectedPoll = TestData.pollData();
        when(pollRepository.save(any())).thenReturn(TestData.pollData());
        Poll poll = pollService.createPoll(TestData.pollRequest());
        assertEquals(expectedPoll.toString(), poll.toString());
    }

    @Test
    void testGetAllPoll() throws Exception{
        User user = TestData.user();
        Poll poll = TestData.pollData();
        List<Long> pollIds = new ArrayList<>();
        pollIds.add(1L);
        List<ChoiceVoteCount> choiceVoteCounts = new ArrayList<>();
        choiceVoteCounts.add(TestData.choiceVoteCount());
        List<Vote> vote = new ArrayList<>();
        vote.add(TestData.vote());
        List<PollResponse> expectedPollResponses = TestData.pollResponseList();
        when(pollRepository.findAllByOrderByIdDesc()).thenReturn(List.of(poll));
        when(voteRepository.countByPollIdInGroupByChoiceId(any())).thenReturn(choiceVoteCounts);
        when(voteRepository.findByUserIdAndPollIdIn(any(), any())).thenReturn(vote);
        when(userRepository.findByIdIn(anyList())).thenReturn(List.of(user));
        List<PollResponse> pollResponses = pollService.getAllPoll(TestData.userPrincipal());
        assertEquals(expectedPollResponses.get(0).getId(), pollResponses.get(0).getId());
    }

    @Test
    void testGetPollById() throws Exception{
        when(pollRepository.findById(anyLong())).thenReturn(Optional.of(TestData.pollData()));
        when(voteRepository.countByPollIdGroupByChoiceId(any())).thenReturn(List.of(TestData.choiceVoteCount()));
        when(userRepository.findById(any())).thenReturn(Optional.of(TestData.user()));
        when(voteRepository.findByUserIdAndPollId(any(), any())).thenReturn(TestData.vote());
        PollResponse pollResponse = pollService.getPollById(1L, TestData.userPrincipal());
        assertEquals(TestData.pollResponse().getId(), pollResponse.getId());
    }

    @Test
    void testGetPollByIdThrowsResourceNotFoundException(){
        UserPrincipal userPrincipal = TestData.userPrincipal();
        when(pollRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, ()->{
            pollService.getPollById(1L, userPrincipal);
        });
    }

    @Test
    void testGetPollByIdThrowsResourceNotFoundExceptionTwo(){
        UserPrincipal userPrincipal = TestData.userPrincipal();
        when(pollRepository.findById(anyLong())).thenReturn(Optional.of(TestData.pollData()));
        when(voteRepository.countByPollIdGroupByChoiceId(any())).thenReturn(List.of(TestData.choiceVoteCount()));
        when(userRepository.findById(any())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, ()->{
            pollService.getPollById(1L, userPrincipal);
        });
    }

    @Test
    void testGetPollByIdThrowsResourceNotFoundExceptionThree(){
        when(pollRepository.findById(anyLong())).thenReturn(Optional.of(TestData.pollData()));
        when(voteRepository.countByPollIdGroupByChoiceId(any())).thenReturn(List.of(TestData.choiceVoteCount()));
        when(userRepository.findById(any())).thenReturn(Optional.of(TestData.user()));
        PollResponse pollResponse = pollService.getPollById(1L, null);
        assertEquals(TestData.pollResponse().getId(), pollResponse.getId());
    }

    @Test
    void testcastVoteAndGetUpdatedPoll(){
        Poll poll = TestData.pollData();
        poll.setExpirationDateTime(Instant.parse("2025-12-03T10:15:30.00Z"));
        when(pollRepository.findById(anyLong())).thenReturn(Optional.of(poll));
        when(userRepository.getOne(any())).thenReturn(TestData.user());
        when(voteRepository.save(any())).thenReturn(TestData.vote());
        when(voteRepository.countByPollIdGroupByChoiceId(any())).thenReturn(List.of(TestData.choiceVoteCount()));
        when(userRepository.findById(any())).thenReturn(Optional.of(TestData.user()));
        PollResponse pollResponse = pollService.castVoteAndGetUpdatedPoll(1L, TestData.voteRequest(), TestData.userPrincipal());
        assertEquals(TestData.pollResponse().getId(), pollResponse.getId());
    }

    @Test
    void testCastVoteAndGetUpdatedPollThrowsResourceNotFoundException(){
        UserPrincipal userPrincipal = TestData.userPrincipal();
        VoteRequest voteRequest = TestData.voteRequest();
        when(pollRepository.findById(anyLong())).thenReturn(Optional.empty());
        assertThrows(ResourceNotFoundException.class, ()->{
            pollService.castVoteAndGetUpdatedPoll(1L, voteRequest, userPrincipal);;
        });
    }

    @Test
    void testcastVoteAndGetUpdatedPollThrowsBadRequestException(){
        Poll poll = TestData.pollData();
        UserPrincipal userPrincipal = TestData.userPrincipal();
        VoteRequest voteRequest = TestData.voteRequest();
        when(pollRepository.findById(anyLong())).thenReturn(Optional.of(poll));
        assertThrows(BadRequestException.class, ()->{
            pollService.castVoteAndGetUpdatedPoll(1L, voteRequest, userPrincipal);;
        });
    }

    @Test
    void testcastVoteAndGetUpdatedPollThrowsBadRequestExceptionTwo(){
        Poll poll = TestData.pollData();
        UserPrincipal userPrincipal = TestData.userPrincipal();
        VoteRequest voteRequest = TestData.voteRequest();
        poll.setExpirationDateTime(Instant.parse("2025-12-03T10:15:30.00Z"));
        when(pollRepository.findById(anyLong())).thenReturn(Optional.of(poll));
        when(userRepository.getOne(any())).thenReturn(TestData.user());
        when(voteRepository.save(any())).thenThrow(BadRequestException.class);
        assertThrows(BadRequestException.class, ()->{
            pollService.castVoteAndGetUpdatedPoll(1L, voteRequest, userPrincipal);;
        });
    }

    @Test
    void testcastVoteAndGetUpdatedPollThrowsResourceNotFoundExceptionTwo(){
        Poll poll = TestData.pollData();
        UserPrincipal userPrincipal = TestData.userPrincipal();
        VoteRequest voteRequest = TestData.voteRequest();
        poll.setExpirationDateTime(Instant.parse("2025-12-03T10:15:30.00Z"));
        when(pollRepository.findById(anyLong())).thenReturn(Optional.of(poll));
        when(userRepository.getOne(any())).thenReturn(TestData.user());
        when(voteRepository.countByPollIdGroupByChoiceId(any())).thenReturn(List.of(TestData.choiceVoteCount()));
        when(userRepository.findById(any())).thenThrow(ResourceNotFoundException.class);
        assertThrows(ResourceNotFoundException.class, ()->{
            pollService.castVoteAndGetUpdatedPoll(1L, voteRequest, userPrincipal);;
        });
    }

    @Test
    void testGetAllPollTwo() throws Exception{
        User user = TestData.user();
        Poll poll = TestData.pollData();
        List<Long> pollIds = new ArrayList<>();
        pollIds.add(1L);
        List<ChoiceVoteCount> choiceVoteCounts = new ArrayList<>();
        choiceVoteCounts.add(TestData.choiceVoteCount());
        List<Vote> vote = new ArrayList<>();
        vote.add(TestData.vote());
        List<PollResponse> expectedPollResponses = TestData.pollResponseList();
        when(pollRepository.findAllByOrderByIdDesc()).thenReturn(List.of(poll));
        when(voteRepository.countByPollIdInGroupByChoiceId(any())).thenReturn(choiceVoteCounts);
        when(userRepository.findByIdIn(anyList())).thenReturn(List.of(user));
        List<PollResponse> pollResponses = pollService.getAllPoll(null);
        assertEquals(expectedPollResponses.get(0).getId(), pollResponses.get(0).getId());
    }

}
