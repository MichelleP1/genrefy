import axios from "axios";
import { genres } from "../../lib/static/genres";
import { URL_PREFIX } from "../../lib/static/constants";

const querySpotify = async (token, url) => {
  return await axios.get(url, {
    params: { limit: 50, offset: 0 },
    headers: {
      Authorization: "Bearer " + token,
      Accept: "application/json",
      "Content-Type": "application/json",
    },
  });
};

const updateSpotify = async (token, url, uris = {}) => {
  await axios.put(
    url,
    { uris },
    {
      headers: {
        Authorization: "Bearer " + token,
        Accept: "application/json",
        "Content-Type": "application/json",
      },
    }
  );
};

export const PlayerService = {
  getPlaylist: async (token, genre) => {
    const genrePlaylists = await querySpotify(
      token,
      `${URL_PREFIX}search?q=sound of ${genre}&type=playlist&limit=10`
    );
    return genrePlaylists?.data?.playlists?.items;
  },

  getPlaylistTracks: async (token, tracksURL) => {
    const tracks = await querySpotify(token, tracksURL);
    return tracks?.data?.items;
  },

  updatePlayer: async (token, deviceID, trackUrl) => {
    const tracks = await PlayerService.getPlaylistTracks(token, trackUrl);
    const uris = tracks.map((track) => `spotify:track:${track?.track?.id}`);

    updateSpotify(
      token,
      `${URL_PREFIX}me/player/play?device_id=${deviceID}`,
      uris
    );
  },

  saveTrack: async (token, trackID) => {
    updateSpotify(token, `${URL_PREFIX}me/tracks?ids=${trackID}`);
  },

  followPlaylist: async (token, playListID) => {
    updateSpotify(token, `${URL_PREFIX}playlists/${playListID}/followers`);
  },

  getRandomGenre: () => {
    return genres[Math.floor(Math.random() * genres.length)];
  },

  getCurrentGenreNextPlaylist: (playlists, playlist) => {
    const index = playlists.findIndex((x) => x.name === playlist.name) + 1 || 0;
    return playlists?.[index];
  },
};
