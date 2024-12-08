package com.contexts.cosmic.domain.model

import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonObject

@Serializable
sealed class Embed {
    @Serializable
    @SerialName("app.bsky.embed.images#view")
    data class Images(
        val images: List<Image>
    ) : Embed()

    @Serializable
    @SerialName("app.bsky.embed.external#view")
    data class External(
        val external: ExternalInfo
    ) : Embed()

    @Serializable
    @SerialName("app.bsky.embed.record#view")
    data class Record(
        val record: RecordView
    ) : Embed()

    @Serializable
    @SerialName("app.bsky.embed.recordWithMedia#view")
    data class RecordWithMedia(
        val record: RecordView,
        val media: Media
    ) : Embed()
}

@Serializable
data class Image(
    val alt: String,
    val fullsize: String,
    val thumb: String
)

@Serializable
data class ExternalInfo(
    val uri: String,
    val title: String,
    val description: String?,
    val thumb: String?
)

@Serializable
data class RecordView(
    val uri: String,
    val cid: String,
    val author: Author,
    val value: JsonObject, // TODO, not sure yet
    val labels: List<Label>? = null,
    val embeds: List<Embed>? = null,
    val indexedAt: String
)

@Serializable
sealed class Media {
    @Serializable
    @SerialName("app.bsky.embed.images#view")
    data class Images(
        val images: List<Image>
    ) : Media()

    @Serializable
    @SerialName("app.bsky.embed.external#view")
    data class External(
        val external: ExternalInfo
    ) : Media()
}

@Serializable
data class Label(
    val src: String,
    val uri: String,
    val val_: String,
    val neg: Boolean = false
)