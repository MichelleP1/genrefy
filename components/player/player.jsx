import React, { useState, useEffect, useRef } from "react";
import { genres } from "../../lib/static/genres";
import { Inactive } from "../connectivity/inactive/inactive";
import { Button } from "../ui/button/button";
import { Browse } from "../browse/browse";
import { PlayerService } from "./player.service";
import {
  FaPlay,
  FaPause,
  FaStepBackward,
  FaFastBackward,
  FaStepForward,
  FaFastForward,
} from "react-icons/fa";
import {
  player_main,
  player_genre,
  player_playlist,
  player_album_image,
  player_track,
  player_controls,
  player_genre_playlist_controls,
  player_like_follow_controls,
} from "./player.module.scss";
import { SPOTIFY_SCRIPT } from "../../lib/static/constants";

const track = {
  name: "",
  album: {
    images: [{ url: "" }],
  },
  artists: [{ name: "" }],
};

export const Player = ({ token }) => {
  const [playerToken, setPlayerToken] = useState(token);
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
  const player = useRef(null);

  useEffect(() => {
    setSpotifyPlayer();

    return () => {
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
    const script = document.createElement("script");
    script.src = SPOTIFY_SCRIPT;
    script.async = true;
    document.body.appendChild(script);

    window.onSpotifyWebPlaybackSDKReady = async () => {
      player.current = new window.Spotify.Player({
        name: "Web Playback SDK",
        getOAuthToken: (cb) => {
          cb(playerToken);
        },
        volume: 0.5,
      });

      player.current.addListener("ready", ({ device_id }) => {
        deviceID.current = device_id;
        handleChangeGenre();
      });

      player.current.addListener("player_state_changed", (state) => {
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

      if (!connection) {
        setPlayerToken("");
        alert("Your session has expired - please sign in again");
      }
    };
  };

  const getNewGenrePlaylist = async (genre) => {
    const playlists = await PlayerService.getPlaylist(token, genre);
    setPlaylists(playlists);
    setGenre(genre);
    return playlists[0];
  };

  const getCurrentGenreNextPlaylist = () => {
    const index = playlists.findIndex((x) => x.name === playlist.name) + 1 || 0;
    return playlists?.[index];
  };

  const handleChangeGenre = async () => {
    const randomGenre = genres[Math.floor(Math.random() * genres.length)];
    handleChangePlaylist(null, randomGenre);
  };

  const handleChangePlaylist = async (_, newGenre = null) => {
    const newPlaylist = newGenre
      ? await getNewGenrePlaylist(newGenre)
      : getCurrentGenreNextPlaylist();
    setPlaylist(newPlaylist);
    const tracks = await PlayerService.getPlaylistTracks(
      token,
      newPlaylist.tracks.href
    );
    const uris = tracks.map((track) => `spotify:track:${track?.track?.id}`);
    PlayerService.updatePlayer(token, deviceID.current, uris);
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
    PlayerService.saveTrack(token, currentTrack.id);
  };

  const handleFollowPlaylist = () => {
    PlayerService.followPlaylist(token, playlist.id);
  };

  return active ? (
    loaded ? (
      <>
        <div className={player_main}>
          <Browse onChangeGenre={handleChangePlaylist}></Browse>
          <h3 className={player_genre}>{genre}</h3>
          <h5 className={player_playlist}>{playlist.name}</h5>
          <img
            className={player_album_image}
            src={currentTrack.album.images[0].url}
          />
          <h5 className={player_track}>
            {currentTrack.name} - {currentTrack.artists[0].name}
          </h5>
          <div>
            <div className={player_controls}>
              <Button title={<FaFastBackward />} onClick={handleRewind} />
              <Button title={<FaStepBackward />} onClick={handlePrevious} />
              <Button
                title={paused ? <FaPlay /> : <FaPause />}
                onClick={handlePlay}
              />
              <Button title={<FaStepForward />} onClick={handleNext} />
              <Button title={<FaFastForward />} onClick={handleFastForward} />
            </div>
            <div className={player_genre_playlist_controls}>
              <Button title="Change Genre" onClick={handleChangeGenre} />
              <Button title="Change Playlist" onClick={handleChangePlaylist} />
            </div>
            <div className={player_like_follow_controls}>
              <Button title="Like" onClick={handleSaveTrack} />
              <Button title="Follow" onClick={handleFollowPlaylist} />
            </div>
          </div>
        </div>
      </>
    ) : (
      <h1>loading</h1>
    )
  ) : (
    <Inactive />
  );
};

export default Player;
