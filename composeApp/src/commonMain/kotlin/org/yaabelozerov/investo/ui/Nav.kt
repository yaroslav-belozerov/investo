package org.yaabelozerov.investo.ui

import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.filled.Search
import androidx.compose.material.icons.filled.Settings
import androidx.compose.material.icons.outlined.Search
import androidx.compose.material.icons.outlined.Settings
import androidx.compose.ui.graphics.vector.ImageVector

enum class Nav(val route: String, val iconFilled: ImageVector, val iconOutline: ImageVector) {
    MAIN("Main Page", Icons.Filled.Search, Icons.Outlined.Search),
    SETTINGS("Settings Page", Icons.Filled.Settings, Icons.Outlined.Settings)
}
