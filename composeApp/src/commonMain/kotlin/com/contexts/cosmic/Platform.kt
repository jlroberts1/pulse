package com.contexts.cosmic

interface Platform {
    val name: String

    fun isIOS(): Boolean
}

expect fun getPlatform(): Platform
