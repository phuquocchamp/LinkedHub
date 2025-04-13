import { Link, useLocation, useNavigate } from "react-router-dom";
import Box from "../../components/Box/Box";
import Button from "../../components/Button/Button";
import Input from "../../components/Input/Input";
import Layout from "../../components/Layout/Layout";
import Separator from "../../components/Separator/Separator";
import classes from "./Login.module.scss";
import { FormEvent, useState } from "react";
import { useAuthenticationContext } from "../../contexts/AuthenticationContextProvider";

export default function Login() {
  const [errorMessage, setErrorMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const { login } = useAuthenticationContext();
  const navigate = useNavigate();
  const location = useLocation();

  const doLogin = async (e: FormEvent<HTMLFormElement>) => {
    e.preventDefault();
    setIsLoading(true);
    const email = e.currentTarget.email.value;
    const password = e.currentTarget.password.value;

    try {
      await login(email, password);
      const destination = location.state?.from || "/";
      navigate(destination);
    } catch (error) {
      if (error instanceof Error) {
        setErrorMessage(error.message);
      } else {
        setErrorMessage("An unknown error occurred");
      }
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Layout className={classes.root}>
      <Box>
        <h1>Sign In</h1>
        <p>Stay updated on your professional world</p>
        <form onSubmit={doLogin}>
          <Input
            type="email"
            id="email"
            label="Email"
            onFocus={() => setErrorMessage("")}
          />
          <Input
            type="password"
            id="password"
            label="Password"
            onFocus={() => setErrorMessage("")}
          />
          {errorMessage && <p className={classes.error}>{errorMessage}</p>}
          <Button type="submit" disabled={isLoading}>
            {isLoading ? "..." : "Sign In"}
          </Button>
          <Link to="/reset-password">Forgot Password?</Link>
        </form>

        <Separator>Or</Separator>
        <div className={classes.signin}>
          Don't have an account? <Link to="/signup">Sign Up</Link>
        </div>
      </Box>
    </Layout>
  );
}
