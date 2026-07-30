package com.PolGrauDev.reproductor_nativo_android.data.model

data class FolderGroup(
    val path: String,
    val name: String,
    val songs: List<Song>,
)

fun List<Song>.toFolderGroups(): List<FolderGroup> = groupBy { it.filePath.substringBeforeLast('/', "") }
    .map { (path, songs) ->
        FolderGroup(
            path = path,
            name = path.substringAfterLast('/').ifBlank { "Carpeta desconocida" },
            songs = songs,
        )
    }
    .sortedBy { it.name }
