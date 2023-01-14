package com.example.polls;

import java.time.Instant;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import com.example.polls.model.Choice;
import com.example.polls.model.ChoiceVoteCount;
import com.example.polls.model.Poll;
import com.example.polls.model.Role;
import com.example.polls.model.RoleName;
import com.example.polls.model.User;
import com.example.polls.model.Vote;
import com.example.polls.payload.ApiResponse;
import com.example.polls.payload.ChoiceRequest;
import com.example.polls.payload.ChoiceResponse;
import com.example.polls.payload.LoginRequest;
import com.example.polls.payload.PollLength;
import com.example.polls.payload.PollRequest;
import com.example.polls.payload.PollResponse;
import com.example.polls.payload.SignUpRequest;
import com.example.polls.payload.UserSummary;
import com.example.polls.payload.VoteRequest;
import com.example.polls.security.UserPrincipal;


public class TestData {

    static final Instant time = Instant.now();

    public static List<Choice> choiceList(){
        List<Choice> choices = new ArrayList<>();
        Choice choice = new Choice();
        choice.setId(1L);
        choice.setPoll(pollData());
        choice.setText("1");
        choices.add(choice);
        choices.add(choice);
        return choices;
    }

    public static List<ChoiceRequest> choiceRequestList(){
        List<ChoiceRequest> choiceRequests = new ArrayList<>();
        ChoiceRequest choiceRequest = new ChoiceRequest();
        choiceRequest.setText("1");
        choiceRequests.add(choiceRequest);
        choiceRequests.add(choiceRequest);
        return choiceRequests;
    } 
    
    public static PollRequest pollRequest(){
        PollRequest pollRequest = new PollRequest();
        PollLength pollLength = new PollLength();
        pollLength.setDays(0);
        pollLength.setHours(1);
        pollRequest.setChoices(choiceRequestList());
        pollRequest.setPollLength(pollLength);
        pollRequest.setQuestion("question");

        return pollRequest;
    }

    public static Poll pollData(){
        Poll poll = new Poll();
        List<Choice> choices = new ArrayList<>();
        Choice choice = new Choice();
        choice.setId(1L);
        choice.setPoll(poll);
        choice.setText("1");
        choices.add(choice);
        choices.add(choice);
        poll.setChoices(choices);
        poll.setCreatedAt(time);
        poll.setCreatedBy(1L);
        poll.setExpirationDateTime(time);
        poll.setId(1L);
        poll.setQuestion("question");
        poll.setUpdatedAt(time);
        poll.setUpdatedBy(1L);
        return poll;
        
    }
    public static ApiResponse apiResponseData(){
        ApiResponse apiResponse = new ApiResponse(true, "Poll Created Successfully");
        return apiResponse;
    }

    public static UserPrincipal userPrincipal(){
        UserPrincipal userPrincipal = new UserPrincipal(1L, "user", "username", "sakthi@gmail.com", "password", null);
        return userPrincipal;
    }

    public static List<ChoiceResponse> choiceResponseList(){
        ChoiceResponse choiceResponse = new ChoiceResponse();
        choiceResponse.setId(1l);
        choiceResponse.setText("text");
        choiceResponse.setVoteCount(10L);
        List<ChoiceResponse> choiceResponses= new ArrayList<>();
        choiceResponses.add(choiceResponse);
        choiceResponses.add(choiceResponse);
        return choiceResponses;
    }

    public static UserSummary userSummary(){
        UserSummary userSummary = new UserSummary(1l, "username", "user");
        return userSummary;
    }

    public static PollResponse pollResponse(){
        PollResponse pollResponse = new PollResponse();
        pollResponse.setChoices(choiceResponseList());
        pollResponse.setCreatedBy(userSummary());
        pollResponse.setCreationDateTime(time);
        pollResponse.setExpirationDateTime(time);
        pollResponse.setExpired(true);
        pollResponse.setId(1L);
        pollResponse.setQuestion("question");
        pollResponse.setSelectedChoice(1L);
        pollResponse.setTotalVotes(10L);
        return pollResponse;
    }

    public static List<PollResponse> pollResponseList(){
        List<PollResponse> responses = new ArrayList<>();
        responses.add(pollResponse());
        return responses;
    }

    public static VoteRequest voteRequest(){
        VoteRequest voteRequest = new VoteRequest();
        voteRequest.setChoiceId(1l);
        return voteRequest;
    }

    public static User user(){
        Set<Role> roles  = new HashSet<>();
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_USER);
        roles.add(role);
        User user = new User();
        user.setCreatedAt(time);
        user.setEmail("user@gmail.com");
        user.setId(1L);
        user.setName("user");
        user.setPassword("password");
        user.setRoles(roles);
        user.setUpdatedAt(time);
        user.setUsername("username");
        return user;
    }

    public static ChoiceVoteCount choiceVoteCount(){
        ChoiceVoteCount choiceVoteCount = new ChoiceVoteCount(1L, 10L);
        return choiceVoteCount;
    }

    public static Choice choice(){
        Choice choice = new Choice();
        choice.setId(1L);
        choice.setPoll(TestData.pollData());
        choice.setText("text");
        return choice;
    }

    public static Vote vote(){
        Vote vote = new Vote();
        vote.setChoice(choice());
        vote.setCreatedAt(time);
        vote.setId(1L);
        vote.setPoll(pollData());
        vote.setUpdatedAt(time);
        vote.setUser(user());
        return vote;
    }

    public static LoginRequest loginRequest(){
        LoginRequest loginRequest = new LoginRequest();
        loginRequest.setUsernameOrEmail("user");
        loginRequest.setPassword("password");
        return loginRequest;
    }

    public static SignUpRequest signUpRequest(){
        SignUpRequest signUpRequest = new SignUpRequest();
        signUpRequest.setEmail("email@gmail.com");
        signUpRequest.setName("name");
        signUpRequest.setPassword("password");
        signUpRequest.setUsername("username");
        return signUpRequest;
    }

    public static Role role(){
        Role role = new Role();
        role.setId(1L);
        role.setName(RoleName.ROLE_USER);
        return role;
    }
}
