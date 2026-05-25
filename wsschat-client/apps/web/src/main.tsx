import ReactDOM from "react-dom/client";
import { RouterProvider } from "react-router";

import "./index.css";
import { router } from "./router";
import StompProvider from "./StompProvider";
import { useEffect, useState } from "react";
import App from "./App";

const rootElement = document.getElementById("app");
(window as any).global = window;

if (!rootElement) {
  throw new Error("Root element not found");
}

// const [username, setUsername] = useState<string | null>("any");

// useEffect(() => {
//   const storedUsername = localStorage.getItem("username");
//   if (storedUsername) {
//     setUsername(storedUsername);
//   } 
// }, []);


ReactDOM.createRoot(rootElement).render(
  <App/>
);
