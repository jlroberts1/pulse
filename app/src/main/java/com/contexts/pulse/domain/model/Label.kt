/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.pulse.domain.model

import kotlinx.serialization.Serializable
import com.atproto.label.Label as AtProtoLabel

@Serializable
data class Label(
    val value: String,
)

fun AtProtoLabel.toLabel(): Label {
    return Label(
        value = `val`,
    )
}
