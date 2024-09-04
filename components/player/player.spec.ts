// __tests__/player.service.test.js
import axios from "axios";
import { FastAverageColor } from "fast-average-color";
import { PlayerService } from "./player.service";
import { genres } from "../../lib/static/genres";
import { URL_PREFIX } from "../../lib/static/constants";

jest.mock("axios");
jest.mock("fast-average-color");

describe("PlayerService", () => {
  const token = "test_token";
  const deviceID = "test_device_id";
  const trackUrl = "test_track_url";
  const trackID = "test_track_id";
  const playListID = "test_playlist_id";
  const albumImage = "test_album_image_url";

  beforeEach(() => {
    jest.clearAllMocks();
  });

  test("getGenrePlaylist should fetch genre playlists", async () => {
    const mockResponse = {
      data: { playlists: { items: ["playlist1", "playlist2"] } },
    };
    axios.get.mockResolvedValue(mockResponse);

    const result = await PlayerService.getGenrePlaylist(token, "rock");
    expect(result).toEqual(["playlist1", "playlist2"]);
    expect(axios.get).toHaveBeenCalledWith(
      `${URL_PREFIX}search?q=sound of rock&type=playlist&limit=10`,
      expect.any(Object)
    );
  });

  test("getPlaylistTracks should fetch playlist tracks", async () => {
    const mockResponse = { data: { items: ["track1", "track2"] } };
    axios.get.mockResolvedValue(mockResponse);

    const result = await PlayerService.getPlaylistTracks(token, trackUrl);
    expect(result).toEqual(["track1", "track2"]);
    expect(axios.get).toHaveBeenCalledWith(trackUrl, expect.any(Object));
  });

  test("updatePlayer should update player with tracks", async () => {
    const mockTracks = {
      data: {
        items: [{ track: { id: "track1" } }, { track: { id: "track2" } }],
      },
    };
    axios.get.mockResolvedValue(mockTracks);

    await PlayerService.updatePlayer(token, deviceID, trackUrl);
    expect(axios.put).toHaveBeenCalledWith(
      `${URL_PREFIX}me/player/play?device_id=${deviceID}`,
      { uris: ["spotify:track:track1", "spotify:track:track2"] },
      expect.any(Object)
    );
  });

  test("saveTrack should save a track", async () => {
    await PlayerService.saveTrack(token, trackID);
    expect(axios.put).toHaveBeenCalledWith(
      `${URL_PREFIX}me/tracks?ids=${trackID}`,
      { uris: null },
      expect.any(Object)
    );
  });

  test("followPlaylist should follow a playlist", async () => {
    await PlayerService.followPlaylist(token, playListID);
    expect(axios.put).toHaveBeenCalledWith(
      `${URL_PREFIX}playlists/${playListID}/followers`,
      { uris: null },
      expect.any(Object)
    );
  });

  test("getRandomGenre should return a random genre", () => {
    const genre = PlayerService.getRandomGenre();
    expect(genres).toContain(genre);
  });

  test("getNextPlaylist should return the next playlist", () => {
    const playlists = [{ name: "playlist1" }, { name: "playlist2" }];
    const currentPlaylist = { name: "playlist1" };

    const nextPlaylist = PlayerService.getNextPlaylist(
      playlists,
      currentPlaylist
    );
    expect(nextPlaylist).toEqual(playlists[1]);
  });

  test("setTrack should return track details", () => {
    const track = {
      id: "track1",
      name: "Track 1",
      album: { images: [{ url: "image_url" }] },
      artists: [{ name: "Artist 1" }],
    };

    const result = PlayerService.setTrack(track);
    expect(result).toEqual({
      id: "track1",
      name: "Track 1",
      albumImage: "image_url",
      artist: "Artist 1",
    });
  });

  test("setAverageBackgroundColor should set background color", async () => {
    const mockColor = { rgba: "rgba(255, 0, 0, 1)" };
    FastAverageColor.prototype.getColorAsync.mockResolvedValue(mockColor);

    document.body.innerHTML = '<div class="container"></div>';
    await PlayerService.setAverageBackgroundColor(albumImage);

    const container = document.querySelector(".container");
    expect(container.style.backgroundImage).toBe(
      `linear-gradient(${mockColor.rgba}, #000)`
    );
  });
});
