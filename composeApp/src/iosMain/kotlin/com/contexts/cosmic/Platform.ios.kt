package com.contexts.cosmic

import platform.UIKit.UIDevice

class IOSPlatform : Platform {
    override val name: String =
        UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion

    override fun isIOS(): Boolean = true
}

actual fun getPlatform(): Platform = IOSPlatform()
