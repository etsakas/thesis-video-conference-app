import { Route, Switch, Redirect } from "react-router-dom";
import { useContext } from 'react';
import SignIn from "./pages/SignIn";
import SignUp from "./pages/SignUp";
import Profile from "./pages/Profile";
import NotFound from "./pages/NotFound";
import CreateSession from "./pages/CreateSession";
import JoinSession from "./pages/JoinSession";
import Layout from "./components/layout/Layout";
import AuthContext from './store/auth-context';

function App() {
  const authCtx = useContext(AuthContext);

  let isLoggedIn = authCtx.isLoggedIn;

  return (
    <Layout>
      <Switch>
        <Route path="/" exact>
          {!isLoggedIn && <Redirect to="/signin" />}
          {isLoggedIn && <Redirect to="/profile" />}
        </Route>

        <Route path="/signin" exact>
          {!isLoggedIn && <SignIn />}
          {isLoggedIn && <Redirect to="/profile" />}
        </Route>)

        <Route path="/signup" exact>
          {!isLoggedIn && <SignUp />}
          {isLoggedIn && <Redirect to="/profile" />}  
        </Route>)

        <Route path="/profile" exact>
          {!isLoggedIn && <Redirect to="/signin" />}
          {isLoggedIn && <Profile />}
        </Route>)

        <Route path="/create-session" exact>
          {!isLoggedIn && <Redirect to="/signin" />}
          {isLoggedIn && <CreateSession />}
        </Route>)
      
        <Route path="/join-session" exact>
          {!isLoggedIn && <Redirect to="/signin" />}
          {isLoggedIn && <JoinSession />}
        </Route>)

        <Route path="*">
          <NotFound />
        </Route>
        
      </Switch>
    </Layout>
  );
}

export default App;
