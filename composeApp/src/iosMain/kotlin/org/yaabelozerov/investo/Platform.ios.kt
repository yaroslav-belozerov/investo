package org.yaabelozerov.investo

import io.ktor.client.engine.darwin.Darwin
import platform.UIKit.UIDevice
import platform.Foundation.NSNumber
import platform.Foundation.NSNumberFormatter
import kotlin.math.pow
import kotlin.math.round

class IOSPlatform: Platform {
    override val name: String = UIDevice.currentDevice.systemName() + " " + UIDevice.currentDevice.systemVersion
}

actual fun getPlatform(): Platform = IOSPlatform()


actual object Net {
    actual val engine = Darwin.create {
        configureRequest {
            setAllowsCellularAccess(true)
        }
    }
}