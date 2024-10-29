package org.yaabelozerov.investo

import platform.UIKit.UIDevice
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import kotlin.math.pow
import kotlin.math.round

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()