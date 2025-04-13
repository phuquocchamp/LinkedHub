import { FormEvent, useState } from "react";
import Box from "../../components/Box/Box";
import Button from "../../components/Button/Button";
import Input from "../../components/Input/Input";
import Layout from "../../components/Layout/Layout";
import classes from "./ResetPassword.module.scss";
import { useNavigate } from "react-router-dom";

export default function ResetPassword() {
  const navigate = useNavigate();
  const [email, setEmail] = useState("");
  const [emailSent, setEmailSent] = useState(false);
  const [isLoading, setIsLoading] = useState(false);
  const [errorMessage, setErrorMessage] = useState("");

  const sendEmailResetPasswordToken = async (email: string) => {
    try {
      const response = await fetch(
        `${
          import.meta.env.VITE_API_URL
        }/api/v1/auth/send-password-reset-token?email=${email}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
        }
      );

      if (!response.ok) {
        const { message } = await response.json();
        setErrorMessage(message);
      }

      if (response.ok) {
        setErrorMessage("");
        setEmailSent(true);
      }
    } catch (error) {
      setErrorMessage("An error occurred while sending the email.");
    } finally {
      setIsLoading(false);
    }
  };

  const resetPassword = async (
    email: string,
    code: string,
    newPassword: string
  ) => {
    setErrorMessage("");
    try {
      const response = await fetch(
        `${
          import.meta.env.VITE_API_URL
        }/api/v1/auth/reset-password?email=${email}&newPassword=${newPassword}&token=${code}`,
        {
          method: "PUT",
          headers: {
            "Content-Type": "application/json",
          },
          body: JSON.stringify({ newPassword }),
        }
      );

      if (!response.ok) {
        const { message } = await response.json();
        setErrorMessage(message);
      }

      if (response.ok) {
        setErrorMessage("");
        navigate("/login");
      }
    } catch (error) {
      setErrorMessage("An error occurred while verifying the email.");
    } finally {
      setIsLoading(false);
    }
  };

  return (
    <Layout className={classes.root}>
      <Box>
        <h1>Reset Password</h1>
        <p>
          Enter the verification code sent to your email address and your new
          password
        </p>
        {!emailSent ? (
          <form
            onSubmit={async (e) => {
              e.preventDefault();
              setIsLoading(true);
              const email = e.currentTarget.email.value;
              setEmail(email);
              await sendEmailResetPasswordToken(email);
            }}
          >
            <Input
              type="email"
              id="email"
              label="Email"
              name="email"
              onFocus={() => setErrorMessage("")}
            />
            {errorMessage && <p className={classes.error}>{errorMessage}</p>}

            <Button type="submit" disabled={isLoading}>
              Send Verification Code
            </Button>

            <Button
              type="button"
              outline={true}
              onClick={() => navigate("/login")}
            >
              Back to Login
            </Button>
          </form>
        ) : (
          <form
            onSubmit={async (e) => {
              e.preventDefault();
              setIsLoading(true);
              const code = e.currentTarget.code.value;
              const newPassword = e.currentTarget["new-password"].value;
              await resetPassword(email, code, newPassword);
              setIsLoading(false);
            }}
          >
            <Input
              type="text"
              id="code"
              key="code"
              label="Code Verification"
              onFocus={() => setErrorMessage("")}
            />
            {errorMessage && <p className={classes.error}>{errorMessage}</p>}

            <Input
              type="password"
              id="new-password"
              key="new-password"
              label="New Password"
              onFocus={() => setErrorMessage("")}
            />

            <Button type="submit">Reset Password</Button>
            <Button
              type="button"
              outline={true}
              onClick={() => {
                setErrorMessage("");
                setEmail("");
                setEmailSent(false);
              }}
            >
              Back
            </Button>
          </form>
        )}
      </Box>
    </Layout>
  );
}
