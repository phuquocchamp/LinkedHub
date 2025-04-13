import { createContext, useContext, useEffect, useState } from "react";
import { Navigate, Outlet, useLocation } from "react-router-dom";
import Loader from "../../../components/Loader/Loader";

interface User {
  id: string;
  email: string;
  emailVerified: boolean;
}

interface AuthenticationContextType {
  user: User | null;
  login: (email: string, password: string) => Promise<void>;
  signup: (email: string, password: string) => Promise<void>;
  logout: () => Promise<void>;
}

const AuthenticationContext = createContext<AuthenticationContextType | null>(
  null
);

export function useAuthenticationContext() {
  const context = useContext(AuthenticationContext);
  if (!context) {
    throw new Error(
      "useAuthenticationContext must be used within a AuthenticationContextProvider"
    );
  }
  return context;
}

export default function AuthenticationContextProvider() {
  const [user, setUser] = useState<User | null>(null);
  const [isLoading, setIsLoading] = useState(true);
  const location = useLocation();

  const isOnAuthPage =
    location.pathname === "/login" ||
    location.pathname === "/signup" ||
    location.pathname === "/reset-password" ||
    location.pathname === "/verify-email";

  const login = async (email: string, password: string) => {
    const response = await fetch(
      import.meta.env.VITE_API_URL + "/api/v1/auth/login",
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
      }
    );

    if (!response.ok) {
      const { message } = await response.json();
      throw new Error(message);
    }

    const { token } = await response.json();
    console.log(token);
    localStorage.setItem("token", token);
  };

  const signup = async (email: string, password: string) => {
    const response = await fetch(
      import.meta.env.VITE_API_URL + "/api/v1/auth/register",
      {
        method: "POST",
        headers: {
          "Content-Type": "application/json",
        },
        body: JSON.stringify({ email, password }),
      }
    );

    if (!response.ok) {
      const { message } = await response.json();
      throw new Error(message);
    }

    const { token } = await response.json();
    localStorage.setItem("token", token);
  };

  const logout = async () => {
    localStorage.removeItem("token");
    setUser(null);
  };

  const fetchUser = async () => {
    try {
      const response = await fetch(
        import.meta.env.VITE_API_URL + "/api/v1/auth/user",
        {
          headers: {
            Authorization: `Bearer ${localStorage.getItem("token")}`,
          },
        }
      );

      if (!response.ok) {
        throw new Error("Failed to fetch user");
      }

      const user = await response.json();
      console.log(user);
      setUser(user);
    } catch (e) {
      console.error(e);
    } finally {
      setIsLoading(false);
    }
  };

  useEffect(() => {
    if (user) return;
    fetchUser();
  }, [user, location.pathname]);

  if (isLoading) {
    return <Loader />;
  }
  if (!isLoading && !user && !isOnAuthPage) {
    return <Navigate to="/login" />;
  }

  if (user && user?.emailVerified && isOnAuthPage) {
    return <Navigate to="/" />;
  }

  return (
    <AuthenticationContext.Provider value={{ user, login, signup, logout }}>
      {user && !user.emailVerified ? <Navigate to="/verify-email" /> : null}
      <Outlet />
    </AuthenticationContext.Provider>
  );
}
