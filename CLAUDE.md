# CLAUDE.md

This file provides guidance to Claude Code (claude.ai/code) when working with code in this repository.

## Project

Native Android music player (Kotlin + Jetpack Compose). Reads the device's audio library from
`MediaStore`, extracts embedded metadata/cover art via `MediaMetadataRetriever`, and plays audio
through Media3 (ExoPlayer + `MediaSessionService`) so playback survives with the screen off and
exposes lock-screen/notification controls. Single Gradle module (`:app`), package
`com.PolGrauDev.reproductor_nativo_android`, `minSdk 24` / `compileSdk`+`targetSdk 36`.

## Commands

Build (Windows):
```
.\gradlew.bat :app:assembleDebug
```
Bash (Git Bash/MSYS on Windows — path auto-conversion breaks `adb`/emulator args with a leading
`/`; prefix those commands with `MSYS_NO_PATHCONV=1` or they get silently rewritten as Windows
paths):
```
./gradlew :app:assembleDebug
```

Install to a running emulator/device and launch:
```
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.PolGrauDev.reproductor_nativo_android/.MainActivity
```

Unit tests (single module, only the stock JUnit template test exists today —
`app/src/test/.../ExampleUnitTest.kt`):
```
./gradlew :app:testDebugUnitTest
./gradlew :app:testDebugUnitTest --tests "com.PolGrauDev.reproductor_nativo_android.ExampleUnitTest"
```

Instrumented tests (need a connected device/emulator; only the stock Espresso template test
exists today — `app/src/androidTest/.../ExampleInstrumentedTest.kt`):
```
./gradlew :app:connectedDebugAndroidTest
```

Dependency resolution sanity check (useful after touching `gradle/libs.versions.toml`):
```
./gradlew :app:dependencies --configuration debugRuntimeClasspath
```

There is no lint/ktlint/detekt config in the project; `./gradlew :app:lint` runs the stock
Android Lint checks only.

## Architecture

MVVM with the playback **Service** as the source of truth for player state, not the ViewModel —
state must survive Activity destruction and screen-off, so the ViewModel is a thin StateFlow
adapter over the Service, never the owner of playback state.

```
MediaRepository (MediaStore scan)  ─┐
                                     ├─▶ MusicViewModel ─▶ Compose screens
PlaybackConnection (MediaController)┘        │
        │                                    │
        ▼                                    ▼
PlaybackService (MediaSessionService +  LibraryScreen / AlbumDetailScreen / ArtistDetailScreen /
   ExoPlayer + MediaSession)              NowPlayingScreen / QueueScreen / FavoritesScreen /
                                           PlaylistDetailScreen
PlaylistRepository (Room: favorites +     (ui/screens, via ui/navigation/NavGraph)
   playlists) ─────────────────────────────────┘
```

- **`data/MediaRepository`** — queries `MediaStore.Audio.Media` once (`IS_MUSIC != 0`), caches
  the result in a `StateFlow<List<Song>>`. Library refresh is scan-on-demand only; there is no
  `ContentObserver`-based live refresh (known, deliberate gap). `Song` carries both `albumId` and
  `artistId` (not just the display strings) so grouping is done by stable MediaStore ID, not by
  name — avoids merging differently-tagged songs that happen to share a misspelled artist string.
- **`data/model/AlbumGroup` / `ArtistGroup`** (`toAlbumGroups()` / `toArtistGroups()` extensions
  on `List<Song>`) — pure in-memory `groupBy` derivations, no separate data source. Computed as
  part of `MusicUiState` (`albums`, `artists`), sourced from `filteredSongs` so the library
  search box filters all three tabs (songs/albums/artists) consistently.
- **`data/AlbumArtExtractor`** — the *only* place that calls
  `MediaMetadataRetriever.getEmbeddedPicture()`. Shared by both the Coil fetcher and
  `PlaybackConnection` so a song's embedded art is never extracted twice.
- **`data/AlbumArtFetcher` + `AlbumArtRequest`** — a custom Coil3 `Fetcher.Factory` for lazy,
  per-song (not eager whole-library) embedded-art loading, cached by Coil.
  **Important Coil3 gotcha**: Coil3 remaps `android.net.Uri` to its own internal type before
  fetcher dispatch, so a `Fetcher.Factory<android.net.Uri>` is never invoked — this is why art
  requests go through the custom `AlbumArtRequest` data class instead of a raw `Uri`. The
  `ImageLoader` is registered explicitly via `SingletonImageLoader.setSafe { }` in
  `App.onCreate()`; relying only on `App` implementing `SingletonImageLoader.Factory` without
  that explicit call does not get picked up.
- **`player/PlaybackService`** — `MediaSessionService` owning the `ExoPlayer` + `MediaSession`.
  Do not add a manual `onTaskRemoved()` override to pause/stop playback — Media3 already does
  this by default (`pauseAllPlayersAndStopSelf()` when no session is actively playing).
