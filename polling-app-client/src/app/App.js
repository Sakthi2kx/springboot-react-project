import React, { useEffect, useState } from "react";
import {Route, Switch, withRouter} from "react-router-dom";
import "./App.css";

import { ACCESS_TOKEN } from "../constants";
import { getCurrentUser } from "../util/APIUtils";

import AppHeader from "../common/AppHeader";
import LoadingIndicator from "../common/LoadingIndicator";
import NotFound from "../common/NotFound";
import NewPoll from "../poll/NewPoll";
import PollList from "../poll/PollList";
import Login from "../user/login/Login";
import Profile from "../user/profile/Profile";
import Signup from "../user/signup/Signup";

import { Layout, notification } from "antd";
const { Content } = Layout;
function App(props) {
  const [currentUser, setCurrentUser] = useState(null);
  const [isAuthenticated, setIsAuthenticated] = useState(false);
  const [isLoading, setIsLoading] = useState(true);

  notification.config({
    placement: "topRight",
    top: 70,
    duration: 3,
  });

  const loadCurrentUser = () => {
    getCurrentUser()
      .then((response) => {
        setCurrentUser(response);
        setIsAuthenticated(true);
        setIsLoading(false);
      })
      .catch((error) => {
        setIsLoading(false);
      });
  };

  const handleLogout = (
    redirectTo = "/",
    notificationType = "success",
    description = "You're successfully logged out."
  ) => {
    localStorage.removeItem(ACCESS_TOKEN);
    setCurrentUser(null);
    setIsAuthenticated(false);
    // navigate(redirectTo);
    props.history.push(redirectTo);

    notification[notificationType]({
      message: "Polling App",
      description: description,
    });
  };

  const handleLogin = () => {
    notification.success({
      message: "Polling App",
      description: "You're successfully logged in.",
    });
    loadCurrentUser();
    props.history.push("/");
  };

  useEffect(() => {
    loadCurrentUser();
  }, []);

  return isLoading ? (
    <LoadingIndicator />
  ) : (
    <Layout className="app-container">
      <AppHeader
        isAuthenticated={isAuthenticated}
        currentUser={currentUser}
        onLogout={handleLogout}
      />

      <Content className="app-content">
        <div className="container">
          <Switch>
            <Route
              exact
              path="/"
                render={(props) => <PollList
                  isAuthenticated={isAuthenticated}
                  currentUser={currentUser}
                  handleLogout={handleLogout}
                  {...props}
                />
              }
            ></Route>
            <Route
              path="/login"
              render={(props) =><Login onLogin={handleLogin} {...props}/>}
            ></Route>
            <Route path="/signup" component={Signup}></Route>
            <Route
              path="/users/:username"
              render={(props) =>
                <Profile
                  isAuthenticated={isAuthenticated}
                  currentUser={currentUser}
                  {...props}
                />
              }
            ></Route>
            {isAuthenticated ? (
              <Route
                path="/poll/new"
                render={props =>
                  <NewPoll
                    handleLogout={handleLogout}
                    authenticated={isAuthenticated}
                    {...props}
                  />
                }
              ></Route>
            ) : (
              <Route
                path="/login"
              ></Route>
            )}
            <Route component={NotFound}></Route>
          </Switch>
        </div>
      </Content>
    </Layout>
  );
}

export default withRouter(App);