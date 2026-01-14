package com.smartchip.aidrink.android.ui.mqtt

import androidx.compose.animation.core.FastOutSlowInEasing
import androidx.compose.animation.core.animateFloatAsState
import androidx.compose.animation.core.tween
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.runtime.Composable
import androidx.compose.ui.unit.dp
import androidx.compose.material3.*
import androidx.compose.runtime.getValue
import androidx.compose.ui.Modifier
import androidx.compose.ui.graphics.graphicsLayer
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CouplerSelector(
    selectedCoupler: Int,
    onSelect: (Int) -> Unit
) {
    SingleChoiceSegmentedButtonRow(
        modifier = Modifier.fillMaxWidth()
    ) {
        (0..3).forEach { coupler ->

            val isSelected = selectedCoupler == coupler

            val weight by animateFloatAsState(
                targetValue = if (isSelected) 2.4f else 1f,
                animationSpec = tween(
                    durationMillis = 300,
                    easing = FastOutSlowInEasing
                ),
                label = "weight"
            )

            val scale by animateFloatAsState(
                targetValue = if (isSelected) 1.05f else 1f,
                animationSpec = tween(300),
                label = "scale"
            )

            SegmentedButton(
                selected = isSelected,
                onClick = { onSelect(coupler) },
                shape = SegmentedButtonDefaults.itemShape(
                    index = coupler,
                    count = 4
                ),
                icon = {},
                modifier = Modifier
                    .weight(weight)
                    .height(48.dp)
                    .graphicsLayer {
                        scaleX = scale
                        scaleY = scale
                    },
                label = {
                    Text(
                        text = "$coupler",
                        fontWeight = if (isSelected) FontWeight.Bold else FontWeight.Normal
                    )
                }
            )
        }
    }
}


//@Composable
//fun CouplerSelector(
//    selectedCoupler: Int,
//    onSelect: (Int) -> Unit
//) {
//    Row(
//        horizontalArrangement = Arrangement.spacedBy(8.dp)
//    ) {
//        (0..3).forEach { coupler ->
//            Button(
//                onClick = { onSelect(coupler) },
//                colors = if (selectedCoupler == coupler)
//                    ButtonDefaults.buttonColors(containerColor = MaterialTheme.colorScheme.primary)
//                else
//                    ButtonDefaults.buttonColors()
//            ) {
//                Text("Coupler $coupler")
//            }
//        }
//    }
//}
