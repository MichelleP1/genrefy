import type { NextApiRequest, NextApiResponse } from "next";
import { serialize } from "cookie";
import axios from "axios";

export default async (req: NextApiRequest, res: NextApiResponse) => {
  const params = {
    code: req.query.code.toString(),
    redirect_uri: "https://genrefy-o9la.vercel.app/api/auth/callback",
    grant_type: "authorization_code",
  };
  const authorization = `Basic ${Buffer.from(
    process.env.SPOTIFY_CLIENT_ID + ":" + process.env.SPOTIFY_CLIENT_SECRET
  ).toString("base64")}`;
  const content_type = "application/x-www-form-urlencoded";

  axios
    .post("https://accounts.spotify.com/api/token", params, {
      headers: {
        Authorization: authorization,
        "Content-Type": content_type,
      },
    })
    .then(({ data }) => {
      if (data.access_token) {
        const auth_token = JSON.stringify(data.access_token);
        res.setHeader("Set-Cookie", serialize("auth_token", auth_token));
        res.status(200).redirect("/");
      }
    })
    .catch((error) => {
      console.error(`Error: ${error}`);
    });
};
