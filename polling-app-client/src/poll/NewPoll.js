import { Button, Col, Form, Input, notification, Select } from "antd";
import React, { useState } from "react";
import {
  POLL_CHOICE_MAX_LENGTH, POLL_QUESTION_MAX_LENGTH
} from "../constants";
import { createPoll } from "../util/APIUtils";
import "./NewPoll.css";
const Option = Select.Option;
const FormItem = Form.Item;
const { TextArea } = Input;

function NewPoll(props) {
  const [question, setQuestion] = useState({ text: "" });
  const [choices, setChoices] = useState([{ text: "" }, { text: "" }, { text: "" }, { text: "" }]);
  const [pollLength, setPollLength] = useState({ days: 1, hours: 0 });

  const handleSubmit = (event) => {
    event.preventDefault();
    const pollData = {
      question: question.text,
      choices: choices.map((choice) => {
        return { text: choice.text };
      }),
      pollLength: pollLength,
    };

    createPoll(pollData)
      .then((response) => {
        props.history.push("/")
      })
      .catch((error) => {
        if (error.status === 401) {
          props.handleLogout(
            "/login",
            "error",
            "You have been logged out. Please login create poll."
          );
        } else {
          notification.error({
            message: "Polling App",
            description:
              error.message || "Sorry! Something went wrong. Please try again!",
          });
        }
      });
  };

  const validateQuestion = (questionText) => {
    if (questionText.length === 0) {
      return {
        validateStatus: "error",
        errorMsg: "Please enter your question!",
      };
    } else if (questionText.length > POLL_QUESTION_MAX_LENGTH) {
      return {
        validateStatus: "error",
        errorMsg: `Question is too long (Maximum ${POLL_QUESTION_MAX_LENGTH} characters allowed)`,
      };
    } else {
      return {
        validateStatus: "success",
        errorMsg: null,
      };
    }
  };

  const handleQuestionChange = (event) => {
    const value = event.target.value;
    setQuestion({ text: value, ...validateQuestion(value) });
  };

  const validateChoice = (choiceText) => {
    if (choiceText.length === 0) {
      return {
        validateStatus: "error",
        errorMsg: "Please enter a choice!",
      };
    } else if (choiceText.length > POLL_CHOICE_MAX_LENGTH) {
      return {
        validateStatus: "error",
        errorMsg: `Choice is too long (Maximum ${POLL_CHOICE_MAX_LENGTH} characters allowed)`,
      };
    } else {
      return {
        validateStatus: "success",
        errorMsg: null,
      };
    }
  };

  const handleChoiceChange = (event, index) => {
    const choice = choices.slice();
    const value = event.target.value;
    choice[index] = {
      text: value,
      ...validateChoice(value),
    };
    setChoices(choice);
  };

  const handlePollDaysChange = (value) => {
    setPollLength({ ...pollLength, days: value });
  };

  const handlePollHoursChange = (value) => {
    setPollLength({ ...pollLength, hours: value });
  };

  const isFormInvalid = () => {
    if (question.validateStatus !== "success") {
      return true;
    }

    for (let i = 0; i < choices.length; i++) {
      const choice = choices[i];
      if (choice.validateStatus !== "success") {
        return true;
      }
    }
  };

  const getChoices = () => {
    const choiceViews = [];
    choices.forEach((choice, index) => {
      choiceViews.push(
        <PollChoice
          key={index}
          choice={choice}
          choiceNumber={index}
          handleChoiceChange={handleChoiceChange}
        />
      );
    });
    return choiceViews;
  };

  return (
    <div className="new-poll-container">
      <h1 className="page-title">Create Poll</h1>
      <div className="new-poll-content">
        <Form onSubmit={handleSubmit} className="create-poll-form">
          <FormItem
            validateStatus={question.validateStatus}
            help={question.errorMsg}
            className="poll-form-row"
          >
            <TextArea
              placeholder="Enter your question"
              style={{ fontSize: "16px" }}
              autosize={{ minRows: 3, maxRows: 6 }}
              name="question"
              value={question.text}
              onChange={handleQuestionChange}
            />
          </FormItem>
          {getChoices()}
          <FormItem className="poll-form-row">
            <Col xs={24} sm={4}>
              Poll length:
            </Col>
            <Col xs={24} sm={20}>
              <span style={{ marginRight: "18px" }}>
                <Select
                  name="days"
                  defaultValue="1"
                  onChange={handlePollDaysChange}
                  value={pollLength.days}
                  style={{ width: 60 }}
                >
                  {Array.from(Array(8).keys()).map((i) => (
                    <Option key={i}>{i}</Option>
                  ))}
                </Select>{" "}
                &nbsp;Days
              </span>
              <span>
                <Select
                  name="hours"
                  defaultValue="0"
                  onChange={handlePollHoursChange}
                  value={pollLength.hours}
                  style={{ width: 60 }}
                >
                  {Array.from(Array(24).keys()).map((i) => (
                    <Option key={i}>{i}</Option>
                  ))}
                </Select>{" "}
                &nbsp;Hours
              </span>
            </Col>
          </FormItem>
          <FormItem className="poll-form-row">
            <Button
              type="primary"
              htmlType="submit"
              size="large"
              disabled={isFormInvalid()}
              className="create-poll-form-button"
            >
              Create Poll
            </Button>
          </FormItem>
        </Form>
      </div>
    </div>
  );
}

function PollChoice(props) {
  return (
    <FormItem
      validateStatus={props.choice.validateStatus}
      help={props.choice.errorMsg}
      className="poll-form-row"
    >
      <Input
        placeholder={"Choice " + (props.choiceNumber + 1)}
        size="large"
        value={props.choice.text}
        onChange={(event) =>
          props.handleChoiceChange(event, props.choiceNumber)
        }
      />

    </FormItem>
  );
}
export default NewPoll;