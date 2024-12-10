package com.contexts.cosmic.extensions

fun String.isGifEmbed(): Boolean {
    val gifHosts =
        setOf(
            "tenor.com",
            "media.tenor.com",
            "giphy.com",
            "media.giphy.com",
        )

    val uriLower = this.lowercase()
    return (gifHosts.any { uriLower.contains(it) } && uriLower.contains("gif"))
}
