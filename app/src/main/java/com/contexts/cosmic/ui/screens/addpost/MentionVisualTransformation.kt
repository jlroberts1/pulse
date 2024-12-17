/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.screens.addpost

import androidx.compose.ui.graphics.Color
import androidx.compose.ui.text.AnnotatedString
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.input.OffsetMapping
import androidx.compose.ui.text.input.TransformedText
import androidx.compose.ui.text.input.VisualTransformation

class MentionVisualTransformation : VisualTransformation {
    override fun filter(text: AnnotatedString): TransformedText {
        if (!text.contains("@")) return TransformedText(text, OffsetMapping.Identity)

        val mentionRegex = "@[\\w.-]+".toRegex()
        val builder = AnnotatedString.Builder()
        var lastIndex = 0

        mentionRegex.findAll(text).forEach { result ->
            builder.append(text.substring(lastIndex, result.range.first))

            builder.pushStyle(
                SpanStyle(
                    color = Color(0xFF2196F3),
                    fontWeight = FontWeight.Medium,
                ),
            )
            builder.append(text.substring(result.range.first, result.range.last + 1))
            builder.pop()

            lastIndex = result.range.last + 1
        }

        if (lastIndex < text.length) {
            builder.append(text.substring(lastIndex))
        }

        return TransformedText(
            builder.toAnnotatedString(),
            OffsetMapping.Identity,
        )
    }
}
