import useInput from "../hooks/useInput";
import classes from "./CreateSessionForm.module.css";
import AuthContext from "../../store/auth-context";
import { useState, useContext, useRef, useEffect, useCallback } from "react";
import { useHistory } from "react-router-dom";

const validateName = (value) => {
  if (value === "") return false;
  // first character is a letter
  let firstChar = value.charAt(0);
  if (firstChar.toLowerCase() === firstChar.toUpperCase()) return false;
  // no whitespaces
  if (/\s/.test(value)) return false;
  if (value.length > 16) return false;
  return true;
};

const validatePassword = (value) => {
  if (value === "") return false;
  if (/\s/.test(value)) return false;
  if (value.length < 8 || value.length > 16) return false;
  return true;
};

const CreateSessionForm = () => {
  const history = useHistory();
  const authCtx = useContext(AuthContext);

  const {
    value: nameValue,
    isValid: nameIsValid,
    hasError: nameHasError,
    valueChangeHandler: nameChangeHandler,
    inputBlurHandler: nameBlurHandler,
    reset: resetName,
  } = useInput(validateName);

  const {
    value: passwordValue,
    isValid: passwordIsValid,
    hasError: passwordHasError,
    valueChangeHandler: passwordChangeHandler,
    inputBlurHandler: passwordBlurHandler,
    reset: resetPassword,
  } = useInput(validatePassword);

  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitMessageIsVisible, setSubmitMessageIsVisible] = useState(false);
  const [submitMessage, setSubmitMessage] = useState(
    <p>Submit message placeholder</p>
  );

  const disableMessageTimerRef = useRef();
  const redirectTimerRef = useRef();

  let messageDisableTimer = useCallback(() => {
    if (disableMessageTimerRef.current)
      clearTimeout(disableMessageTimerRef.current);
    disableMessageTimerRef.current = setTimeout(
      () => setSubmitMessageIsVisible(false),
      3000
    );
  }, []);

  let redirectTimer = useCallback(() => {
    if (redirectTimerRef.current) clearTimeout(redirectTimerRef.current);
    redirectTimerRef.current = setTimeout(() => {
      history.replace({
        pathname: "/join-session",
        state: { sessionName: nameValue, sessionPassword: passwordValue },
      });
    }, 3000);
  }, [history, nameValue, passwordValue]);

  useEffect(() => {
    return () => {
      clearTimeout(disableMessageTimerRef.current);
      clearTimeout(redirectTimerRef.current);
    };
  }, []);

  let formIsValid = false;

  if (!nameHasError && !passwordHasError) formIsValid = true;

  const submitHandler = async (event) => {
    event.preventDefault();

    if (!formIsValid) return;

    setIsSubmitting(true);

    let response = await fetch("/sessions/create-session", {
      method: "POST",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
        Authorization: "Bearer " + authCtx.token,
      },
      body: JSON.stringify({
        sessionName: nameValue,
        sessionPassword: passwordValue,
      }),
    });

    console.log(response);
    if (!response.ok && response.status === 409) {
      // conflict
      let error = await response.json();
      console.log(error);
      setSubmitMessageIsVisible(true);
      setSubmitMessage(<p>{error.message}</p>);
      messageDisableTimer();
    } else if (!response.ok && response.status === 500) {
      setSubmitMessageIsVisible(true);
      try {
        let error = await response.json();
        setSubmitMessage(<p>{error.message}</p>);
      } catch (ex) {
        setSubmitMessage(
          <p>
            Unexpected error! Check your internet connection or try again later.
          </p>
        );
      }
      messageDisableTimer();
    } else if (response.ok) {
      setSubmitMessageIsVisible(true);
      setSubmitMessage(<p>Session was created successfully! Redirecting...</p>);
      // Redirect timer takes 3 seconds to execute. That is enough time for the
      // submitHandler to finish. If it was executed immediately then we would
      // get the Can't perform a React state update on an unmounted component error
      // on our console because of the setState lines that exist AFTER this redirection
      // command. We would just have to place every setState command above redirectTimer();
      redirectTimer();
      resetName();
      resetPassword();
    }

    setIsSubmitting(false);
  };

  const nameClasses = nameHasError
    ? `${classes.input} ${classes.invalid}`
    : `${classes.input}`;
  const passwordClasses = passwordHasError
    ? `${classes.input} ${classes.invalid}`
    : `${classes.input}`;

  const isSubmittingMessage = (
    <div className={classes.submitMsg}>Sending order data...</div>
  );

  const createSessionForm = (
    <form className={classes.form} onSubmit={submitHandler}>
      <input
        className={nameClasses}
        type="text"
        placeholder="Session name"
        maxLength="16"
        value={nameValue}
        onChange={nameChangeHandler}
        onBlur={nameBlurHandler}
        required
      />
      <input
        className={passwordClasses}
        type="password"
        placeholder="Session password"
        minLength="8"
        maxLength="16"
        value={passwordValue}
        onChange={passwordChangeHandler}
        onBlur={passwordBlurHandler}
        required
      />
      <button className={classes.button}>Create Session</button>
      {submitMessageIsVisible && submitMessage}
    </form>
  );

  const inputInstructions = (
    <div className={classes.inputInfo}>
      <ol>
        <li>
          Session name should start with a letter, have no whitespaces and be at
          most 16 characters long
        </li>
        <li>
          Session password should have no whitespaces and be between 8 and 16
          characters long
        </li>
      </ol>
    </div>
  );

  return (
    <div className={classes.createSessionPanel}>
      {!isSubmitting && createSessionForm}
      {isSubmitting && isSubmittingMessage}
      {inputInstructions}
    </div>
  );
};

export default CreateSessionForm;
