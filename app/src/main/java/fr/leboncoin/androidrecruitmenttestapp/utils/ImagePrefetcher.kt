package fr.leboncoin.androidrecruitmenttestapp.utils

import kotlinx.coroutines.CoroutineScope

interface ImagePrefetcher {
    fun prefetchImages(scope: CoroutineScope, urlList: List<String>)
}
