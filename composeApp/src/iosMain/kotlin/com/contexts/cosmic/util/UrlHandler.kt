package com.contexts.cosmic.util

import platform.Foundation.NSURL
import platform.UIKit.UIApplication

actual class UrlHandler {
    actual fun openUrl(url: String) {
        val nsUrl = NSURL(string = url)
        UIApplication.sharedApplication.openURL(nsUrl)
    }
}
