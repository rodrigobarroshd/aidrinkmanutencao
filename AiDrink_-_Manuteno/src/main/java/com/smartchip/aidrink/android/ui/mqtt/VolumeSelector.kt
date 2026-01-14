package com.smartchip.aidrink.android.ui.mqtt

import androidx.compose.animation.animateContentSize
import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.Composable
import androidx.compose.runtime.getValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.unit.dp
import kotlin.math.roundToInt

@Composable
fun VolumeSelector(
    value: Int,
    onValueChange: (Int) -> Unit,
    range: IntRange = 0..500
) {
    val animatedValue by animateFloatAsState(
        targetValue = value.toFloat(),
        animationSpec = tween(
            durationMillis = 250,
            easing = FastOutSlowInEasing
        ),
        label = "volume-animation"
    )

    Row(
        modifier = Modifier.fillMaxWidth(),
        verticalAlignment = Alignment.CenterVertically,
        horizontalArrangement = Arrangement.spacedBy(12.dp)
    ) {

        // 🔵 Progress extremamente suave
        Slider(
            value = animatedValue,
            onValueChange = { newValue ->
                onValueChange(newValue.roundToInt().coerceIn(range))
            },
            valueRange = range.first.toFloat()..range.last.toFloat(),
            steps = 0, // 🚫 sem degraus
            modifier = Modifier.weight(1f)
        )

        // ✍️ Input livre (1,2,3,4...)
        OutlinedTextField(
            value = value.toString(),
            onValueChange = { text ->
                text.toIntOrNull()?.let {
                    onValueChange(it.coerceIn(range))
                }
            },
            singleLine = true,
            modifier = Modifier.width(90.dp),
            label = { Text("ml") }
        )
    }
}

//@Composable
//fun VolumeSelector(
//    value: Int,
//    onValueChange: (Int) -> Unit,
//    range: IntRange = 50..350,
//    step: Int = 50
//) {
//    val stepsCount = ((range.last - range.first) / step) - 1
//
//    Row(
//        verticalAlignment = Alignment.CenterVertically,
//        horizontalArrangement = Arrangement.spacedBy(12.dp),
//        modifier = Modifier.fillMaxWidth()
//    ) {
//
//        // 🔵 "Progress" (Slider)
//        Slider(
//            value = value.toFloat(),
//            onValueChange = { raw ->
//                val snapped =
//                    ((raw / step).roundToInt()) * step
//                        .coerceIn(range.first, range.last)
//
//                onValueChange(snapped)
//            },
//            valueRange = range.first.toFloat()..range.last.toFloat(),
//            steps = stepsCount,
//            modifier = Modifier.weight(1f)
//        )
//
//        // ✍️ Input numérico
//        OutlinedTextField(
//            value = value.toString(),
//            onValueChange = { text ->
//                text.toIntOrNull()?.let {
//                    val snapped =
//                        ((it / step) * step)
//                            .coerceIn(range.first, range.last)
//
//                    onValueChange(snapped)
//                }
//            },
//            singleLine = true,
//            modifier = Modifier.width(90.dp),
//            label = { Text("ml") }
//        )
//    }
//}
