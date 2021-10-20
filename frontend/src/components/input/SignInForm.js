import { useRef, useState, useContext, useEffect, useCallback } from "react";
import classes from "./SignInForm.module.css";
import AuthContext from "../../store/auth-context";

const SignInForm = () => {
  const nameRef = useRef("");
  const passwordRef = useRef("");
  const [invalidFormMessage, setInvalidFormMessage] = useState(null);
  const [isSubmitting, setIsSubmitting] = useState(false);

  const authCtx = useContext(AuthContext);

  // Why all this fuss with useRef?
  // Since we decided to use a callback to clear the error message after 3 seconds
  // we need to make sure that this callback that changes the state of this component
  // will only run if the component is mounted. If the component gets unmount then
  // we need to disable the callback. This is done using the return inside the useEffect
  const handleRef = useRef();

  let messageDisableTimer = useCallback(() => {
    if (handleRef.current)
      clearTimeout(handleRef.current)
    handleRef.current = setTimeout(() => setInvalidFormMessage(null), 3000);
  }, []);

  useEffect(() => {
    return () => {clearTimeout(handleRef.current)};
  }, []);

  const submitHandler = async (event) => {
    event.preventDefault();
    setIsSubmitting(true);
    let name = nameRef.current.value.trim();
    let password = passwordRef.current.value.trim();

    if (name === "" || password === "") {
      setInvalidFormMessage(<p>Please fill both fields.</p>);
      messageDisableTimer();
      setIsSubmitting(false);
      return;
    }

    let response = await fetch("/signin", {
      method: "POST",
      headers: {
        Accept: "application/json",
        "Content-Type": "application/json",
      },
      body: JSON.stringify({
        username: name,
        password: password,
      }),
    });

    if (!response.ok && response.status === 500) {
      try {
        let error = await response.json();
        setInvalidFormMessage(<p>{error.message}</p>);
      } catch (ex) {
        // happends also when spring boot server is down
        setInvalidFormMessage(
          <p>
            Unexpected error! Check your internet connection or try again later.
          </p>
        );
      }
      messageDisableTimer();
      setIsSubmitting(false);
    } else if (!response.ok) {
      let error = await response.json();
      console.log(error);
      setInvalidFormMessage(<p>{error.message}</p>);
      messageDisableTimer();
      setIsSubmitting(false);
    } else {
      let token_obj = await response.json();
      console.log(token_obj);

      const expirationTime = new Date(
        new Date().getTime() + parseInt(token_obj.expiresIn)
      );
      setIsSubmitting(false);
      authCtx.login(token_obj.token, expirationTime.toISOString());
    }

  };

  return (
    <div align="center" style={{ marginTop: "2em" }}>
      <form className={classes.form} onSubmit={submitHandler}>
        <input
          className={classes.input}
          type="text"
          ref={nameRef}
          placeholder="Username"
          maxLength="16"
          required
        />
        <input
          className={classes.input}
          type="password"
          ref={passwordRef}
          placeholder="Password"
          minLength="8"
          maxLength="16"
          required
        />
        {!isSubmitting && <button className={classes.button}>Sign in</button>}
        {invalidFormMessage}
      </form>
    </div>
  );
};

export default SignInForm;
