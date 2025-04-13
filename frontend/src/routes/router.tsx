import { createBrowserRouter } from "react-router-dom";
import Feed from "../features/feed/pages/Feed";
import Login from "../features/authentication/pages/Login/Login";
import Signup from "../features/authentication/pages/Signup/Signup";
import ResetPassword from "../features/authentication/pages/ResetPassword/ResetPassword";
import VerifyEmail from "../features/authentication/pages/VerifyEmail/VerifyEmail";
import AuthenticationContextProvider from "../features/authentication/contexts/AuthenticationContextProvider";

const router = createBrowserRouter([
  {
    element: <AuthenticationContextProvider />,
    children: [
      {
        path: "/",
        element: <Feed />,
      },
      {
        path: "/login",
        element: <Login />,
      },
      {
        path: "/signup",
        element: <Signup />,
      },
      {
        path: "/reset-password",
        element: <ResetPassword />,
      },
      {
        path: "/verify-email",
        element: <VerifyEmail />,
      },
    ],
  },
]);

export default router;
