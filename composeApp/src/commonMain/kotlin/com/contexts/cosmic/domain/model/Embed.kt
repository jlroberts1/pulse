package com.contexts.cosmic.domain.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator
import kotlinx.serialization.json.JsonObject

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("\$type")
sealed class Embed {
    @Serializable
    @SerialName("app.bsky.embed.images")
    data class Images(
        val images: List<ImageEmbed>,
    ) : Embed()

    @Serializable
    @SerialName("app.bsky.embed.external")
    data class External(
        val external: ExternalEmbed,
    ) : Embed()

    @Serializable
    @SerialName("app.bsky.embed.record")
    data class Record(
        val record: EmbeddedRecord,
    ) : Embed()
}

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("\$type")
sealed class EmbedView {
    @Serializable
    @SerialName("app.bsky.embed.images#view")
    data class Images(
        val images: List<ImageView>,
    ) : EmbedView()

    @Serializable
    @SerialName("app.bsky.embed.external#view")
    data class External(
        val external: ExternalView,
    ) : EmbedView()

    @Serializable
    @SerialName("app.bsky.embed.record#view")
    data class Record(
        val record: EmbeddedRecord,
    ) : EmbedView()
}

@Serializable
data class ImageEmbed(
    val alt: String,
    val image: BlobReference,
    val aspectRatio: AspectRatio,
)

@Serializable
data class AspectRatio(
    val width: Int,
    val height: Int,
)

@Serializable
data class ImageView(
    val thumb: String,
    val fullsize: String,
    val alt: String,
    val aspectRatio: AspectRatio? = null,
)

@Serializable
data class ExternalView(
    val uri: String,
    val title: String,
    val description: String?,
    val thumb: String? = null,
)

@Serializable
data class ExternalEmbed(
    val uri: String,
    val title: String,
    val description: String?,
    val thumb: BlobReference? = null,
)

@Serializable
data class BlobReference(
    @SerialName("\$type")
    val type: String,
    val ref: Link,
    val mimeType: String,
    val size: Int,
)

@Serializable
data class Link(
    @SerialName("\$link")
    val link: String,
)

@Serializable
data class EmbeddedRecord(
    val uri: String,
    val cid: String,
    val author: Author? = null,
    val value: JsonObject? = null,
    val embeds: List<EmbedView>? = null,
    val indexedAt: String? = null,
)

@Serializable
data class Label(
    val src: String,
    val uri: String,
    val val_: String,
    val neg: Boolean = false,
)
