import { useRef, useState, useContext, useEffect, useCallback, Fragment } from "react";
import classes from "./JoinSessionPanel.module.css";
import AuthContext from "../../store/auth-context";
import { useHistory } from "react-router-dom";
import VideoPanel from "./VideoPanel";

const JoinSessionPanel = () => {
  const history = useHistory();
  const authCtx = useContext(AuthContext);
  const [wasRedirected, setWasRedirected] = useState(false);
  const sessionNameRef = useRef("");
  const sessionPasswordRef = useRef("");

  const [sessionData, setSessionData] = useState(null);

  const [isSubmitting, setIsSubmitting] = useState(false);
  const [isRedirectFetching, setIsRedirectFetching] = useState(false);

  const [invalidFormMessage, setInvalidFormMessage] = useState(null);
  const [redirectFetchMessage, setRedirectFetchMessage] = useState(null);

  const [isConnected, setIsConnected] = useState(false);

  const formMessageTimerRef = useRef();
  const redirectFetchMessageTimerRef = useRef();

  let formMessageDisableTimer = useCallback(() => {
    if (formMessageTimerRef.current)
      clearTimeout(formMessageTimerRef.current)
      formMessageTimerRef.current = setTimeout(() => setInvalidFormMessage(null), 3000);
  }, []);

  let redirectFetchMessageDisableTimer = useCallback(() => {
    if (redirectFetchMessageTimerRef.current)
      clearTimeout(redirectFetchMessageTimerRef.current)
      redirectFetchMessageTimerRef.current = setTimeout(() => setRedirectFetchMessage(null), 3000);
  }, []);

  useEffect(() => {
    return () => {
      clearTimeout(formMessageTimerRef.current);
      clearTimeout(redirectFetchMessageTimerRef.current);
    };
  }, []);

  useEffect(() => {
    const fetchData = async () => {
      if (history.location !== undefined &&
        history.location.state !== undefined &&
        history.location.state !== null) {
        // then it was redirected after creating a new session
        setWasRedirected(true);
        let sessionName = history.location.state.sessionName;
        let sessionPassword = history.location.state.sessionPassword;
        history.replace(history.location.pathname, null); // reset history state

        let response = await fetch("/sessions/get-session-token", {
          method: "POST",
          headers: {
            Accept: "application/json",
            "Content-Type": "application/json",
            Authorization: "Bearer " + authCtx.token,
          },
          body: JSON.stringify({
            sessionName: sessionName,
            sessionPassword: sessionPassword,
          }),
        });
        if (!response.ok && response.status === 500) {
          try {
            let error = await response.json();
            setRedirectFetchMessage(<p className={classes.errorMsg}>{error.message}</p>);
          } catch (ex) {
            setRedirectFetchMessage(
              <p className={classes.errorMsg}>
                Unexpected error! Check your internet connection or try again
                later.
              </p>
            );
          }
          redirectFetchMessageDisableTimer();
        } else if (!response.ok) {
          let error = await response.json();
          console.log(error);
          setRedirectFetchMessage(<p className={classes.errorMsg}>{error.message}</p>);
          redirectFetchMessageDisableTimer();
        } else {
          let responseObj = await response.json();
          console.log(responseObj);
          setSessionData(responseObj);
        }
      }
    };

    setIsRedirectFetching(true);
    fetchData();
    setIsRedirectFetching(false);
  }, [authCtx.token, history, redirectFetchMessageDisableTimer]);

  const submitHandler = async (event) => {
    event.preventDefault();

    setIsSubmitting(true);

    let sessionName = sessionNameRef.current.value.trim();
    let sessionPassword = sessionPasswordRef.current.value.trim();

    if (sessionName === "" || sessionPassword === "") {
      setInvalidFormMessage(<p>Please fill both fields.</p>);
      formMessageDisableTimer();
      setIsSubmitting(false);
      return;
    }

    let response = await fetch("/sessions/get-session-token", {
      method: "POST",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
        Authorization: "Bearer " + authCtx.token,
      },
      body: JSON.stringify({
        sessionName: sessionName,
        sessionPassword: sessionPassword,
      }),
    });

    if (!response.ok && response.status === 500) {
      try {
        let error = await response.json();
        setInvalidFormMessage(<p>{error.message}</p>);
      } catch (ex) {
        setInvalidFormMessage(
          <p>
            Unexpected error! Check your internet connection or try again later.
          </p>
        );
      }
      formMessageDisableTimer();
    } else if (!response.ok) {
      let error = await response.json();
      console.log(error);
      setInvalidFormMessage(<p>{error.message}</p>);
      formMessageDisableTimer();
    } else {
      let responseObj = await response.json();
      console.log(responseObj);
      setSessionData(responseObj);
    }

    setIsSubmitting(false);
  };

  const sessionJoinForm = (
    <div align="center" style={{ marginTop: "2em" }}>
      <form className={classes.form} onSubmit={submitHandler}>
        <input
          className={classes.input}
          type="text"
          ref={sessionNameRef}
          placeholder="Session name"
          maxLength="16"
          required
        />
        <input
          className={classes.input}
          type="password"
          ref={sessionPasswordRef}
          placeholder="Session password"
          minLength="8"
          maxLength="16"
          required
        />
        <button className={classes.button}>Join Session</button>
        {invalidFormMessage}
      </form>
    </div>
  );

  return (
    <Fragment>
      {!wasRedirected && !isSubmitting && !sessionData && !isConnected && sessionJoinForm}
      {wasRedirected && !isRedirectFetching && !sessionData && !isConnected && sessionJoinForm}
      {redirectFetchMessage}
      {sessionData && <VideoPanel {...sessionData} setIsConnected={setIsConnected} isConnected={isConnected} setSessionData={setSessionData}/>}
    </Fragment>
  );
};

export default JoinSessionPanel;
