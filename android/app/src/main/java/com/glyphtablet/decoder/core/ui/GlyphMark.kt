package com.glyphtablet.decoder.core.ui

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.layout.size
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.*
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.graphics.drawscope.scale
import androidx.compose.ui.unit.Dp
import androidx.compose.ui.unit.dp
import com.glyphtablet.decoder.core.theme.*

@Composable
fun GlyphMark(glyph: Int, size: Dp = 28.dp, tint: Color = Ink) {
    Canvas(Modifier.size(size)) {
        scale(this.size.width / 32f, this.size.height / 32f, pivot = Offset.Zero) {
            val path = Path()
            fun line(vararg values: Float) {
                path.moveTo(values[0], values[1])
                for (i in 2 until values.size step 2) path.lineTo(values[i], values[i + 1])
            }
            when (glyph % 13) {
                0 -> line(7f,25f,7f,8f,25f,8f,25f,25f)
                1 -> { line(16f,26f,16f,7f); line(7f,10f,16f,17f,25f,10f) }
                2 -> { line(7f,8f,7f,22f,25f,22f,25f,8f); line(16f,13f,16f,27f) }
                3 -> { line(16f,6f,25f,15f,16f,24f,7f,15f,16f,6f); line(16f,24f,16f,28f) }
                4 -> { line(8f,25f,8f,8f,25f,8f); line(8f,19f,25f,19f) }
                5 -> line(7f,8f,24f,8f,24f,15f,10f,15f,10f,24f,25f,24f)
                6 -> { line(11f,6f,11f,26f); line(11f,12f,24f,8f); line(11f,21f,24f,17f) }
                7 -> { line(25f,7f,7f,7f,7f,25f,25f,25f); line(10f,22f,22f,10f) }
                8 -> { line(7f,18f,16f,7f,25f,18f); line(10f,25f,22f,25f) }
                9 -> { line(8f,8f,24f,24f); line(24f,8f,8f,24f); line(16f,16f,16f,28f) }
                10 -> { line(7f,21f,7f,7f,21f,7f); line(13f,26f,13f,13f,26f,13f) }
                11 -> { line(9f,6f,9f,26f); line(23f,6f,23f,26f); for (y in listOf(9f,16f,23f)) line(9f,y,23f,y) }
                12 -> { path.addOval(androidx.compose.ui.geometry.Rect(7f,7f,25f,24f)); line(16f,7f,16f,17f); line(13f,24f,16f,28f,19f,24f) }
            }
            if (glyph < 13) line(5f,9f,5f,5f,10f,5f) else { line(23f,28f,28f,28f,28f,23f); drawCircle(tint, 1.8f, Offset(16f,16f)) }
            drawPath(path, Parchment.copy(alpha = 0.55f), style = Stroke(3.2f, cap = StrokeCap.Round, join = StrokeJoin.Round))
            drawPath(path, tint, style = Stroke(1.9f, cap = StrokeCap.Square, join = StrokeJoin.Round))
        }
    }
}
