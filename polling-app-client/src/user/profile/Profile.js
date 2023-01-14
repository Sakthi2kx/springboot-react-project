import { Avatar } from "antd";
import React, { useEffect, useState } from "react";
import LoadingIndicator from "../../common/LoadingIndicator";
import NotFound from "../../common/NotFound";
import ServerError from "../../common/ServerError";
import { getUserProfile } from "../../util/APIUtils";
import { getAvatarColor } from "../../util/Colors";
import { formatDate } from "../../util/Helpers";
import "./Profile.css";

function Profile(props) {
  const [user, setUser] = useState(null);
  const [isLoading, setIsLoading] = useState(false);
  const [notFound, setNotFound] = useState(false);
  const [serverError, setServerError] = useState(false);

  const loadUserProfile = (username) => {
    setIsLoading(true);

    getUserProfile(username)
      .then((response) => {
        setUser(response);
        setIsLoading(false);
      })
      .catch((error) => {
        if (error.status === 404) {
          setNotFound(true);
          setIsLoading(false);
        } else {
          setServerError(true);
          setIsLoading(false);
        }
      });
  };

  useEffect(() => {
    const username = props.match.params.username;
    loadUserProfile(username);
  }, [props.match.params.username]);

  //   useEffect((nextProps) => {
  //     if (params.username !== nextProps.params.username) {
  //       loadUserProfile(nextProps.username);
  //     }
  //   });

  return isLoading ? (
    <LoadingIndicator />
  ) : notFound ? (
    <NotFound />
  ) : serverError ? (
    <ServerError />
  ) : (
    <div className="profile">
      {user ? (
        <div className="user-profile">
          <div className="user-details">
            <div className="user-avatar">
              <Avatar
                className="user-avatar-circle"
                style={{
                  backgroundColor: getAvatarColor(user.name),
                }}
              >
                {user.name[0].toUpperCase()}
              </Avatar>
            </div>
            <div className="user-summary">
              <div className="full-name">{user.name}</div>
              <div className="username">@{user.username}</div>
              <div className="user-joined">
                Joined {formatDate(user.joinedAt)}
              </div>
            </div>
          </div>
        </div>
      ) : null}
    </div>
  );
}

export default Profile;


