/*
 * Copyright (c) 2024. James Roberts
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 */

package com.contexts.cosmic.ui.composables
import android.text.SpannableString
import android.text.style.URLSpan
import android.text.util.Linkify
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.remember
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.SpanStyle
import androidx.compose.ui.text.buildAnnotatedString
import androidx.compose.ui.text.style.TextDecoration
import androidx.core.text.util.LinkifyCompat

@Composable
fun AutoLinkText(
    text: String,
    modifier: Modifier = Modifier,
    linkColor: androidx.compose.ui.graphics.Color = MaterialTheme.colorScheme.primary,
) {
    val annotatedString =
        remember(text) {
            val spannableString = SpannableString(text)
            LinkifyCompat.addLinks(spannableString, Linkify.WEB_URLS or Linkify.EMAIL_ADDRESSES)

            buildAnnotatedString {
                append(text)
                spannableString.getSpans(0, text.length, URLSpan::class.java).forEach { urlSpan ->
                    val start = spannableString.getSpanStart(urlSpan)
                    val end = spannableString.getSpanEnd(urlSpan)

                    addStyle(
                        style =
                            SpanStyle(
                                color = linkColor,
                                textDecoration = TextDecoration.Underline,
                            ),
                        start = start,
                        end = end,
                    )

                    addStringAnnotation(
                        tag = "URL",
                        annotation = urlSpan.url,
                        start = start,
                        end = end,
                    )
                }
            }
        }

    Text(
        text = annotatedString,
        modifier = modifier,
        style =
            MaterialTheme.typography.bodyMedium.copy(
                color = MaterialTheme.colorScheme.onSurfaceVariant,
            ),
    )

    ClickableAnnotatedText(
        annotatedString = annotatedString,
        modifier = modifier,
    )
}
