import useInput from "../hooks/useInput";
import classes from "./SignUpForm.module.css";
import { useState, useEffect, useCallback, useRef } from "react";

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

const validateEmail = (value) => {
  if (value === "") return false;
  if (/\s/.test(value)) return false;
  if (value.length < 5 || value.length > 30) return false;
  if (/[A-ZΑ-ΩΆ-Ώ]/.test(value)) return false;
  const re =
    /^(([^<>()[\]\\.,;:\s@"]+(\.[^<>()[\]\\.,;:\s@"]+)*)|(".+"))@((\[[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\.[0-9]{1,3}\])|(([a-zA-Z\-0-9]+\.)+[a-zA-Z]{2,}))$/;
  return re.test(value);
};

const SignUpForm = () => {
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

  const {
    value: emailValue,
    isValid: emailIsValid,
    hasError: emailHasError,
    valueChangeHandler: emailChangeHandler,
    inputBlurHandler: emailBlurHandler,
    reset: resetEmail,
  } = useInput(validateEmail);

  const [isSubmitting, setIsSubmitting] = useState(false);
  const [submitError, setSubmitError] = useState(false);
  const [submitMessage, setSubmitMessage] = useState(
    <p>Submit message placeholder</p>
  );

  const handleRef = useRef();

  let messageDisableTimer = useCallback(() => {
    if (handleRef.current)
      clearTimeout(handleRef.current)
    handleRef.current = setTimeout(() => setSubmitError(false), 3000);
  }, []);

  useEffect(() => {
    return () => {clearTimeout(handleRef.current)};
  }, []);

  let formIsValid = false;

  if (!nameHasError && !passwordHasError && !emailHasError) formIsValid = true;

  const submitHandler = async (event) => {
    event.preventDefault();

    if (!formIsValid) return;

    setIsSubmitting(true);

    let response = await fetch("/signup", {
      method: "POST",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        username: nameValue,
        password: passwordValue,
        email: emailValue,
      }),
    });

    console.log(response);
    if (!response.ok && response.status === 409) {
      // conflict
      let error = await response.json();
      console.log(error);
      setSubmitError(true);
      setSubmitMessage(<p>{error.message}</p>);
      messageDisableTimer();
    } else if (!response.ok && response.status === 500) {
      // This happens either if user has lost internet connection
      // or something went wrong on the server (like when db is down).
      // How do i know which one happened?
      // Assuming that our backend will send a json as a response,
      // we can use the fact that we can't have json data in case
      // internet connection was lost.
      setSubmitError(true);
      try {
        // this will even happen when db is down
        let error = await response.json();
        setSubmitMessage(<p>{error.message}</p>);
      } catch (ex) {
        // this will happen when spring boot server is down (or no internet connection)
        setSubmitMessage(
          <p>
            Unexpected error! Check your internet connection or try again later.
          </p>
        );
      }
      messageDisableTimer();
    } else if (response.ok) {
      let user = await response.json();
      console.log(user);

      setSubmitError(true);
      setSubmitMessage(<p>Account was created successfully!</p>);
      messageDisableTimer();
      resetName();
      resetPassword();
      resetEmail();
    }

    setIsSubmitting(false);
  };

  const nameClasses = nameHasError
    ? `${classes.input} ${classes.invalid}`
    : `${classes.input}`;
  const passwordClasses = passwordHasError
    ? `${classes.input} ${classes.invalid}`
    : `${classes.input}`;
  const emailClasses = emailHasError
    ? `${classes.input} ${classes.invalid}`
    : `${classes.input}`;

  const isSubmittingMessage = (
    <div className={classes.submitMsg}>Sending order data...</div>
  );

  const signUpForm = (
    <form className={classes.form} onSubmit={submitHandler}>
      <input
        className={nameClasses}
        type="text"
        placeholder="Username"
        maxLength="16"
        value={nameValue}
        onChange={nameChangeHandler}
        onBlur={nameBlurHandler}
        required
      />
      <input
        className={passwordClasses}
        type="password"
        placeholder="Password"
        minLength="8"
        maxLength="16"
        value={passwordValue}
        onChange={passwordChangeHandler}
        onBlur={passwordBlurHandler}
        required
      />
      <input
        className={emailClasses}
        type="email"
        placeholder="Email"
        minLength="5"
        maxLength="30"
        value={emailValue}
        onChange={emailChangeHandler}
        onBlur={emailBlurHandler}
        required
      />
      <button className={classes.button}>Sign up</button>
      {submitError && submitMessage}
    </form>
  );

  const inputInstructions = (
    <div className={classes.inputInfo}>
      <ol>
        <li>
          Username should start with a letter, have no whitespaces and be at
          most 16 characters long
        </li>
        <li>
          Password should have no whitespaces and be between 8 and 16 characters
          long
        </li>
        <li>
          Email should be at most 30 characters long. Capitals are not accepted
        </li>
      </ol>
    </div>
  );

  return (
    <div className={classes.signUpPanel}>
      {!isSubmitting && signUpForm}
      {isSubmitting && isSubmittingMessage}
      {inputInstructions}
    </div>
  );
};

export default SignUpForm;
