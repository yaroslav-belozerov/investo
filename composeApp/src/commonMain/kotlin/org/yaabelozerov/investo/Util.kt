package org.yaabelozerov.investo

import androidx.annotation.RestrictTo
import androidx.compose.animation.AnimatedContent
import androidx.compose.animation.Crossfade
import androidx.compose.animation.core.EaseInOut
import androidx.compose.animation.core.RepeatMode
import androidx.compose.animation.core.animateFloat
import androidx.compose.animation.core.infiniteRepeatable
import androidx.compose.animation.core.rememberInfiniteTransition
import androidx.compose.animation.core.tween
import androidx.compose.animation.fadeIn
import androidx.compose.animation.fadeOut
import androidx.compose.animation.togetherWith
import androidx.compose.foundation.ScrollState
import androidx.compose.foundation.background
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.LocalTextStyle
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.runtime.DisposableEffect
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.composed
import androidx.compose.ui.draw.drawWithContent
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.RectangleShape
import androidx.compose.ui.graphics.Shape
import androidx.compose.ui.graphics.TileMode
import androidx.compose.ui.platform.debugInspectorInfo
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import cafe.adriel.lyricist.LocalStrings
import io.github.aakira.napier.log
import io.ktor.client.call.body
import io.ktor.client.plugins.ClientRequestException
import io.ktor.client.plugins.ServerResponseException
import io.ktor.client.statement.HttpResponse
import io.ktor.http.HttpStatusCode
import io.ktor.util.network.UnresolvedAddressException
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.withContext
import kotlinx.io.IOException

fun Modifier.horizontalFadingEdge(
    scrollState: ScrollState,
    length: Dp,
    edgeColor: Color? = null,
) = composed(debugInspectorInfo {
    name = "length"
    value = length
}) {
    val color = edgeColor ?: MaterialTheme.colorScheme.surface

    drawWithContent {
        val lengthValue = length.toPx()
        val scrollFromStart = scrollState.value
        val scrollFromEnd = scrollState.maxValue - scrollState.value

        val startFadingEdgeStrength = lengthValue * (scrollFromStart / lengthValue).coerceAtMost(1f)

        val endFadingEdgeStrength = lengthValue * (scrollFromEnd / lengthValue).coerceAtMost(1f)

        drawContent()

        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    color,
                    Color.Transparent,
                ),
                startX = 0f,
                endX = startFadingEdgeStrength,
            ),
            size = Size(
                startFadingEdgeStrength,
                this.size.height,
            ),
        )

        drawRect(
            brush = Brush.horizontalGradient(
                colors = listOf(
                    Color.Transparent,
                    color,
                ),
                startX = size.width - endFadingEdgeStrength,
                endX = size.width,
            ),
            topLeft = Offset(x = size.width - endFadingEdgeStrength, y = 0f),
        )
    }
}

fun Modifier.shimmerBackground(shape: Shape = RectangleShape): Modifier = composed {
    val transition = rememberInfiniteTransition()

    val translateAnimation by transition.animateFloat(
        initialValue = 0f,
        targetValue = 400f,
        animationSpec = infiniteRepeatable(
            tween(durationMillis = 2000, easing = EaseInOut), RepeatMode.Restart
        ),
        label = "",
    )
    val shimmerColors = listOf(
        MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.7f),
        MaterialTheme.colorScheme.surfaceBright.copy(alpha = 0.0f),
    )
    val brush = Brush.linearGradient(
        colors = shimmerColors,
        start = Offset(translateAnimation, translateAnimation),
        end = Offset(translateAnimation + 200f, translateAnimation + 200f),
        tileMode = TileMode.Mirror,
    )
    return@composed this.then(background(brush, shape))
}

@Composable
fun CrossfadeText(text: String, color: Color? = null, fontWeight: FontWeight? = null) =
    AnimatedContent(text, transitionSpec = { fadeIn() togetherWith fadeOut() }) {
        val textStyle = LocalTextStyle.current
        Text(it, color = color ?: textStyle.color, fontWeight = fontWeight ?: textStyle.fontWeight)
    }

inline fun <T> check(block: () -> T): Result<T> = try {
    Result.success(block())
} catch (e: Throwable) {
    Result.failure(e)
}

inline fun <T> Result<T>.onFailure(block: (Throwable?) -> Unit): T? {
    val value = getOrNull()
    if (isFailure) {
        block(exceptionOrNull())
    } else {
        if (value == null) block(exceptionOrNull())
    }
    return value
}

sealed interface NetworkResult<out T> {
    data class Success<T>(val value: T) : NetworkResult<T>
    data class Error<T>(val error: NetworkError) : NetworkResult<T>
    data object Finished : NetworkResult<Nothing>
}

enum class NetworkError {
    Server, Credentials, Network, Unknown
}

private fun HttpStatusCode.toNetworkError(): NetworkError = when (this) {
    HttpStatusCode.Forbidden, HttpStatusCode.Unauthorized -> NetworkError.Credentials
    HttpStatusCode.InternalServerError -> NetworkError.Server
    else -> NetworkError.Unknown
}

fun Throwable?.toError(): NetworkError = when (this) {
    is ServerResponseException -> this.response.status.toNetworkError()
    is ClientRequestException -> this.response.status.toNetworkError()
    is UnresolvedAddressException, is IOException -> NetworkError.Network
    else -> NetworkError.Unknown
}
