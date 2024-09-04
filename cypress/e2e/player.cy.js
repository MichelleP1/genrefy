// cypress/e2e/player.cy.ts

describe("Player Component", () => {
  beforeEach(() => {
    cy.visit("http://localhost:3000");
  });

  it("renders loading state initially", () => {
    cy.get("h1").contains("loading").should("be.visible");
  });

  it("initializes player correctly", () => {
    cy.window().should("have.property", "onSpotifyWebPlaybackSDKReady");
  });

  it("handles play/pause toggle", () => {
    cy.window().then((win) => {
      win.onSpotifyWebPlaybackSDKReady();
    });

    cy.get(".player_toggle_play button").click();
    cy.window().its("PlayerService.togglePlay").should("have.been.called");
  });

  it("handles next track", () => {
    cy.window().then((win) => {
      win.onSpotifyWebPlaybackSDKReady();
    });

    cy.get(".player_next button").click();
    cy.window().its("PlayerService.nextTrack").should("have.been.called");
  });

  it("handles previous track", () => {
    cy.window().then((win) => {
      win.onSpotifyWebPlaybackSDKReady();
    });

    cy.get(".player_previous button").click();
    cy.window().its("PlayerService.previousTrack").should("have.been.called");
  });

  it("handles rewind", () => {
    cy.window().then((win) => {
      win.onSpotifyWebPlaybackSDKReady();
    });

    cy.get(".player_rewind button").click();
    cy.window().its("PlayerService.seek").should("have.been.calledWith", 0);
  });

  it("handles fast forward", () => {
    cy.window().then((win) => {
      win.onSpotifyWebPlaybackSDKReady();
    });

    cy.get(".player_fastforward button").click();
    cy.window().its("PlayerService.seek").should("have.been.calledWith", 15000);
  });

  it("handles save track", () => {
    cy.window().then((win) => {
      win.onSpotifyWebPlaybackSDKReady();
    });

    cy.get(".player_like button").click();
    cy.window()
      .its("PlayerService.saveTrack")
      .should("have.been.calledWith", "test-token", "track-id");
  });

  it("handles follow playlist", () => {
    cy.window().then((win) => {
      win.onSpotifyWebPlaybackSDKReady();
    });

    cy.get(".player_follow button").click();
    cy.window()
      .its("PlayerService.followPlaylist")
      .should("have.been.calledWith", "test-token", "playlist-id");
  });

  it("changes genre and updates playlist", () => {
    cy.window().then((win) => {
      win.onSpotifyWebPlaybackSDKReady();
    });

    cy.get(".player_change_genre button").click();
    cy.window()
      .its("PlayerService.getGenrePlaylist")
      .should("have.been.calledWith", "test-token", "genre");
  });

  it("changes playlist", () => {
    cy.window().then((win) => {
      win.onSpotifyWebPlaybackSDKReady();
    });

    cy.get(".player_change_playlist button").click();
    cy.window()
      .its("PlayerService.getNextPlaylist")
      .should("have.been.calledWith", "playlists", "playlist");
  });
});
