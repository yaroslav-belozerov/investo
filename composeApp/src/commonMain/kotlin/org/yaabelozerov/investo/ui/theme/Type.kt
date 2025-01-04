package org.yaabelozerov.investo.ui.theme

import androidx.compose.material3.Typography
import androidx.compose.runtime.Composable
import androidx.compose.ui.text.TextStyle
import androidx.compose.ui.text.font.FontFamily
import androidx.compose.ui.text.font.FontStyle
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import investo.composeapp.generated.resources.Res
import investo.composeapp.generated.resources.inter_black
import investo.composeapp.generated.resources.inter_blackitalic
import investo.composeapp.generated.resources.inter_bold
import investo.composeapp.generated.resources.inter_bolditalic
import investo.composeapp.generated.resources.inter_extrabold
import investo.composeapp.generated.resources.inter_extrabolditalic
import investo.composeapp.generated.resources.inter_extralight
import investo.composeapp.generated.resources.inter_extralightitalic
import investo.composeapp.generated.resources.inter_italic
import investo.composeapp.generated.resources.inter_light
import investo.composeapp.generated.resources.inter_lightitalic
import investo.composeapp.generated.resources.inter_medium
import investo.composeapp.generated.resources.inter_mediumitalic
import investo.composeapp.generated.resources.inter_regular
import investo.composeapp.generated.resources.inter_semibold
import investo.composeapp.generated.resources.inter_semibolditalic
import investo.composeapp.generated.resources.inter_thin
import investo.composeapp.generated.resources.inter_thinitalic
import org.jetbrains.compose.resources.Font

private val normalFontFamily
    @Composable get() = FontFamily(
        Font(Res.font.inter_black, FontWeight.Black, FontStyle.Normal),
        Font(Res.font.inter_blackitalic, FontWeight.Black, FontStyle.Italic),
        Font(Res.font.inter_bold, FontWeight.Bold, FontStyle.Normal),
        Font(Res.font.inter_bolditalic, FontWeight.Bold, FontStyle.Italic),
        Font(Res.font.inter_extrabold, FontWeight.ExtraBold, FontStyle.Normal),
        Font(Res.font.inter_extrabolditalic, FontWeight.ExtraBold, FontStyle.Italic),
        Font(Res.font.inter_extralight, FontWeight.ExtraLight, FontStyle.Normal),
        Font(Res.font.inter_extralightitalic, FontWeight.ExtraLight, FontStyle.Italic),
        Font(Res.font.inter_regular, FontWeight.Normal, FontStyle.Normal),
        Font(Res.font.inter_italic, FontWeight.Normal, FontStyle.Italic),
        Font(Res.font.inter_light, FontWeight.Light, FontStyle.Normal),
        Font(Res.font.inter_lightitalic, FontWeight.Light, FontStyle.Italic),
        Font(Res.font.inter_medium, FontWeight.Medium, FontStyle.Normal),
        Font(Res.font.inter_mediumitalic, FontWeight.Medium, FontStyle.Italic),
        Font(Res.font.inter_semibold, FontWeight.SemiBold, FontStyle.Normal),
        Font(Res.font.inter_semibolditalic, FontWeight.SemiBold, FontStyle.Italic),
        Font(Res.font.inter_thin, FontWeight.Thin, FontStyle.Normal),
        Font(Res.font.inter_thinitalic, FontWeight.Thin, FontStyle.Italic),
    )

val Typography
    @Composable get() = Typography(
        bodyLarge = TextStyle(
            fontFamily = normalFontFamily,
            fontSize = 18.sp,
        ),
        bodyMedium = TextStyle(
            fontFamily = normalFontFamily,
            fontSize = 16.sp,
        ),
        bodySmall = TextStyle(
            fontFamily = normalFontFamily,
            fontSize = 12.sp,
        ),
    )