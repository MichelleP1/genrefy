import React from "react";
import Link from "next/link";

export const Login = () => {
  return (
    <div className="App">
      <header className="App-header">
        <Link href="/api/auth/login" className="btn-spotify">
          Login with Spotify
        </Link>
      </header>
    </div>
  );
};
