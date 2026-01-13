package com.smartchip.aidrink.android.ui

import androidx.compose.foundation.layout.Column
import androidx.compose.material3.Button
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable

@Composable
fun ControlButtons(onCommand: (String) -> Unit) {
    Column {
        Button(onClick = { onCommand("unlock") }) {
            Text("Destravar")
        }

        Button(onClick = { onCommand("dispenserCup") }) {
            Text("Dispenser Cup")
        }

        Button(onClick = { onCommand("shutdown") }) {
            Text("Shutdown")
        }

        Button(onClick = { onCommand("reboot") }) {
            Text("Reboot")
        }
    }
}