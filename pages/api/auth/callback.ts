import type { NextApiRequest, NextApiResponse } from "next";
import { serialize, CookieSerializeOptions } from "cookie";
import axios from "axios";

export const CallBack = (req: NextApiRequest, res: NextApiResponse) => {
  const params = {
    code: req.query.code.toString(),
    redirect_uri: `${process.env.APP_URL}/api/auth/callback`,
    grant_type: "authorization_code",
  };

  const authorization = `Basic ${Buffer.from(
    process.env.SPOTIFY_CLIENT_ID + ":" + process.env.SPOTIFY_CLIENT_SECRET
  ).toString("base64")}`;

  const content_type = "application/x-www-form-urlencoded";

  const options: CookieSerializeOptions = {
    httpOnly: true,
    secure: true,
    path: "/",
  };

  axios
    .post("https://accounts.spotify.com/api/token", params, {
      headers: {
        Authorization: authorization,
        "Content-Type": content_type,
      },
    })
    .then(({ data }) => {
      const token = data.access_token;
      res.setHeader("Set-Cookie", serialize("auth_token", token, options));

      res.status(200).redirect("/");
    })
    .catch((error) => {
      console.error(`Error: ${error}`);
    });
};
