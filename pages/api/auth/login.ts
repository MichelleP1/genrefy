import type { NextApiResponse } from "next";
import generateRandomString from "../../../lib/util/generate_random_string";

export default (_, res: NextApiResponse) => {
  const auth_query_parameters = new URLSearchParams({
    response_type: "code",
    client_id: process.env.SPOTIFY_CLIENT_ID,
    scope:
      "streaming user-read-email user-read-private user-follow-modify user-library-modify",
    redirect_uri: `${process.env.APP_URL}/api/auth/callback`,
    state: generateRandomString(16),
  });

  res.redirect(
    "https://accounts.spotify.com/authorize/?" +
      auth_query_parameters.toString()
  );
};
