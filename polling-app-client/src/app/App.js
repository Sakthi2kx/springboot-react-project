import React, { useEffect, useState } from "react";
import { Navigate, Route, Routes, useNavigate } from "react-router-dom";
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
function App() {
  let navigate = useNavigate();
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
    navigate(redirectTo);

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
    navigate("/");
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
          <Routes>
            <Route
              exact
              path="/"
              element={
                <PollList
                  isAuthenticated={isAuthenticated}
                  currentUser={currentUser}
                  handleLogout={handleLogout}
                />
              }
            ></Route>
            <Route
              path="/login"
              element={<Login onLogin={handleLogin} />}
            ></Route>
            <Route path="/signup" element={<Signup />}></Route>
            <Route
              path="/users/:username"
              element={
                <Profile
                  isAuthenticated={isAuthenticated}
                  currentUser={currentUser}
                />
              }
            ></Route>
            {isAuthenticated ? (
              <Route
                path="/poll/new"
                element={
                  <NewPoll
                    handleLogout={handleLogout}
                    authenticated={isAuthenticated}
                  />
                }
              ></Route>
            ) : (
              <Route
                path="/"
                element={<Navigate replace to="/login" />}
              ></Route>
            )}
            <Route element={NotFound}></Route>
          </Routes>
        </div>
      </Content>
    </Layout>
  );
}

export default App;