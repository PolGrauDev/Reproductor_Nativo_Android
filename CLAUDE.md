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

Unit tests (single module; the stock JUnit template test (`ExampleUnitTest.kt`) is still there
alongside real coverage of the repositories/pure-logic layers — `data/MediaRepositoryTest.kt`,
`data/PlaylistRepositoryTest.kt`, `data/SettingsRepositoryTest.kt`,
`data/model/SongConstructionSanityTest.kt` (+ `SongTestFixtures.kt`),
`player/PlaybackConnectionLogicTest.kt`, `player/PlaybackSchedulingTest.kt`,
`player/PlaybackServiceLogicTest.kt`, `viewmodel/MusicUiStateTest.kt`):
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
                                           PlaylistDetailScreen / SettingsScreen
PlaylistRepository (Room: favorites +     (ui/screens, via ui/navigation/NavGraph)
   playlists) ─────────────────────────────────┘
SettingsRepository (DataStore: fade
   duration + sleep timer default) ────────────┘
```

- **App-style system (Papel / Stickers / Fanzine)** — predates this plan's Task 1-6 work, but is
  documented here for the first time. `ui/theme/style/AppStyle.kt` is a 3-value enum
  (`PAPEL`/`STICKERS`/`FANZINE`); the user's choice is persisted via `data/SettingsRepository`
  (same DataStore Preferences mechanism as `fadeDurationMs`/`sleepTimerDefaultMinutes`, stored
  under the `app_style` string key, falling back to `AppStyle.PAPEL` if the stored value is
  missing or fails `AppStyle.valueOf(...)`) and folded into `MusicUiState.appStyle` via the same
  `SettingsExtras` combine bucket described under `data/SettingsRepository` below. It's selected
  from `ui/screens/SettingsScreen` (`onSetAppStyle` wired to `MusicViewModel.setAppStyle`).
  Every screen file under `ui/screens/*.kt` and `ui/components/AddToPlaylistDialog.kt` is a thin
  dispatcher: it `when`-branches to a per-style implementation
  (`LibraryScreen` → `LibraryScreenPapel`/`LibraryScreenSticker`/`LibraryScreenFanzine` in
  `ui/screens/style/{papel,sticker,fanzine}/`; likewise `AddToPlaylistDialog` →
  `AddToPlaylistDialogPapel`/`...Sticker`/`...Fanzine` in
  `ui/components/style/{papel,sticker,fanzine}/`) — the dispatcher file itself has no styling
  logic, just prop forwarding. Most dispatchers read `uiState.appStyle` directly; the exception
  is `AddToPlaylistDialog`, which has no `MusicViewModel`/`uiState` of its own — it takes
  `appStyle: AppStyle` as a plain function parameter, with the caller passing `uiState.appStyle`
  in. Each style's design tokens (colors, type scale, custom
  `FontFamily`s, shapes) live in their own `ui/theme/style/<style>/*Tokens.kt` (e.g.
  `PapelTokens.kt` defines `PapelColors`/`PapelFonts`/`PapelType`). Custom fonts are loaded via
  `FontFamily(Font(R.font....))` from `.ttf` files in `res/font/` (Anton, Fredoka, Gaegu,
  Instrument Serif, Karla, Quicksand, Special Elite), with their licenses (mostly OFL) bundled as
  plain-text files under `assets/font_licenses/` rather than only referenced externally.
- **`data/MediaRepository`** — queries `MediaStore.Audio.Media` once (`IS_MUSIC != 0`) via
  `scanLibrary()`, caching the result in a `StateFlow<List<Song>>`. `Song` carries both `albumId`
  and `artistId` (not just the display strings) so grouping is done by stable MediaStore ID, not
  by name — avoids merging differently-tagged songs that happen to share a misspelled artist
  string. Stays live after that: a `ContentObserver` on `MediaStore.Audio.Media.EXTERNAL_CONTENT_URI`
  (registered once in `init`, never explicitly unregistered — the repository is an `App`-scoped
  singleton with no teardown hook) feeds a `MutableSharedFlow` debounced 800ms
  (`REFRESH_DEBOUNCE_MS`) before re-querying, so a single file operation (which MediaStore's
  scanner can report as several separate `onChange()` calls) triggers at most one re-scan. Each
  debounced refresh checks `ContextCompat.checkSelfPermission(...) == PERMISSION_GRANTED`
  (`ui/permissions/AudioPermissionState.kt`'s `audioPermission`) before querying — the observer can
  register before the user has granted the permission, and querying without it would otherwise
  wipe `_songs` to an empty list. `MusicViewModel.refreshLibrary()` (called from `MainActivity`'s
  `AudioLibraryGate` when `AudioPermissionStatus` transitions to `Granted`) closes a related gap:
  the ViewModel's own initial `scanLibrary()` call in `init` runs as soon as the Activity composes,
  which can race ahead of the user actually granting the permission on first install or after a
  revoke — without the extra rescan-on-grant, the library would stay empty until some unrelated
  MediaStore change happened to fire the observer. The `Cursor` → `Song` row mapping is its own
  `internal fun songFromCursor(...)` rather than inline logic inside `scanLibrary()`'s query loop,
  so `data/MediaRepositoryTest.kt` can build a `MatrixCursor` by hand (via Robolectric) and assert
  on the mapping directly — e.g. that a `0` `ALBUM_ID`/`ARTIST_ID` (MediaStore's "no value" sentinel
  for those columns) maps to `null`, not `0L`, without needing a real device/emulator.
- **`data/model/AlbumGroup` / `ArtistGroup` / `FolderGroup`** (`toAlbumGroups()` /
  `toArtistGroups()` / `toFolderGroups()` extensions on `List<Song>`) — pure in-memory `groupBy`
  derivations, no separate data source. Computed as part of `MusicUiState` (`albums`, `artists`,
  `folders`), sourced from `filteredSongs` so the library search box filters all four tabs
  (songs/albums/artists/folders) consistently. `FolderGroup` groups by `Song.filePath`'s parent
  directory (`substringBeforeLast('/')`) rather than `MediaStore.RELATIVE_PATH`, since that column
  only exists from API 29+ and `minSdk` is 24 — `Song.filePath` comes from the older but
  universally-available `MediaStore.Audio.Media.DATA` column, used here only to derive a display
  name, never to open the file directly. A folder's identity is its full path (a `String`, unlike
  the stable MediaStore `Long` IDs album/artist have), so `Routes.folderDetail()` passes it through
  `Uri.encode`/`Uri.decode` rather than inventing a numeric ID scheme.
- **`data/model/SortOrder`** — `MusicUiState.filteredSongs` applies the user-selected sort
  (title/date-added/duration, picked from a `DropdownMenu` in `LibraryScreen`'s `TopAppBar`) after
  the search filter. Deliberately scoped to the flat "Canciones" tab only — Álbumes/Artistas/Carpetas
  keep their own fixed alphabetical-by-group-name order (`sortedBy` inside their own
  `toXGroups()`), since "ordenar por duración" doesn't mean anything for a list of albums.
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
  this by default (`pauseAllPlayersAndStopSelf()` when no session is actively playing). The
  notification/lock-screen's shuffle and repeat buttons are built from `Player.COMMAND_SET_SHUFFLE_MODE`/
  `Player.COMMAND_SET_REPEAT_MODE` `CommandButton`s in `SLOT_OVERFLOW` (`buildMediaButtonPreferences()`),
  not a custom `SessionCommand` — Media3's own `CommandButton.executeAction(MediaController)` already
  knows how to toggle shuffle and, for repeat, set whatever mode is passed as the button's
  `parameter`, so no `MediaSession.Callback`/`onCustomCommand` override is needed. Because repeat
  doesn't auto-cycle, the repeat button's `parameter` is always precomputed as "the next mode in the
  cycle" (`nextRepeatMode()`, same OFF→ALL→ONE order as `PlaybackConnection.cycleRepeatMode()` but
  duplicated here since this runs directly against the service's `Player`, not through
  `PlaybackConnection`). `nextRepeatMode()` is `internal` (not `private`) purely so
  `player/PlaybackServiceLogicTest.kt` can call it directly against a bare
  `PlaybackService()` instance under Robolectric — no logic difference from a private function. A
  `Player.Listener` added directly to the service's `ExoPlayer` (separate
  from `PlaybackConnection`'s own client-side listener) rebuilds and republishes both buttons via
  `mediaSession.setMediaButtonPreferences(...)` on every shuffle/repeat change, whether it
  originated from the notification or from `NowPlayingScreen` in-app.
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
  `PlaybackUiState.queueMediaIds` reflects the *real* playback order, including the shuffled
  order when `shuffleModeEnabled` is on, not raw playlist/insertion order: `refreshQueue()` walks
  `Player.currentTimeline` via the `internal fun Timeline.playbackOrderIndices(shuffleModeEnabled)`
  extension (bottom of this file), which starts at `Timeline.getFirstWindowIndex(shuffleModeEnabled)`
  and repeatedly calls `Timeline.getNextWindowIndex(index, Player.REPEAT_MODE_OFF, shuffleModeEnabled)`
  until `C.INDEX_UNSET` — the same mechanism Media3 itself uses internally for next/previous
  navigation, always walked with `REPEAT_MODE_OFF` regardless of the user's real repeat mode so
  each window is visited exactly once. `onShuffleModeEnabledChanged` calls `refreshQueue()` too,
  so the published order updates the moment shuffle is toggled, not just on the next track
  transition. Because a track's position in that list no longer matches its raw index in the
  underlying `Player` playlist once shuffle is on, `playQueueItem`/`removeQueueItem` identify the
  track by `mediaId` (`Song.id.toString()`, resolved to the current raw index internally via a
  `MediaController.indexOfMediaId` helper) instead of by position — unlike `moveQueueItem`, which
  stays position-based because `QueueScreen*` only exposes manual reordering while shuffle is
  off, where displayed position and raw index coincide (see `ui/screens/style/*/QueueScreen*.kt`
  below). The sleep timer (`startSleepTimer`/`cancelSleepTimer`) follows
  the same job/`delay`/cancel idiom as the position-polling loop, using
  `SystemClock.elapsedRealtime()` (monotonic) rather than wall-clock time, and calls
  `controller.pause()` when it reaches zero. **"Fundido entre canciones" is deliberately not
  crossfade**: Media3 1.10.0 has no public API for overlapping audio between two tracks (that
  would need a dual-`ExoPlayer` mixing architecture); `setFadeDurationMs` instead ramps the
  single `MediaController`'s `volume` down near the end of a track and back up on
  `onMediaItemTransition` (see `rampVolume`/`rescheduleFadeOut`/`startFadeIn`), which removes the
  abrupt volume cut without any real overlap — hence the UI/code wording says "fundido", not
  "crossfade". Gapless playback itself needs no configuration: Media3 already handles it
  automatically at the decoder level. The pure arithmetic behind the sleep timer and the fade
  schedule — "how many ms remain" and "how many ms until the fade-out should start" — is pulled
  out into standalone top-level functions in `player/PlaybackScheduling.kt`
  (`sleepTimerRemainingMs`, `fadeOutDelayMs`), so `player/PlaybackSchedulingTest.kt` can test the
  math without a live `Player`/`MediaController`. `rampVolume` itself stays `private` and
  untested directly — unlike those two, it isn't pure arithmetic, it drives the live
  `MediaController`'s volume over time.
- **`player/PlaybackConnection` error handling** — `Player.Listener.onPlayerError` translates
  `PlaybackException.errorCode` into a short Spanish message (`toUserMessage`, an `internal`
  extension function on `PlaybackException` rather than `private` so
  `player/PlaybackConnectionLogicTest.kt` can call `exception.toUserMessage(...)` directly per
  error code — no logic change from being private) and publishes it as
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
  library. A second, separate search field — `queueSearchQuery` (own `MutableStateFlow<String>`,
  folded into the existing `libraryExtras` combine bucket alongside `favoriteSongIds`/`playlists`/
  `sortOrder` — not alongside `searchQuery`, which is a sibling top-level flow that only meets
  `libraryExtras`'s combined output at the outer 5-argument `combine()` that builds `uiState`) and
  the derived `MusicUiState.filteredQueue` (filters `queue` by title/artist, case-insensitive; returns `queue`
  unfiltered when blank) — is scoped to *only* the current playback queue, independent of the
  library-wide `searchQuery`/`filteredSongs`. `QueueScreen*`'s search field writes to it via
  `viewModel::setQueueSearchQuery`. `playQueueItem`/`removeFromQueue` identify the tapped row by
  `song.id.toString()` (the `Song`/`MediaItem` id), not by position, so they're correct
  regardless of whether the visible list is `queue` or the filtered `filteredQueue` — no index
  translation needed for those two actions, and the "Quitar de la cola" remove icon stays visible
  and functional during search for the same reason. `moveQueueItem`, by contrast, is genuinely
  position-based (`Player.moveMediaItem(from, to)`), so each `QueueScreen*` style implementation
  still maps a filtered-list row back to its real queue index via `queue.indexOf(song)` before
  calling it — see `QueueScreenPapel`'s `realIndex` — and the reorder up/down affordances are
  hidden (`canReorder = !isSearching && !shuffleModeEnabled`) both while a search is active
  (reordering a filtered subview against the real queue's indices would be confusing) and while
  shuffle is on (the displayed order is then the shuffled play order, not raw queue position —
  see `player/PlaybackConnection` above — so there's no raw index for a manual move to target).
- **`data/db/` (Room) + `data/PlaylistRepository`** — the persistence layer for relational data
  (favorites/playlists). `data/SettingsRepository` (DataStore Preferences, see below) persists
  simple scalar app settings. Together these are the only persistence layers in the app;
  everything else is either derived from `MediaStore` or lives only in the `Player`/in-memory.
  Two tables: `favorites` (just `songId` + timestamp) and `playlists` +
  `playlist_song_cross_ref` (has a `position` column for manual ordering, reassigned in full on
  every move rather than swapped — see `PlaylistRepository.moveSong`). Both tables store only
  `Song.id`; metadata is resolved against `MediaRepository.songs` in memory, same pattern as the
  playback queue. Uses KSP (not kapt) for the Room annotation processor.
- **`data/SettingsRepository`** — wraps DataStore Preferences (not Room: these are simple
  scalar values, not relational data) the same way `PlaylistRepository` wraps Room. Exposes
  `fadeDurationMs` (0 = disabled) and `sleepTimerDefaultMinutes` as `Flow`s + suspend setters.
  `MusicViewModel` pre-combines these into a `SettingsExtras` bucket alongside the existing
  `LibraryExtras` bucket (`combine()`'s built-in overload tops out at 5 flows, which is already
  spent by `songs`/`playback`/`isLoadingLibrary`/`searchQuery`/one extras bucket — a second
  settings-only flow has to be pre-merged the same way `libraryExtras` already is, not added as
  a 6th argument).
- **`viewmodel/MusicViewModel.playlistSongsFlow(playlistId)`** — the one piece of UI state that
  is *not* folded into `MusicUiState`, because it's parameterized per-playlist.
  `PlaylistDetailScreen` collects it directly via
  `remember(playlistId) { viewModel.playlistSongsFlow(playlistId) }`. Favorites, by contrast,
  *is* in `MusicUiState` (`favoriteSongIds`/`favoriteSongs`) since there's only ever one.
- **`ui/permissions/AudioPermissionState`** — required-permission choice is SDK-gated:
  `READ_MEDIA_AUDIO` on API 33+, `READ_EXTERNAL_STORAGE` below that (declared in the manifest
  with `android:maxSdkVersion="32"`). `MainActivity` gates the whole `NavGraph` behind this
  permission state.
- **`ui/screens/SettingsScreen` (`Routes.SETTINGS`)** — reached from an `IconButton` in
  `LibraryScreen`'s `TopAppBar`, next to the existing `SortMenu`. Hosts the sleep timer picker
  (reuses the `Box`/`IconButton`/`DropdownMenu` pattern from `LibraryScreen`'s `SortMenu`) and the
  fade-duration `Slider` described under `player/PlaybackConnection` above.
- **`NowPlayingScreen`'s search icon → Library** — all three style variants' top bar take an
  `onSearchClick` param; `ui/navigation/NavGraph.kt` wires it to
  `navController.navigate(Routes.SONG_LIST) { popUpTo(Routes.SONG_LIST) { inclusive = true } }`
  rather than a plain `navigate(Routes.SONG_LIST)` — collapsing the back stack down to (and
  including) the existing `SONG_LIST` entry instead of pushing a duplicate one, so pressing back
  from the Library after using this shortcut doesn't walk back through Now Playing again.
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
