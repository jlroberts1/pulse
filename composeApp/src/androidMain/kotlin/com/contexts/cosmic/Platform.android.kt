package com.contexts.cosmic

import android.os.Build

class AndroidPlatform : Platform {
    override val name: String = "Android ${Build.VERSION.SDK_INT}"

    override fun isIOS(): Boolean = false
}

actual fun getPlatform(): Platform = AndroidPlatform()
