<div align="right">

🌐 <a href="README.md">Leer en Español</a>

</div>

<div align="center">

# 🎵 Native Android Music Player

A native Android music player built with **Kotlin + Jetpack Compose**, reading your
library straight from `MediaStore` and playing audio through **Media3 (ExoPlayer)** —
with full background playback, notification and lock-screen support.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.10-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202026.02.01-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Media3](https://img.shields.io/badge/Media3%20ExoPlayer-1.10.0-34A853)](https://developer.android.com/media/media3)
[![Min SDK](https://img.shields.io/badge/minSdk-24-brightgreen)](#requirements-and-permissions)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Status](https://img.shields.io/badge/status-WIP-orange)](#-project-status)

</div>

> ## 🚧 Project status: actively in development (WIP)
>
> This is a **personal project under construction**, built as a portfolio piece to
> showcase my Android development work. It's already usable day-to-day (library,
> playback, favorites, playlists, settings...), but it's **not feature-complete yet**:
> there's no signed release build, test coverage is minimal, and some items in the
> roadmap below are still pending. If you're reviewing this as a code sample, keep that
> in mind — and if something stands out (good or bad), issues are welcome.

## 📱 Screenshots

<!--
TODO: add real app screenshots here, e.g.:
<p align="center">
  <img src=".github/screenshots/library.png" width="200" />
  <img src=".github/screenshots/now_playing.png" width="200" />
  <img src=".github/screenshots/playlists.png" width="200" />
</p>
-->

_Coming soon — screenshots on the way._

## ✨ Features

- 🎧 **Full library browsing**: songs, albums, artists and folders, read directly from
  `MediaStore` (no server, no separate metadata database).
- 🔍 **Search and sorting**: filter the library by text and sort by title, date added, or
  duration.
- ❤️ **Favorites** and custom **playlists**, persisted with Room.
- 📜 **Reorderable playback queue**, with **shuffle** and **repeat** (off/all/one), also
  controllable from the media notification.
- 🔔 **True background playback** via `MediaSessionService`, with controls on the
  notification and lock screen — playback survives app closure and screen-off.
- 🖼️ **Embedded cover art**: extracted straight from the audio file's own metadata, no
  external service calls.
- ⏱️ **Configurable sleep timer**.
- 🎚️ **Fade between tracks** at the end of each song, avoiding abrupt volume cuts.
- 🔄 **Always up to date**: a `ContentObserver` detects storage changes (songs
  added/removed) and refreshes the library automatically.
- 🛡️ **Playback error handling**: a corrupted file is skipped automatically instead of
  breaking the playback session.

## 🛠️ Tech stack

| Component | Technology |
|---|---|
| Language | Kotlin |
| UI | Jetpack Compose (BOM 2026.02.01) |
| Playback | Media3 / ExoPlayer + `MediaSessionService` (1.10.0) |
| Relational persistence | Room 2.8.4 (favorites, playlists) |
| Preferences | DataStore Preferences 1.2.1 |
| Image loading | Coil3 3.5.0 (custom fetcher for embedded cover art) |
| Navigation | Navigation Compose 2.9.7 |
| Concurrency | Kotlin Coroutines + `StateFlow` 1.11.0 |
| Build | AGP 9.1.1, KSP (not kapt) |

## 🏗️ Architecture

MVVM, with one important twist: **the playback `Service` is the source of truth for
playback state, not the ViewModel** — so state survives Activity destruction and
screen-off.

```
MediaRepository (MediaStore scan)  ─┐
                                     ├─▶ MusicViewModel ─▶ Compose screens
PlaybackConnection (MediaController)┘        │
        │                                    │
        ▼                                    ▼
PlaybackService (MediaSessionService +  LibraryScreen / AlbumDetailScreen /
   ExoPlayer + MediaSession)              ArtistDetailScreen / NowPlayingScreen /
                                           QueueScreen / FavoritesScreen /
PlaylistRepository (Room) ────────────────PlaylistDetailScreen / SettingsScreen
SettingsRepository (DataStore) ──────────────────┘
```

`MusicViewModel` is a thin `StateFlow` adapter over the Service — it never owns playback
state itself.

## 📋 Requirements and permissions

- **minSdk 24** · **targetSdk / compileSdk 36**

| Permission | Reason |
|---|---|
| `READ_MEDIA_AUDIO` (API 33+) / `READ_EXTERNAL_STORAGE` (≤32) | Read the device's audio library via `MediaStore` |
| `FOREGROUND_SERVICE` / `FOREGROUND_SERVICE_MEDIA_PLAYBACK` | Keep playback running in the background |
| `POST_NOTIFICATIONS` (API 33+) | Show the playback notification/controls |

## 🚀 Build and run

```bash
# Build the debug APK (Windows)
.\gradlew.bat :app:assembleDebug

# Install on a connected emulator/device and launch
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.PolGrauDev.reproductor_nativo_android/.MainActivity
```

You can also open the project directly in **Android Studio** (auto Gradle sync) and run
it from there.

## 🗺️ Roadmap / TODO

- [ ] Release build signing (`signingConfigs`)
- [ ] Unit and instrumented tests beyond the default templates — already covered: pure
  playback logic (`PlaybackScheduling`) and the data repositories (`PlaylistRepository`,
  `SettingsRepository`); still pending: `MusicViewModel`, `MediaRepository`,
  `PlaybackConnection`/`PlaybackService`, and the Compose screens
- [ ] Screenshots and visual material for this README
- [ ] General UI/UX polish and possible new features

## 📄 License

This project is licensed under the [MIT License](LICENSE).

## 👤 Author

**PolGrauDev** — [github.com/PolGrauDev](https://github.com/PolGrauDev)
