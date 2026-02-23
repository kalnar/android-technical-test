package fr.leboncoin.core.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.Dispatchers

object DefaultDispatcherProvider: DispatcherProvider {

    override val io: CoroutineDispatcher = Dispatchers.IO

    override val main: CoroutineDispatcher = Dispatchers.Main
}
