import { useNavigate } from "react-router-dom";
import Box from "../../components/Box/Box";
import Button from "../../components/Button/Button";
import Input from "../../components/Input/Input";
import Layout from "../../components/Layout/Layout";
import classes from "./VerifyEmail.module.scss";
import { useState } from "react";

export default function VerifyEmail() {
  const [errorMessage, setErrorMessage] = useState("");
  const [message, setMessage] = useState("");
  const [isLoading, setIsLoading] = useState(false);
  const navigate = useNavigate();

  const validateEmail = async (code: string) => {
    setMessage("");
    try {
      const response = await fetch(
        `${
          import.meta.env.VITE_API_URL
        }/api/v1/auth/validate-email-verification-token?token=${code}`,
        {
          method: "PUT",
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );

      if (!response.ok) {
        const { message } = await response.json();
        setErrorMessage(message);
      }

      if (response.ok) {
        setErrorMessage("");
        navigate("/");
      }
    } catch (error) {
      setErrorMessage("An error occurred while verifying the email.");
    } finally {
      setIsLoading(false);
    }
  };

  const sendEmailVerificationToken = async () => {
    setMessage("");
    try {
      const response = await fetch(
        `${
          import.meta.env.VITE_API_URL
        }/api/v1/auth/send-email-verification-token`,
        {
          method: "GET",
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );

      if (!response.ok) {
        const { message } = await response.json();
        setErrorMessage(message);
      }

      if (response.ok) {
        setErrorMessage("");
        setMessage("Email verification code sent successfully.");
      }
    } catch (error) {
      setErrorMessage("An error occurred while sending the email.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Layout className={classes.root}>
      <Box>
        <h1>Verify Email</h1>
        <p>
          We've sent a verification code to your email address. Please check
          your inbox and click the link to continue.
        </p>
        <form
          onSubmit={async (e) => {
            e.preventDefault();
            setIsLoading(true);
            const code = e.currentTarget.code.value;
            await validateEmail(code);
            setIsLoading(false);
          }}
        >
          <Input id="code" label="Code Verification"></Input>
          {errorMessage && <p className={classes.error}>{errorMessage}</p>}
          {message && <p className={classes.message}>{message}</p>}
          <Button type="submit" disabled={isLoading}>
            Validate Email
          </Button>
          <Button
            type="button"
            outline={true}
            disabled={isLoading}
            onClick={() => sendEmailVerificationToken()}
          >
            Send Again
          </Button>
        </form>
      </Box>
    </Layout>
  );
}
