import React, { useState, useEffect } from "react";
import Head from "next/head";
import Layout, { siteTitle } from "../components/layout";
import utilStyles from "../styles/utils.module.css";
import Login from "../components/login";
import Player from "../components/Player";

export default function Home({ token }) {
  // const [token, setToken] = useState("");

  useEffect(() => {
    // getToken();
  }, []);

  // const getToken = async () => {
  //   console.log("Get Token");
  //   const response = await fetch("/api/auth/callback");
  //   // const { access_token } = await response.json();
  //   console.log("response" + JSON.stringify(response));
  //   // setToken(access_token);
  // };

  return (
    <div className="container">
      {token ? (
        <Player
          token={token}
          // setToken={setToken}
        />
      ) : (
        <Login />
      )}
    </div>
  );
}

export const getServerSideProps = async (context) => {
  if (context.req.cookies["spotify-token"]) {
    console.log("one");
    const token = context.req.cookies["spotify-token"];
    return {
      props: { token: token },
    };
  } else {
    console.log("two");
    return {
      props: { token: "" },
    };
  }
};
