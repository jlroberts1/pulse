package com.contexts.cosmic.domain.model

import kotlinx.serialization.ExperimentalSerializationApi
import kotlinx.serialization.SerialName
import kotlinx.serialization.Serializable
import kotlinx.serialization.json.JsonClassDiscriminator

@Serializable
data class Facet(
    @SerialName("\$type")
    val type: String? = null,
    val features: List<Feature>,
    val index: FacetIndex,
)

@Serializable
data class FacetIndex(
    val byteEnd: Int,
    val byteStart: Int,
)

@OptIn(ExperimentalSerializationApi::class)
@Serializable
@JsonClassDiscriminator("\$type")
sealed class Feature {
    @Serializable
    @SerialName("app.bsky.richtext.facet#link")
    data class Link(
        val uri: String,
    ) : Feature()

    @Serializable
    @SerialName("app.bsky.richtext.facet#mention")
    data class Mention(
        val did: String,
    ) : Feature()

    @Serializable
    @SerialName("app.bsky.richtext.facet#tag")
    data class Tag(
        val tag: String,
    ) : Feature()
}
