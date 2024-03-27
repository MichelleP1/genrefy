import React, { useState, useEffect, useRef } from "react";
import { genres } from "../public/genres";
import {
  FaPlay,
  FaPause,
  FaStepBackward,
  FaFastBackward,
  FaStepForward,
  FaFastForward,
} from "react-icons/fa";
// import { Inactive } from "./Inactive";
import { Button } from "./Button";
import axios from "axios";
// import { LoadingSpinner } from "./LoadingSpinner/LoadingSpinner";
// import "./player.scss";
// import Browse from "../Browse/browse";

const track = {
  name: "",
  album: {
    images: [{ url: "" }],
  },
  artists: [{ name: "" }],
};

function Player(props) {
  const { token } = props;
  console.log("token " + token);

  // const { token, setToken } = props;
  const [paused, setPaused] = useState(false);
  const [active, setActive] = useState(true);
  const [currentTrack, setCurrentTrack] = useState(track);
  const [genre, setGenre] = useState("");
  const [playlist, setPlaylist] = useState("");
  const [playlists, setPlaylists] = useState([]);
  const [loaded, setLoaded] = useState(false);
  const deviceID = useRef("");
  const trackName = useRef("");
  const position = useRef(0);
  const urlScript = "https://sdk.scdn.co/spotify-player.js";
  const urlPrefix = "https://api.spotify.com/v1/";
  const headers = {
    Authorization: "Bearer " + token,
    Accept: "application/json",
    "Content-Type": "application/json",
  };
  const player = useRef(null);

  useEffect(() => {
    setSpotifyPlayer();

    return () => {
      console.log("leaving");
      console.log(player.current);
      player.current.disconnect();
      setActive(false);
      setCurrentTrack(track);
      setGenre("");
      setPlaylist("");
      setPlaylists([]);
      setLoaded(false);
      trackName.current = "";
    };
  }, []);

  const setSpotifyPlayer = () => {
    console.log("setSpotifyPlayer token");
    console.log(token);
    const script = document.createElement("script");
    script.src = urlScript;
    script.async = true;
    document.body.appendChild(script);

    window.onSpotifyWebPlaybackSDKReady = async () => {
      player.current = new window.Spotify.Player({
        name: "Web Playback SDK",
        getOAuthToken: (cb) => {
          cb(token);
        },
        volume: 0.5,
      });
      // setPlayer(player);
      console.log(player.current);

      player.current.addListener("ready", ({ device_id }) => {
        console.log("ready");

        deviceID.current = device_id;
        handleChangeGenre();
      });

      player.current.addListener("not_ready", ({ device_id }) => {
        console.log("Device ID has gone offline");

        console.log("Device ID has gone offline", device_id);
      });

      player.current.addListener("player_state_changed", (state) => {
        console.log("player_state_changed");

        if (!state) return;

        const track = state.track_window.current_track;
        if (track.name !== trackName.current) position.current = 0;
        trackName.current = track.name;
        setCurrentTrack(track);
        setPaused(state.paused);
        setLoaded(true);

        player.current.getCurrentState().then((state) => {
          !state ? setActive(false) : setActive(true);
        });
      });

      const connection = await player.current.connect();
      // player.disconnect();

      if (!connection) {
        console.log(player.current);

        // setToken("");
        alert("Your session has expired - please sign in again");
      }
    };
  };

  const querySpotify = async (url) => {
    return await axios.get(url, {
      params: { limit: 50, offset: 0 },
      headers,
    });
  };

  const getNewGenrePlaylist = async (genre) => {
    console.log("getNewGenrePlaylist");
    const genrePlaylist = await querySpotify(
      `${urlPrefix}search?q=sound of ${genre}&type=playlist&limit=10`
    );
    console.log("genrePlaylist");
    console.log(genrePlaylist);
    const playlists = genrePlaylist?.data?.playlists?.items;
    setPlaylists(playlists);
    setGenre(genre);
    return playlists[0];
  };

  const getCurrentGenreNextPlaylist = () => {
    console.log("getCurrentGenreNextPlaylist");
    const index = playlists.findIndex((x) => x.name === playlist.name) + 1 || 0;
    console.log(playlists);
    return playlists?.[index];
  };

  const handleChangeGenre = async () => {
    console.log("genres");
    console.log(genres);
    const randomGenre = genres[Math.floor(Math.random() * genres.length)];
    console.log(randomGenre);
    handleChangePlaylist(null, randomGenre);
  };

  const handleChangePlaylist = async (_, newGenre = null) => {
    console.log("handleChangePlaylist newGenre");
    console.log(newGenre);
    const newPlaylist = newGenre
      ? await getNewGenrePlaylist(newGenre)
      : getCurrentGenreNextPlaylist();
    setPlaylist(newPlaylist);
    console.log("newPlaylist");
    console.log(newPlaylist);
    const playlistReturn = await querySpotify(newPlaylist.tracks.href);
    const tracks = playlistReturn.data.items;
    const uris = tracks.map((track) => `spotify:track:${track?.track?.id}`);
    const urlPlayer = `${urlPrefix}me/player/play?device_id=${deviceID.current}`;
    axios.put(urlPlayer, { uris }, { headers });
  };

  const handlePlay = () => {
    player.current.togglePlay();
  };

  const handleNext = () => {
    player.current.nextTrack();
  };

  const handlePrevious = () => {
    player.current.previousTrack();
  };

  const handleRewind = () => {
    const positionChange = position.current - 15 * 1000;
    position.current = positionChange >= 0 ? positionChange : 0;
    player.current.seek(position.current);
  };

  const handleFastForward = () => {
    position.current += 15 * 1000;
    player.current.seek(position.current);
  };

  const handleSaveTrack = () => {
    axios.put(`${urlPrefix}me/tracks?ids=${currentTrack.id}`, {}, { headers });
  };

  const handleFollowPlaylist = () => {
    axios.put(
      `${urlPrefix}playlists/${playlist.id}/followers`,
      {},
      { headers }
    );
  };

  return active ? (
    loaded ? (
      <>
        <div className="main">
          {/* <Browse onChangeGenre={handleChangePlaylist}></Browse> */}
          <h3 className="genre">{genre}</h3>
          <h5 className="playlist">{playlist.name}</h5>
          <img className="album-image" src={currentTrack.album.images[0].url} />
          <h5 className="track">
            {currentTrack.name} - {currentTrack.artists[0].name}
          </h5>
          <div>
            <div className="player-controls">
              <Button title={<FaFastBackward />} onClick={handleRewind} />
              <Button title={<FaStepBackward />} onClick={handlePrevious} />
              <Button
                title={paused ? <FaPlay /> : <FaPause />}
                onClick={handlePlay}
              />
              <Button title={<FaStepForward />} onClick={handleNext} />
              <Button title={<FaFastForward />} onClick={handleFastForward} />
            </div>
            <div className="genre-playlist-controls">
              <Button title="Change Genre" onClick={handleChangeGenre} />
              <Button title="Change Playlist" onClick={handleChangePlaylist} />
            </div>
            <div className="like-follow-controls">
              <Button title="Like" onClick={handleSaveTrack} />
              <Button title="Follow" onClick={handleFollowPlaylist} />
            </div>
          </div>
        </div>
      </>
    ) : (
      <h1>loading</h1>
      // <LoadingSpinner />
    )
  ) : (
    <h1>inactive</h1>
    // <Inactive />
  );
}

export default Player;
