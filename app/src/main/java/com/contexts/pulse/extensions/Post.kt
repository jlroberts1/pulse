/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.extensions

import androidx.compose.ui.text.intl.Locale
import app.bsky.notification.ListNotificationsNotification
import app.bsky.richtext.Facet
import com.atproto.label.Label
import kotlinx.serialization.Serializable
import logcat.logcat
import sh.christian.ozone.api.model.Timestamp

@Serializable
data class RecordText(
    val text: String,
    val facets: List<Facet>? = null,
    val langs: List<String>? = null,
    val labels: List<Label>? = null,
    val createdAt: Timestamp,
)

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

fun ListNotificationsNotification.getRecordText(): String {
    return try {
        record.decodeAs<RecordText>().text
    } catch (e: Exception) {
        logcat { "Unable to parse record text, ${e.message}" }
        ""
    }
}

fun Long.toKFormat(): String =
    when {
        this < 1000 -> this.toString()
        this < 1_000_000 ->
            String.format(
                locale = Locale.current.platformLocale,
                format = "%.1fk",
                this / 1000.0,
            )
                .replace(".0k", "k")
        else -> this.toString()
    }
