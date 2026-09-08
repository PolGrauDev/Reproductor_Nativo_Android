<div align="right">

🌐 <a href="README.en.md">Read in English</a>

</div>

<div align="center">

# 🎵 Reproductor Nativo Android

Un reproductor de música nativo para Android construido con **Kotlin + Jetpack Compose**,
que lee tu biblioteca directamente desde `MediaStore` y reproduce audio con **Media3
(ExoPlayer)** — con soporte completo de reproducción en segundo plano, notificación y
pantalla de bloqueo.

[![Kotlin](https://img.shields.io/badge/Kotlin-2.4.10-7F52FF?logo=kotlin&logoColor=white)](https://kotlinlang.org)
[![Jetpack Compose](https://img.shields.io/badge/Jetpack%20Compose-BOM%202026.02.01-4285F4?logo=jetpackcompose&logoColor=white)](https://developer.android.com/jetpack/compose)
[![Media3](https://img.shields.io/badge/Media3%20ExoPlayer-1.10.0-34A853)](https://developer.android.com/media/media3)
[![Min SDK](https://img.shields.io/badge/minSdk-24-brightgreen)](#requisitos-y-permisos)
[![License: MIT](https://img.shields.io/badge/License-MIT-yellow.svg)](LICENSE)
[![Status](https://img.shields.io/badge/status-WIP-orange)](#-estado-del-proyecto)
[![Release](https://img.shields.io/github/v/release/PolGrauDev/Reproductor_Nativo_Android?label=descarga)](https://github.com/PolGrauDev/Reproductor_Nativo_Android/releases/latest)

</div>

> ## 🚧 Estado del proyecto: en desarrollo activo (WIP)
>
> Este es un **proyecto personal en construcción**, pensado para mi portfolio como
> desarrollador Android. Ya es funcional en el día a día (biblioteca, reproducción,
> favoritos, listas, ajustes, estilos visuales...), pero **todavía le faltan piezas**
> antes de considerarlo "terminado": no hay tests instrumentados (los unitarios ya cubren
> buena parte de la lógica), y algunas ideas de la lista de más abajo siguen pendientes.
> Si lo estás mirando como muestra de código, ten esto en cuenta — y si algo te llama la
> atención (para bien o para mal), los issues son bienvenidos.

## 📱 Capturas de pantalla

<!--
TODO: añadir capturas reales de la app aquí, por ejemplo:
<p align="center">
  <img src=".github/screenshots/library.png" width="200" />
  <img src=".github/screenshots/now_playing.png" width="200" />
  <img src=".github/screenshots/playlists.png" width="200" />
</p>
-->

_Próximamente — capturas de pantalla en camino._

## ✨ Características

- 🎧 **Biblioteca completa**: explora tu música por canciones, álbumes, artistas y
  carpetas, leída directamente de `MediaStore` (sin servidor, sin base de datos propia
  de metadatos).
- 🔍 **Búsqueda y orden**: filtra la biblioteca por texto (también accesible con un toque
  desde la pantalla de reproducción) y ordena por título, fecha de añadido o duración; la
  cola de reproducción tiene su propio buscador independiente para localizar una canción
  sin perder el orden real de la cola.
- ❤️ **Favoritos** y **listas de reproducción** propias, persistidas con Room.
- 🎨 **Estilos visuales seleccionables**: tres temas completos — Papel, Stickers y
  Fanzine — con tipografías propias, elegibles desde Ajustes y aplicados de forma
  consistente en toda la app.
- 📜 **Cola de reproducción reordenable**, con **shuffle** y **repeat** (off/todo/uno),
  también controlables desde la notificación multimedia.
- 🔔 **Reproducción en segundo plano** real vía `MediaSessionService`, con controles en
  la notificación y en la pantalla de bloqueo — la reproducción sobrevive aunque se
  cierre la app o se apague la pantalla.
- 🖼️ **Portadas embebidas**: extrae la carátula desde los propios metadatos del archivo
  de audio, sin llamadas a servicios externos.
- ⏱️ **Temporizador de apagado** (sleep timer) configurable.
- 🎚️ **Fundido entre canciones** al terminar cada pista, para evitar cortes bruscos de
  volumen.
- 🔄 **Biblioteca siempre al día**: un `ContentObserver` detecta cambios en el
  almacenamiento (canciones añadidas/borradas) y refresca la lista automáticamente.
- 🛡️ **Manejo de errores de reproducción**: si un archivo está corrupto, salta
  automáticamente al siguiente en vez de romper la sesión de reproducción.

## 🛠️ Stack técnico

| Componente | Tecnología |
|---|---|
| Lenguaje | Kotlin |
| UI | Jetpack Compose (BOM 2026.02.01) |
| Reproducción | Media3 / ExoPlayer + `MediaSessionService` (1.10.0) |
| Persistencia relacional | Room 2.8.4 (favoritos, listas de reproducción) |
| Preferencias | DataStore Preferences 1.2.1 |
| Carga de imágenes | Coil3 3.5.0 (fetcher a medida para portadas embebidas) |
| Navegación | Navigation Compose 2.9.7 |
| Concurrencia | Kotlin Coroutines + `StateFlow` 1.11.0 |
| Build | AGP 9.1.1, KSP (no kapt) |

## 🏗️ Arquitectura

MVVM, con una particularidad importante: **el `Service` de reproducción es la fuente de
verdad del estado de reproducción, no el ViewModel** — así el estado sobrevive a la
destrucción de la Activity y a la pantalla apagada.

```
MediaRepository (escaneo de MediaStore) ─┐
                                          ├─▶ MusicViewModel ─▶ Pantallas Compose
PlaybackConnection (MediaController) ────┘        │
        │                                         │
        ▼                                         ▼
PlaybackService (MediaSessionService +      LibraryScreen / AlbumDetailScreen /
   ExoPlayer + MediaSession)                ArtistDetailScreen / NowPlayingScreen /
                                             QueueScreen / FavoritesScreen /
PlaylistRepository (Room) ──────────────────PlaylistDetailScreen / SettingsScreen
SettingsRepository (DataStore) ─────────────────────┘
```

`MusicViewModel` es un adaptador fino en forma de `StateFlow` sobre el Service — nunca es
el dueño del estado de reproducción.

## 📋 Requisitos y permisos

- **minSdk 24** · **targetSdk / compileSdk 36**

| Permiso | Motivo |
|---|---|
| `READ_MEDIA_AUDIO` (API 33+) / `READ_EXTERNAL_STORAGE` (≤32) | Leer la biblioteca de audio del dispositivo vía `MediaStore` |
| `FOREGROUND_SERVICE` / `FOREGROUND_SERVICE_MEDIA_PLAYBACK` | Mantener la reproducción activa en segundo plano |
| `POST_NOTIFICATIONS` (API 33+) | Mostrar la notificación de reproducción/controles |

## 📲 Instalación

La forma más rápida de probar la app es descargar el APK ya compilado desde
**[GitHub Releases](https://github.com/PolGrauDev/Reproductor_Nativo_Android/releases/latest)**
(no requiere Google Play ni compilar nada):

1. Descarga `Reproductor.Add.Free.apk` desde la [última release](https://github.com/PolGrauDev/Reproductor_Nativo_Android/releases/latest).
2. Android bloqueará la instalación por defecto (origen desconocido) — al abrir el APK
   descargado, sigue el aviso del sistema para permitir "instalar apps desconocidas" desde
   el navegador o gestor de archivos que hayas usado para descargarlo.
3. Instala y abre la app. Requiere **Android 7.0 (API 24) o superior**.

> El APK está firmado con la clave de release del proyecto — instalar una versión nueva
> sobre una anterior (actualizar) funciona sin desinstalar, siempre que provenga de esta
> misma clave.

## 🚀 Cómo compilar y ejecutar

```bash
# Compilar el APK de debug (Windows)
.\gradlew.bat :app:assembleDebug

# Instalar en un emulador/dispositivo conectado y lanzar
adb install -r app/build/outputs/apk/debug/app-debug.apk
adb shell am start -n com.PolGrauDev.reproductor_nativo_android/.MainActivity
```

También puedes abrir el proyecto directamente en **Android Studio** (Gradle sync
automático) y ejecutar desde ahí.

## 🗺️ Roadmap / Pendiente

- [x] Firma de build de release (`signingConfigs`) + publicación automática en GitHub
  Releases vía GitHub Actions al pushear un tag `vX.Y.Z`
- [ ] Tests instrumentados más allá de la plantilla por defecto — los unitarios ya cubren
  buena parte de la capa de datos y lógica pura (`MediaRepository`, `PlaylistRepository`,
  `SettingsRepository`, la lógica extraíble de `PlaybackConnection`/`PlaybackService`/
  `PlaybackScheduling`, y las propiedades derivadas de `MusicViewModel`); pendiente:
  tests de integración con un `MediaController` real y de las pantallas Compose
- [ ] Capturas de pantalla y material gráfico para este README
- [ ] Pulido general de UI/UX y posibles nuevas funcionalidades

## 📄 Licencia

Este proyecto está bajo la licencia [MIT](LICENSE).

## 👤 Autor

**PolGrauDev** — [github.com/PolGrauDev](https://github.com/PolGrauDev)
