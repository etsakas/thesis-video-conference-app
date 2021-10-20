import { NavLink } from "react-router-dom";
import { useContext } from 'react';
import AuthContext from '../../store/auth-context';
import classes from "./MainNavigation.module.css";

const MainNavigation = () => {
  const authCtx = useContext(AuthContext);

  const isLoggedIn = authCtx.isLoggedIn;

  const logoutHandler = () => {
    authCtx.logout();
  };

  return (
    <header className={classes.header}>
      <div className={classes.logo}>Roomis</div>
      <nav className={classes.nav}>
        <ul>
          {isLoggedIn && (
          <li>
            <NavLink activeClassName={classes.active} to="/create-session">Create</NavLink>
          </li>)}
          {isLoggedIn && (
          <li>
            <NavLink activeClassName={classes.active} to="/join-session">Join</NavLink>
          </li>)}
          {isLoggedIn && (
          <li>
            <NavLink activeClassName={classes.active} to="/profile">Profile</NavLink>
          </li>)}
          {isLoggedIn && (
          <li>
            <button onClick={logoutHandler}>Logout</button>
          </li>)}
          {!isLoggedIn && (
          <li>
            <NavLink activeClassName={classes.active} to="/signin">Sign In</NavLink>
          </li>)}
          {!isLoggedIn && (
          <li>
            <NavLink activeClassName={classes.active} to="/signup">Sign Up</NavLink>
          </li>)}
        </ul>
      </nav>
    </header>
  );
};

export default MainNavigation;
