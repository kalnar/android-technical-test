package fr.leboncoin.androidrecruitmenttestapp.ui.mapper

import fr.leboncoin.androidrecruitmenttestapp.ui.model.AlbumUi
import fr.leboncoin.domain.model.Album
import javax.inject.Inject

class AlbumUiMapper @Inject constructor() {

    fun toUi(album: Album): AlbumUi = AlbumUi(
        id = album.id,
        chip1Description = "Album #${album.albumId}",
        chip2Description = "Track #${album.id}",
        title = album.title,
        url = album.url,
        thumbnailUrl = album.thumbnailUrl
    )
}
