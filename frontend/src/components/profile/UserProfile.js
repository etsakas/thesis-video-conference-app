import classes from "./UserProfile.module.css";
import { useState, useEffect, useContext, useCallback, useRef } from "react";
import AuthContext from "../../store/auth-context";

const UserProfile = () => {
  const [userName, setUserName] = useState("");
  const [errorMessage, setErrorMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const [isDeletingAccount, setIsDeletingAccount] = useState(false);

  const authCtx = useContext(AuthContext);

  const logoutHandler = () => {
    authCtx.logout();
  };

  const handleRef = useRef();

  let messageDisableTimer = useCallback(() => {
    if (handleRef.current)
      clearTimeout(handleRef.current)
    handleRef.current = setTimeout(() => setErrorMessage(""), 3000);
  }, []);

  useEffect(() => {
    return () => {clearTimeout(handleRef.current)};
  }, []);

  useEffect(() => {
    const fetchData = async () => {
      setIsLoading(true);
      let response = await fetch("/profile", {
        method: "GET",
        headers: {
          Authorization: "Bearer " + authCtx.token,
        },
      });

      if (!response.ok && response.status === 500) {
        try {
          let error = await response.json();
          setErrorMessage(error.message);
        } catch (ex) {
          setErrorMessage(
            "Unexpected error! Check your internet connection or try again later."
          );
        } finally {
          return;
        }
      } else if (!response.ok) {
        let error = await response.json();
        setErrorMessage(error.message);
        return;
      }

      let responseJson = await response.json();

      console.log(responseJson);

      setUserName(responseJson.username);
      setIsLoading(false);
    };

    fetchData();
  }, [authCtx.token]);

  const deleteRequest = async () => {
    setIsDeletingAccount(true);
    let response = await fetch("/profile", {
      method: "DELETE",
      headers: {
        Authorization: "Bearer " + authCtx.token,
      },
    });

    if (!response.ok && response.status === 500) {
      try {
        let error = await response.json();
        setErrorMessage(error.message);
      } catch (ex) {
        setErrorMessage(
          "Unexpected error! Check your internet connection or try again later."
        );
      } finally {
        setIsDeletingAccount(false);
        messageDisableTimer();
        return;
      }
    } else if (!response.ok) {
      let error = await response.json();
      setErrorMessage(error.message);
      setIsDeletingAccount(false);
      messageDisableTimer();
      return;
    }

    // account was deleted successfully
    let responseJson = await response.json();
    console.log(responseJson);
    logoutHandler();
  };

  return (
    <section className={classes.profile}>
      {isLoading && <h1>Loading data from the server...</h1>}
      {errorMessage && !isLoading && <h1>{errorMessage}</h1>}
      {!errorMessage && !isLoading && <h1>Welcome {userName}!</h1>}
      {!isDeletingAccount && (
        <button onClick={deleteRequest}>Delete account!</button>
      )}
    </section>
  );
};

export default UserProfile;
