package com.contexts.cosmic

interface Platform {
    val name: String
}

expect fun getPlatform(): Platform