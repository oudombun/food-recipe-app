package com.oudombun.chelfy.data.remote

import com.oudombun.chelfy.BuildConfig
import javax.inject.Inject
import javax.inject.Singleton

@Singleton
class ApiKeyProvider @Inject constructor() {
    private val keys = listOf(BuildConfig.API_KEY, BuildConfig.API_KEY2, BuildConfig.API_KEY3)
    private var index = 0

    @Synchronized
    fun next(): String {
        val key = keys[index]
        index = (index + 1) % keys.size
        return key
    }
}
