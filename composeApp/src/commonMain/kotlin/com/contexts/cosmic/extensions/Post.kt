/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

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