- **`player/PlaybackConnection`** — the only bridge between UI-layer code and the Service; binds
  via `MediaController` + `SessionToken`, never a raw Service reference. Translates
  `Player.Listener` callbacks into `PlaybackUiState` (`StateFlow`), and runs a manual polling
  loop (~500ms) while playing since `Player` has no continuous position-update callback. Also
  lazily enriches the *currently playing* `MediaItem`'s metadata with embedded art (via
  `AlbumArtExtractor`) after a track transition, rather than pre-loading art for the whole queue.
  Shuffle/repeat/queue reordering (`toggleShuffle`, `cycleRepeatMode`, `moveQueueItem`,
  `removeQueueItem`, `playQueueItem`) are thin wrappers over `Player`'s own
  `shuffleModeEnabled`/`repeatMode`/`moveMediaItem`/`removeMediaItem` — ExoPlayer already
  implements queue manipulation natively, this layer only exposes it as `StateFlow` state.
  Known v1 simplification: the queue list (`queueMediaIds`) reflects playlist/index order, not
  the shuffled play order — Media3 applies shuffle to next/previous navigation, not to what
  `getMediaItemAt(index)` returns.
- **`player/PlaybackConnection` error handling** — `Player.Listener.onPlayerError` translates
  `PlaybackException.errorCode` into a short Spanish message (`toUserMessage`) and publishes it as
  `PlaybackUiState.errorMessage`. Recovery is automatic, not just a notice: if there's a next item
  (`hasNextMediaItem()`), it auto-skips (`seekToNextMediaItem` + `prepare` + `play`) so one corrupt
  file doesn't kill the whole session. A private `consecutiveErrorCount` (reset on
  `onIsPlayingChanged(true)`) caps this at `MAX_CONSECUTIVE_ERRORS = 3`; past that it stops
  auto-skipping and shows a distinct "several tracks failed" message instead of looping through a
  fully-broken queue. `MusicViewModel.clearPlaybackError()` / `PlaybackConnection.clearError()`
  reset `errorMessage` back to `null` once shown.
- **`viewmodel/MusicViewModel`** — combines `MediaRepository.songs` + `PlaybackConnection.state`
  + a `searchQuery` `StateFlow<String>` into one `MusicUiState` (also derives `currentSong` and
  `queue: List<Song>` by mapping `PlaybackUiState.queueMediaIds` back to `Song` via the
  repository's song list). Manual DI (no Hilt/Koin): `App` holds the single `MediaRepository`
  instance; `MusicViewModelFactory` wires it up. `playSong(song, fromList = uiState.value.songs)`
  takes an optional queue scope — `AlbumDetailScreen`/`ArtistDetailScreen` pass the group's own
  song list so playing from a group's detail screen queues just that group, not the whole
  library.
- **`data/db/` (Room) + `data/PlaylistRepository`** — the only persistence layer in the app;
  everything else is either derived from `MediaStore` or lives only in the `Player`/in-memory.
  Two tables: `favorites` (just `songId` + timestamp) and `playlists` +
  `playlist_song_cross_ref` (has a `position` column for manual ordering, reassigned in full on
  every move rather than swapped — see `PlaylistRepository.moveSong`). Both tables store only
  `Song.id`; metadata is resolved against `MediaRepository.songs` in memory, same pattern as the
  playback queue. Uses KSP (not kapt) for the Room annotation processor.
- **`viewmodel/MusicViewModel.playlistSongsFlow(playlistId)`** — the one piece of UI state that
  is *not* folded into `MusicUiState`, because it's parameterized per-playlist.
  `PlaylistDetailScreen` collects it directly via
  `remember(playlistId) { viewModel.playlistSongsFlow(playlistId) }`. Favorites, by contrast,
  *is* in `MusicUiState` (`favoriteSongIds`/`favoriteSongs`) since there's only ever one.
- **`ui/permissions/AudioPermissionState`** — required-permission choice is SDK-gated:
  `READ_MEDIA_AUDIO` on API 33+, `READ_EXTERNAL_STORAGE` below that (declared in the manifest
  with `android:maxSdkVersion="32"`). `MainActivity` gates the whole `NavGraph` behind this
  permission state.
- **`MainActivity` global `SnackbarHost`** — a single `SnackbarHostState` lives in the `Scaffold`
  wrapping `NavGraph`, not per-screen: playback errors are Service-level events that can happen on
  any screen (e.g. an auto-skip while browsing the library), so there's one `LaunchedEffect`
  keyed on `uiState.playback.errorMessage` instead of duplicating that logic in every screen.

### Gradle version catalog

All dependency versions live in `gradle/libs.versions.toml` — do not hardcode version strings
directly in `app/build.gradle.kts`. `androidx.lifecycle` artifacts (`lifecycle-runtime-ktx`,
`lifecycle-viewmodel-compose`, `lifecycle-runtime-compose`) must stay pinned to the same version
reference; they've drifted before and Gradle won't align them automatically since they're
separate artifacts within the same group.
