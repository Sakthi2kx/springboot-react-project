import React, { Component, useState } from "react";
import {
  signup,
  checkUsernameAvailability,
  checkEmailAvailability,
} from "../../util/APIUtils";
import "./Signup.css";
import { Link, useNavigate } from "react-router-dom";
import {
  NAME_MIN_LENGTH,
  NAME_MAX_LENGTH,
  USERNAME_MIN_LENGTH,
  USERNAME_MAX_LENGTH,
  EMAIL_MAX_LENGTH,
  PASSWORD_MIN_LENGTH,
  PASSWORD_MAX_LENGTH,
} from "../../constants";

import { Form, Input, Button, notification } from "antd";
const FormItem = Form.Item;

function Signup() {
  const navigate = useNavigate();
  const [name , setName] = useState({ value: "" });
  const[username,setUsername] = useState({value:""})
  const [email, setEmail] = useState({ value: "" });
  const [password, setPassword] = useState({ value: "" });

  const handleSubmit = (event) => {
    event.preventDefault();

    const signupRequest = {
      name: name.value,
      email: email.value,
      username: username.value,
      password: password.value,
    };
    signup(signupRequest)
      .then((response) => {
        notification.success({
          message: "Polling App",
          description:
            "Thank you! You're successfully registered. Please Login to continue!",
        });
        navigate("/login");
      })
      .catch((error) => {
        notification.error({
          message: "Polling App",
          description:
            error.message || "Sorry! Something went wrong. Please try again!",
        });
      });
  };

  const handleInputChange = (event, validationFun) => {
    const field = event.target.name;
    if(field === 'name'){
      setName({value: event.target.value,
        ...validationFun(event.target.value),})
    }else if(field === 'username'){
      setUsername({value: event.target.value,
        ...validationFun(event.target.value),})
    }else if(field === "password"){
      setPassword({value: event.target.value,
        ...validationFun(event.target.value),})
    }else{
      setEmail({value: event.target.value,
        ...validationFun(event.target.value),})
    }
  };

  const isFormInvalid = () => {
    return !(
      name.validateStatus === "success" &&
      username.validateStatus === "success" &&
      email.validateStatus === "success" &&
      password.validateStatus === "success"
    );
  };

  // Validation Functions

  const validateName = (name) => {
    if (name.length < NAME_MIN_LENGTH) {
      return {
        validateStatus: "error",
        errorMsg: `Name is too short (Minimum ${NAME_MIN_LENGTH} characters needed.)`,
      };
    } else if (name.length > NAME_MAX_LENGTH) {
      return {
        validationStatus: "error",
        errorMsg: `Name is too long (Maximum ${NAME_MAX_LENGTH} characters allowed.)`,
      };
    } else {
      return {
        validateStatus: "success",
        errorMsg: null,
      };
    }
  };

  const validateEmail = (email) => {
    if (!email) {
      return {
        validateStatus: "error",
        errorMsg: "Email may not be empty",
      };
    }

    const EMAIL_REGEX = RegExp("[^@ ]+@[^@ ]+\\.[^@ ]+");
    if (!EMAIL_REGEX.test(email)) {
      return {
        validateStatus: "error",
        errorMsg: "Email not valid",
      };
    }

    if (email.length > EMAIL_MAX_LENGTH) {
      return {
        validateStatus: "error",
        errorMsg: `Email is too long (Maximum ${EMAIL_MAX_LENGTH} characters allowed)`,
      };
    }

    return {
      validateStatus: null,
      errorMsg: null,
    };
  };

  const validateUsername = (username) => {
    if (username.length < USERNAME_MIN_LENGTH) {
      return {
        validateStatus: "error",
        errorMsg: `Username is too short (Minimum ${USERNAME_MIN_LENGTH} characters needed.)`,
      };
    } else if (username.length > USERNAME_MAX_LENGTH) {
      return {
        validationStatus: "error",
        errorMsg: `Username is too long (Maximum ${USERNAME_MAX_LENGTH} characters allowed.)`,
      };
    } else {
      return {
        validateStatus: null,
        errorMsg: null,
      };
    }
  };

  const validateUsernameAvailability = () => {
    // First check for client side errors in username
    const usernameValue = username.value;
    const usernameValidation = validateUsername(usernameValue);

    if (usernameValidation.validateStatus === "error") {
      setUsername({
          value: usernameValue,
          ...usernameValidation,
      });
      return;
    }

    setUsername({
        value: usernameValue,
        validateStatus: "validating",
        errorMsg: null,
    });

    checkUsernameAvailability(usernameValue)
      .then((response) => {
        if (response.available) {
          setUsername({
              value: usernameValue,
              validateStatus: "success",
              errorMsg: null,
          });
        } else {
          setUsername({
              value: usernameValue,
              validateStatus: "error",
              errorMsg: "This username is already taken",
          });
        }
      })
      .catch((error) => {
        // Marking validateStatus as success, Form will be recchecked at server
        setUsername({
            value: usernameValue,
            validateStatus: "success",
            errorMsg: null,
        });
      });
  };

  const validateEmailAvailability = () => {
    // First check for client side errors in email
    const emailValue = email.value;
    const emailValidation = validateEmail(emailValue);

    if (emailValidation.validateStatus === "error") {
      setEmail({
          value: emailValue,
          ...emailValidation,
      });
      return;
    }

    setEmail({
        value: emailValue,
        validateStatus: "validating",
        errorMsg: null,
    });

    checkEmailAvailability(emailValue)
      .then((response) => {
        if (response.available) {
          setEmail({
              value: emailValue,
              validateStatus: "success",
              errorMsg: null,
          });
        } else {
          setEmail({
              value: emailValue,
              validateStatus: "error",
              errorMsg: "This Email is already registered",
          });
        }
      })
      .catch((error) => {
        // Marking validateStatus as success, Form will be recchecked at server
        setEmail({
            value: emailValue,
            validateStatus: "success",
            errorMsg: null,
        });
      });
  };

  const validatePassword = (password) => {
    if (password.length < PASSWORD_MIN_LENGTH) {
      return {
        validateStatus: "error",
        errorMsg: `Password is too short (Minimum ${PASSWORD_MIN_LENGTH} characters needed.)`,
      };
    } else if (password.length > PASSWORD_MAX_LENGTH) {
      return {
        validationStatus: "error",
        errorMsg: `Password is too long (Maximum ${PASSWORD_MAX_LENGTH} characters allowed.)`,
      };
    } else {
      return {
        validateStatus: "success",
        errorMsg: null,
      };
    }
  };
  return (
    <div className="signup-container">
      <h1 className="page-title">Sign Up</h1>
      <div className="signup-content">
        <Form onSubmit={handleSubmit} className="signup-form">
          <FormItem
            label="Full Name"
            validateStatus={name.validateStatus}
            help={name.errorMsg}
          >
            <Input
              size="large"
              name="name"
              autoComplete="off"
              placeholder="Your full name"
              value={name.value}
              onChange={(event) => handleInputChange(event, validateName)}
            />
          </FormItem>
          <FormItem
            label="Username"
            hasFeedback
            validateStatus={username.validateStatus}
            help={username.errorMsg}
          >
            <Input
              size="large"
              name="username"
              autoComplete="off"
              placeholder="A unique username"
              value={username.value}
              onBlur={validateUsernameAvailability}
              onChange={(event) => handleInputChange(event, validateUsername)}
            />
          </FormItem>
          <FormItem
            label="Email"
            hasFeedback
            validateStatus={email.validateStatus}
            help={email.errorMsg}
          >
            <Input
              size="large"
              name="email"
              type="email"
              autoComplete="off"
              placeholder="Your email"
              value={email.value}
              onBlur={validateEmailAvailability}
              onChange={(event) =>
                handleInputChange(event, validateEmail)
              }
            />
          </FormItem>
          <FormItem
            label="Password"
            validateStatus={password.validateStatus}
            help={password.errorMsg}
          >
            <Input
              size="large"
              name="password"
              type="password"
              autoComplete="off"
              placeholder="A password between 6 to 20 characters"
              value={password.value}
              onChange={(event) => handleInputChange(event, validatePassword)}
            />
          </FormItem>
          <FormItem>
            <Button
              type="primary"
              htmlType="submit"
              size="large"
              className="signup-form-button"
              disabled={isFormInvalid()}
            >
              Sign up
            </Button>
            Already registed? <Link to="/login">Login now!</Link>
          </FormItem>
        </Form>
      </div>
    </div>
  );
}

export default Signup;