import React, { useState } from "react";
import { Login } from "../components/login/login";
import { Player } from "../components/player/player";

export default function Home({ auth_token }) {
  const [token, setToken] = useState(auth_token);

  return (
    <div className="container">
      {token ? <Player token={token} setToken={setToken} /> : <Login />}
    </div>
  );
}

export const getServerSideProps = async ({ req }) => {
  const auth_token = req.cookies["auth_token"] || "";
  return { props: { auth_token } };
};
