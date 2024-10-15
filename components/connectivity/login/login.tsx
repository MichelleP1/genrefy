import React, { useEffect } from "react";
import { Button } from "@mui/material";
import styles from "./login.module.scss";
import logo from "./logo.svg";
import Image from "next/image";

export const Login = () => {
  return (
    <div className={styles.container}>
      <header className={styles.centered}>
        <h1 className={styles.title}>Welcome to Genrefy</h1>
        <Image src={logo} width="100" height="100" alt="Spotify logo" />

        <Button href="/api/auth/login" className={styles.loginButton}>
          Login with Spotify
        </Button>
        <div className={styles.policy}>
          <h4>Privacy policy</h4>
          <p>
            Genrefy does not store any personal data. We will never use your
            email for any purpose. You may disconnect from the app at any time
            by clicking the logout button at the top right of the player. We
            only require the following permissions, which you can find more
            information about on the Spotify website &nbsp;
            <a href="https://developer.spotify.com/documentation/web-api/concepts/scopes">
              here
            </a>
            :
          </p>
          <br></br>
          <ul className={styles.list}>
            <li>streaming - allows for streaming the content from spotify</li>
            <li>user-library-modify - allows for user to like new songs</li>
            <li>
              playlist-modify-public - allows for user to follow playlists
            </li>
            <li>
              playlist-modify-private - allows for user to follow playlists
            </li>
          </ul>
          <br></br>
          <p>
            The intention of this app is to help users discover new music they
            may not come across normally. A spotify premium account is required.
          </p>
        </div>
      </header>
    </div>
  );
};
