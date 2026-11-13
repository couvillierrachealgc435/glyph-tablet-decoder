package com.glyphtablet.decoder.presentation.game

import androidx.compose.animation.*
import androidx.compose.animation.core.Animatable
import androidx.compose.animation.core.spring
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.relocation.BringIntoViewRequester
import androidx.compose.foundation.relocation.bringIntoViewRequester
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.scale
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.semantics.*
import androidx.compose.ui.unit.dp
import com.glyphtablet.decoder.R
import com.glyphtablet.decoder.core.theme.*
import com.glyphtablet.decoder.core.ui.*

@Composable
fun GlyphCell(cell: GlyphCellState, selected: Boolean, enabled: Boolean, firstSelected: Boolean, onSelect: (Int) -> Unit) {
    val glyph = cell.glyph
    if (glyph == null) {
        Box(Modifier.width(16.dp).height(64.dp), contentAlignment = Alignment.Center) {
            Text(stringResource(R.string.punctuation, cell.punctuation?.toString().orEmpty()), style = MaterialTheme.typography.titleLarge)
        }
        return
    }
    val requester = remember { BringIntoViewRequester() }
    val bounce = remember { Animatable(1f) }
    LaunchedEffect(firstSelected) { if (firstSelected) requester.bringIntoView() }
    LaunchedEffect(cell.letter) {
        if (cell.letter != null) { bounce.snapTo(1.04f); bounce.animateTo(1f, spring(dampingRatio = 0.78f, stiffness = 420f)) }
    }
    val description = if (cell.letter == null) stringResource(R.string.sign_unanswered, glyph + 1, cell.occurrences)
        else stringResource(R.string.sign_solved, glyph + 1, stringResource(TextResources.letters[cell.letter - 'A']), cell.occurrences)
    val selection = stringResource(if (selected) R.string.selected else if (cell.letter != null) R.string.solved_state else R.string.unanswered)
    val modifier = Modifier.width(48.dp).heightIn(min = 64.dp).bringIntoViewRequester(requester).scale(bounce.value).semantics {
        contentDescription = description
        stateDescription = selection
    }
    val shape = RoundedCornerShape(10.dp)
    val face: @Composable () -> Unit = {
        AnimatedContent(targetState = cell.letter, transitionSpec = { fadeIn() togetherWith fadeOut() }, label = "mark") { letter ->
            Column(Modifier.widthIn(min = 36.dp).heightIn(min = 52.dp), horizontalAlignment = Alignment.CenterHorizontally, verticalArrangement = Arrangement.Center) {
                GlyphMark(glyph, if (letter == null) 26.dp else 14.dp, if (selected) ActionBlue else Ink)
                if (letter != null) Text(stringResource(TextResources.letters[letter - 'A']), style = MaterialTheme.typography.titleLarge)
                else HorizontalDivider(Modifier.width(24.dp).padding(top = 8.dp), thickness = if (selected) 2.dp else 1.dp, color = if (selected) ActionBlue else MutedInk)
            }
        }
    }
    if (cell.letter == null) {
        OutlinedButton(onClick = { onSelect(glyph) }, enabled = enabled, modifier = modifier, shape = shape,
            border = BorderStroke(if (selected) 2.dp else 1.dp, if (selected) ActionBlue else Terracotta.copy(alpha = 0.3f)),
            contentPadding = PaddingValues(4.dp), colors = ButtonDefaults.outlinedButtonColors(containerColor = if (selected) Parchment else Sandstone.copy(alpha = 0.6f), disabledContainerColor = Sandstone.copy(alpha = 0.3f))) { face() }
    } else Surface(modifier, color = Parchment.copy(alpha = 0.7f), shape = shape, border = BorderStroke(1.dp, Sandstone)) { Box(contentAlignment = Alignment.Center) { face() } }
}
