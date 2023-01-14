import { notification } from "antd";
import React, { Component } from "react";
import LoadingIndicator from "../common/LoadingIndicator";
import { castVote, getAllPoll } from "../util/APIUtils";
import Poll from "./Poll";
import "./PollList.css";

class PollList extends Component {
  constructor(props) {
    super(props);
    this.state = {
      polls: [],
      currentVotes: [],
      isLoading: false,
    };
    this.loadPollList = this.loadPollList.bind(this);
  }

  loadPollList() {
    let promise;
    promise = getAllPoll();

    if (!promise) {
      return;
    }

    this.setState({
      isLoading: true,
    });

    promise
      .then((response) => {
        const polls = this.state.polls.slice();
        const currentVotes = this.state.currentVotes.slice();

        this.setState({
           polls: polls.concat(response),
          currentVotes: currentVotes.concat(
            Array(response.length).fill(null)
          ),
          isLoading: false,
        });
      })
      .catch((error) => {
        this.setState({
          isLoading: false,
        });
      });
  }

  componentDidMount() {
    this.loadPollList();
  }

  componentDidUpdate(nextProps) {
    if (this.props.isAuthenticated !== nextProps.isAuthenticated) {
      // Reset State
      this.setState({
        polls: [],
        currentVotes: [],
        isLoading: false,
      });
      this.loadPollList();
    }
  }

  handleVoteChange(event, pollIndex) {
    const currentVotes = this.state.currentVotes.slice();
    currentVotes[pollIndex] = event.target.value;

    this.setState({
      currentVotes: currentVotes,
    });
  }

  handleVoteSubmit(event, pollIndex) {
    event.preventDefault();
    if (!this.props.isAuthenticated) {
      this.props.history.push("/login");
      notification.info({
        message: "Polling App",
        description: "Please login to vote.",
      });
      return;
    }

    const poll = this.state.polls[pollIndex];
    const selectedChoice = this.state.currentVotes[pollIndex];

    const voteData = {
      pollId: poll.id,
      choiceId: selectedChoice,
    };

    castVote(voteData)
      .then((response) => {
        const polls = this.state.polls.slice();
        polls[pollIndex] = response;
        this.setState({
          polls: polls,
        });
      })
      .catch((error) => {
        if (error.status === 401) {
          this.props.handleLogout(
            "/login",
            "error",
            "You have been logged out. Please login to vote"
          );
        } else {
          notification.error({
            message: "Polling App",
            description:
              error.message || "Sorry! Something went wrong. Please try again!",
          });
        }
      });
  }

  render() {
    const pollViews = [];
    this.state.polls.forEach((poll, pollIndex) => {
      pollViews.push(
        <Poll
          key={poll.id}
          poll={poll}
          currentVote={this.state.currentVotes[pollIndex]}
          handleVoteChange={(event) => this.handleVoteChange(event, pollIndex)}
          handleVoteSubmit={(event) => this.handleVoteSubmit(event, pollIndex)}
        />
      );
    });

    return (
      <div className="polls-container">
        {pollViews}
        {!this.state.isLoading && this.state.polls.length === 0 ? (
          <div className="no-polls-found">
            <span>No Polls Found.</span>
          </div>
        ) : null}
        {this.state.isLoading ? <LoadingIndicator /> : null}
      </div>
    );
  }
}

export default PollList;
