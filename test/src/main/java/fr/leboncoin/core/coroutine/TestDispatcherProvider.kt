package fr.leboncoin.core.coroutine

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher


object TestDispatcherProvider: DispatcherProvider {

    @OptIn(ExperimentalCoroutinesApi::class)
    private val testDispatcher = UnconfinedTestDispatcher()

    override val io: CoroutineDispatcher
        get() = testDispatcher
    override val main: CoroutineDispatcher
        get() = testDispatcher
}

