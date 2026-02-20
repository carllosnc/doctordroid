package com.carlosnc.doctordroid.ui.components.health

import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.gestures.detectDragGestures
import androidx.compose.foundation.layout.*
import androidx.compose.material3.*
import androidx.compose.runtime.*
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.geometry.Size
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.input.pointer.pointerInput
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.dp

@Composable
fun TouchTestOverlay(onDismiss: () -> Unit, onComplete: () -> Unit) {
    var touchedCells by remember { mutableStateOf(setOf<Int>()) }
    val rows = 15
    val cols = 10
    val totalCells = rows * cols

    LaunchedEffect(touchedCells.size) {
        if (touchedCells.size >= totalCells) {
            onComplete()
        }
    }

    Box(
        modifier = Modifier
            .fillMaxSize()
            .background(Color.Black.copy(alpha = 0.8f))
            .pointerInput(Unit) {
                detectDragGestures(
                    onDrag = { change, _ ->
                        val x = change.position.x
                        val y = change.position.y
                        val cellW = size.width / cols
                        val cellH = size.height / rows
                        val col = (x / cellW).toInt().coerceIn(0, cols - 1)
                        val row = (y / cellH).toInt().coerceIn(0, rows - 1)
                        touchedCells = touchedCells + (row * cols + col)
                    }
                )
            }
    ) {
        Canvas(modifier = Modifier.fillMaxSize()) {
            val cellW = size.width / cols
            val cellH = size.height / rows

            for (r in 0 until rows) {
                for (c in 0 until cols) {
                    val index = r * cols + c
                    if (touchedCells.contains(index)) {
                        drawRect(
                            color = Color.Green.copy(alpha = 0.5f),
                            topLeft = Offset(c * cellW, r * cellH),
                            size = Size(cellW, cellH)
                        )
                    } else {
                        drawRect(
                            color = Color.White.copy(alpha = 0.1f),
                            topLeft = Offset(c * cellW, r * cellH),
                            size = Size(cellW, cellH)
                        )
                    }
                }
            }
        }
        
        Text(
            text = "Touch all areas to complete (${touchedCells.size}/$totalCells)",
            color = Color.White,
            modifier = Modifier.align(Alignment.TopCenter).padding(top = 40.dp),
            fontWeight = FontWeight.Bold
        )
        
        TextButton(
            onClick = onDismiss,
            modifier = Modifier.align(Alignment.BottomCenter).padding(bottom = 40.dp)
        ) {
            Text("Cancel Test", color = Color.White)
        }
    }
}
