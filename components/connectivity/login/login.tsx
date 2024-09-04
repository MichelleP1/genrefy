import React from "react";
import { Button } from "@mui/material";
import styles from "./login.module.scss";

export const Login = () => {
  return (
    <div className="App">
      <header className="App-header">
        <Button href="/api/auth/login" className={styles.button}>
          Login with Spotify
        </Button>
      </header>
    </div>
  );
};
