package com.contexts.cosmic.util

import android.content.Context
import android.content.Intent
import android.net.Uri

actual class UrlHandler(private val context: Context) {
    actual fun openUrl(url: String) {
        val intent = Intent(Intent.ACTION_VIEW, Uri.parse(url))
        intent.addFlags(Intent.FLAG_ACTIVITY_NEW_TASK)
        context.startActivity(intent)
    }
}
