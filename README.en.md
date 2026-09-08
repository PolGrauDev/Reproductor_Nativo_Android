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
[![Release](https://img.shields.io/github/v/release/PolGrauDev/Reproductor_Nativo_Android?label=download)](https://github.com/PolGrauDev/Reproductor_Nativo_Android/releases/latest)

</div>

> ## 🚧 Project status: actively in development (WIP)
>
> This is a **personal project under construction**, built as a portfolio piece to
> showcase my Android development work. It's already usable day-to-day (library,
> playback, favorites, playlists, settings, visual styles...), but it's **not
> feature-complete yet**: there are no instrumented tests yet (unit tests already cover a
> good part of the logic), and some items in the roadmap below are still pending. If
> you're reviewing this as a code sample, keep that in mind — and if something stands out
> (good or bad), issues are welcome.

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
- 🔍 **Search and sorting**: filter the library by text (also reachable with one tap from
  the Now Playing screen) and sort by title, date added, or duration; the playback queue
  has its own independent search to find a track without losing the queue's real order.
- ❤️ **Favorites** and custom **playlists**, persisted with Room.
- 🎨 **Selectable visual styles**: three complete themes — Papel, Stickers and Fanzine —
  with their own typography, chosen from Settings and applied consistently across the
  whole app.
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

## 📲 Installation

The quickest way to try the app is to download the prebuilt APK from
**[GitHub Releases](https://github.com/PolGrauDev/Reproductor_Nativo_Android/releases/latest)**
(no Google Play, no build step required):

1. Download `Reproductor.Add.Free.apk` from the [latest release](https://github.com/PolGrauDev/Reproductor_Nativo_Android/releases/latest).
2. Android blocks installation by default (unknown source) — when you open the downloaded
   APK, follow the system prompt to allow "install unknown apps" for whichever browser or
   file manager you used to download it.
3. Install and open the app. Requires **Android 7.0 (API 24) or later**.

> The APK is signed with the project's release key — installing a newer version over an
> older one (updating) works without uninstalling, as long as it comes from this same key.

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

- [x] Release build signing (`signingConfigs`) + automatic publishing to GitHub Releases
  via GitHub Actions on pushing a `vX.Y.Z` tag
- [ ] Instrumented tests beyond the default template — unit tests already cover a good
  part of the data/logic layer (`MediaRepository`, `PlaylistRepository`,
  `SettingsRepository`, the extractable logic in `PlaybackConnection`/`PlaybackService`/
  `PlaybackScheduling`, and `MusicViewModel`'s derived properties); still pending:
  integration tests against a real `MediaController` and the Compose screens
- [ ] Screenshots and visual material for this README
- [ ] General UI/UX polish and possible new features

## 📄 License

This project is licensed under the [MIT License](LICENSE).

## 👤 Author

**PolGrauDev** — [github.com/PolGrauDev](https://github.com/PolGrauDev)
