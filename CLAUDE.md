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
PlaybackService (MediaSessionService +  SongListScreen / NowPlayingScreen
   ExoPlayer + MediaSession)              (ui/screens, via ui/navigation/NavGraph)
```

- **`data/MediaRepository`** — queries `MediaStore.Audio.Media` once (`IS_MUSIC != 0`), caches
  the result in a `StateFlow<List<Song>>`. Library refresh is scan-on-demand only; there is no
  `ContentObserver`-based live refresh (known, deliberate gap).
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
- **`viewmodel/MusicViewModel`** — combines `MediaRepository.songs` + `PlaybackConnection.state`
  into one `MusicUiState`. Manual DI (no Hilt/Koin): `App` holds the single `MediaRepository`
  instance; `MusicViewModelFactory` wires it up.
- **`ui/permissions/AudioPermissionState`** — required-permission choice is SDK-gated:
  `READ_MEDIA_AUDIO` on API 33+, `READ_EXTERNAL_STORAGE` below that (declared in the manifest
  with `android:maxSdkVersion="32"`). `MainActivity` gates the whole `NavGraph` behind this
  permission state.

### Gradle version catalog

All dependency versions live in `gradle/libs.versions.toml` — do not hardcode version strings
directly in `app/build.gradle.kts`. `androidx.lifecycle` artifacts (`lifecycle-runtime-ktx`,
`lifecycle-viewmodel-compose`, `lifecycle-runtime-compose`) must stay pinned to the same version
reference; they've drifted before and Gradle won't align them automatically since they're
separate artifacts within the same group.
