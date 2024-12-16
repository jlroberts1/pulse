/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.extensions

import app.bsky.embed.RecordViewRecordUnion
import app.bsky.feed.PostView
import app.bsky.notification.ListNotificationsNotification
import app.bsky.richtext.Facet
import com.atproto.label.Label
import com.contexts.cosmic.domain.model.PostRecord
import kotlinx.serialization.Serializable
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

fun PostView.getPostText(): String {
    return try {
        record.decodeAs<PostRecord>().text
    } catch (e: Exception) {
        ""
    }
}

fun RecordViewRecordUnion.ViewRecord.getRecordText(): String {
    return try {
        value.value.decodeAs<RecordText>().text
    } catch (e: Exception) {
        ""
    }
}

fun ListNotificationsNotification.getRecordText(): String {
    return try {
        record.decodeAs<RecordText>().text
    } catch (e: Exception) {
        ""
    }
}
