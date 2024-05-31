import React, { useState, useEffect, useRef } from "react";
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
  FaSync,
  FaHeart,
} from "react-icons/fa";
import styles from "./player.module.scss";
import { SPOTIFY_SCRIPT } from "../../lib/static/constants";
import { FastAverageColor } from "fast-average-color";

const track = {
  name: "",
  albumImage: "",
  artist: "",
};

export const Player = ({ token }) => {
  const [playerToken, setPlayerToken] = useState(token);
  const [paused, setPaused] = useState(false);
  const [active, setActive] = useState(true);
  const [currentTrack, setCurrentTrack] = useState(track);
  const [genre, setGenre] = useState("");
  const [playlist, setPlaylist] = useState("");
  const [playlists, setPlaylists] = useState([]);
  const deviceID = useRef("");
  const position = useRef(0);
  const player = useRef(null);

  useEffect(() => {
    setSpotifyPlayer();

    return () => {
      player.current.disconnect();
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

        const track = PlayerService.setTrack(state.track_window.current_track);
        if (track.name !== currentTrack.name) position.current = 0;

        setCurrentTrack(track);
        setPaused(state.paused);

        player.current.getCurrentState().then((state) => {
          !state ? setActive(false) : setActive(true);
        });

        const fac = new FastAverageColor();

        fac
          .getColorAsync(track.albumImage)
          .then((color) => {
            const container = document.querySelector(".container");
            container.style.backgroundImage = `linear-gradient(${color.rgba}, #000)`;
          })
          .catch((e) => {
            console.log(e);
          });
      });

      const connection = await player.current.connect();
      if (!connection) {
        setPlayerToken("");
        alert("Your session has expired - please sign in again");
      }
    };
  };

  const handleChangeGenre = async (_, selectedGenre = null) => {
    const genre = selectedGenre || PlayerService.getRandomGenre();
    const playlists = await PlayerService.getGenrePlaylist(token, genre);
    const playlist = playlists[0];
    const tracksUrl = playlist.tracks.href;

    setPlaylists(playlists);
    setPlaylist(playlist);
    setGenre(genre);

    PlayerService.updatePlayer(token, deviceID.current, tracksUrl);
  };

  const handleChangePlaylist = async () => {
    const newPlaylist = PlayerService.getNextPlaylist(playlists, playlist);
    const tracksUrl = newPlaylist.tracks.href;

    setPlaylist(newPlaylist);

    PlayerService.updatePlayer(token, deviceID.current, tracksUrl);
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
    playlist ? (
      <>
        <div className={styles.player_main}>
          <Browse onChangeGenre={handleChangeGenre}></Browse>

          <h5 className={styles.player_genre}>{genre}</h5>
          <h5 className={styles.player_playlist}>{playlist.name}</h5>
          <img
            className={styles.player_album_image}
            src={currentTrack.albumImage}
          />
          <h5 className={styles.player_track}>
            {currentTrack.name} - {currentTrack.artist}
          </h5>
          <div className={styles.player_rewind}>
            <Button title={<FaFastBackward />} onClick={handleRewind} />
          </div>
          <div className={styles.player_previous}>
            <Button title={<FaStepBackward />} onClick={handlePrevious} />
          </div>
          <div className={styles.player_toggle_play}>
            <Button
              title={paused ? <FaPlay /> : <FaPause />}
              onClick={handlePlay}
            />
          </div>
          <div className={styles.player_next}>
            <Button title={<FaStepForward />} onClick={handleNext} />
          </div>
          <div className={styles.player_fastforward}>
            <Button title={<FaFastForward />} onClick={handleFastForward} />
          </div>
          <div className={styles.player_change_genre}>
            <Button title="Genre" onClick={handleChangeGenre} />
          </div>
          <div className={styles.player_change_playlist}>
            <Button title="Playlist" onClick={handleChangePlaylist} />
          </div>
          <div className={styles.player_follow}>
            <Button title="Follow" onClick={handleFollowPlaylist} />
          </div>
          <div className={styles.player_like}>
            <Button title="Like" onClick={handleSaveTrack} />
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
