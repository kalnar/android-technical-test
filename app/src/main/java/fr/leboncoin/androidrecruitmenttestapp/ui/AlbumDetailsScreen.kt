package fr.leboncoin.androidrecruitmenttestapp.ui

import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.verticalScroll
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.layout.ContentScale
import androidx.compose.ui.platform.LocalContext
import androidx.compose.ui.text.style.TextOverflow
import androidx.compose.ui.unit.dp
import coil3.compose.AsyncImage
import coil3.network.NetworkHeaders
import coil3.network.httpHeaders
import coil3.request.CachePolicy
import coil3.request.ImageRequest
import coil3.request.crossfade
import com.adevinta.spark.ExperimentalSparkApi
import com.adevinta.spark.SparkTheme
import com.adevinta.spark.components.chips.ChipTinted
import fr.leboncoin.androidrecruitmenttestapp.ui.model.AlbumUi

@OptIn(ExperimentalSparkApi::class)
@Composable
fun AlbumDetailsScreen(
    album: AlbumUi,
    modifier: Modifier = Modifier,
) {
    Column(
        modifier = modifier
            .fillMaxSize()
            .verticalScroll(rememberScrollState())
    ) {
        AsyncImage(
            model = ImageRequest.Builder(LocalContext.current)
                .data(album.url)
                .httpHeaders(
                    NetworkHeaders.Builder()
                        .add("User-Agent", "LeboncoinApp/1.0")
                        .build()
                )
                .memoryCachePolicy(CachePolicy.ENABLED)
                .diskCachePolicy(CachePolicy.ENABLED)
                .crossfade(true)
                .build(),
            contentDescription = album.title,
            modifier = Modifier
                .fillMaxWidth(),
            contentScale = ContentScale.Crop
        )

        Column(
            modifier = Modifier.padding(16.dp),
            verticalArrangement = Arrangement.spacedBy(12.dp)
        ) {
            Text(
                text = album.title,
                style = SparkTheme.typography.caption,
                overflow = TextOverflow.Ellipsis
            )

            Row(
                horizontalArrangement = Arrangement.spacedBy(8.dp)
            ) {
                ChipTinted(text = album.chip1Description)
                ChipTinted(text = album.chip2Description)
            }
        }
    }
}
