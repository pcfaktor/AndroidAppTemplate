package com.softteco.template.utils

import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.ExperimentalCoroutinesApi
import kotlinx.coroutines.test.UnconfinedTestDispatcher
import kotlinx.coroutines.test.resetMain
import kotlinx.coroutines.test.setMain
import org.spekframework.spek2.dsl.Root

@OptIn(ExperimentalCoroutinesApi::class)
fun Root.setTestDispatcher() {
    beforeGroup {
        Dispatchers.setMain(UnconfinedTestDispatcher())
    }
    afterGroup {
        Dispatchers.resetMain()
    }
}
