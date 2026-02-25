package fr.leboncoin.androidrecruitmenttestapp.ui.model

import kotlinx.serialization.Serializable

@Serializable
data class AlbumUi(
    val id: Int,
    val chip1Description: String,
    val chip2Description: String,
    val title: String,
    val url: String,
    val thumbnailUrl: String,
    val isFavorite: Boolean = false,
)
