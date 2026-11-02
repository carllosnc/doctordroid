package com.carlosnc.doctordroid.modules.cpu

import androidx.activity.compose.BackHandler
import androidx.compose.animation.AnimatedVisibility
import androidx.compose.animation.animateColorAsState
import androidx.compose.foundation.BorderStroke
import androidx.compose.foundation.Canvas
import androidx.compose.foundation.background
import androidx.compose.foundation.layout.Arrangement
import androidx.compose.foundation.layout.Box
import androidx.compose.foundation.layout.Column
import androidx.compose.foundation.layout.Row
import androidx.compose.foundation.layout.Spacer
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.foundation.layout.fillMaxWidth
import androidx.compose.foundation.layout.height
import androidx.compose.foundation.layout.padding
import androidx.compose.foundation.layout.size
import androidx.compose.foundation.layout.width
import androidx.compose.foundation.rememberScrollState
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.foundation.shape.RoundedCornerShape
import androidx.compose.foundation.verticalScroll
import androidx.compose.material.icons.Icons
import androidx.compose.material.icons.automirrored.outlined.ArrowBack
import androidx.compose.material.icons.outlined.Close
import androidx.compose.material.icons.outlined.LocalFireDepartment
import androidx.compose.material.icons.outlined.PlayArrow
import androidx.compose.material.icons.outlined.Speed
import androidx.compose.material.icons.outlined.Stop
import com.carlosnc.doctordroid.ui.components.PageTitle
import androidx.compose.material3.Button
import androidx.compose.material3.ButtonDefaults
import androidx.compose.material3.Card
import androidx.compose.material3.CardDefaults
import androidx.compose.material3.ExperimentalMaterial3Api
import androidx.compose.material3.FilterChip
import androidx.compose.material3.FilterChipDefaults
import androidx.compose.material3.HorizontalDivider
import androidx.compose.material3.Icon
import androidx.compose.material3.IconButton
import androidx.compose.material3.LinearProgressIndicator
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Scaffold
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.material3.TopAppBar
import androidx.compose.material3.TopAppBarDefaults
import androidx.compose.runtime.Composable
import androidx.compose.runtime.LaunchedEffect
import androidx.compose.runtime.getValue
import androidx.compose.runtime.mutableFloatStateOf
import androidx.compose.runtime.mutableIntStateOf
import androidx.compose.runtime.mutableStateListOf
import androidx.compose.runtime.mutableStateOf
import androidx.compose.runtime.remember
import androidx.compose.runtime.rememberCoroutineScope
import androidx.compose.runtime.setValue
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.geometry.Offset
import androidx.compose.ui.graphics.Brush
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.graphics.Path
import androidx.compose.ui.graphics.StrokeCap
import androidx.compose.ui.graphics.drawscope.Stroke
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import androidx.compose.ui.unit.sp
import kotlinx.coroutines.Dispatchers
import kotlinx.coroutines.Job
import kotlinx.coroutines.delay
import kotlinx.coroutines.isActive
import kotlinx.coroutines.launch
import kotlinx.coroutines.withContext
import java.util.concurrent.atomic.AtomicLong
import kotlin.math.max
import kotlin.math.min

@OptIn(ExperimentalMaterial3Api::class)
@Composable
fun CpuStressTestOverlay(
    onDismiss: () -> Unit
) {
    val coreCount = remember { Runtime.getRuntime().availableProcessors() }
    var testDurationSeconds by remember { mutableIntStateOf(15) }
    var isRunning by remember { mutableStateOf(false) }
    var secondsElapsed by remember { mutableIntStateOf(0) }

    val scores = remember { mutableStateListOf<Float>() }
    var maxScore by remember { mutableFloatStateOf(0f) }
    var currentScore by remember { mutableFloatStateOf(0f) }
    var testFinished by remember { mutableStateOf(false) }

    BackHandler {
        onDismiss()
    }

    // Stress runner loop
    LaunchedEffect(isRunning) {
        if (!isRunning) return@LaunchedEffect

        secondsElapsed = 0
        scores.clear()
        maxScore = 0f
        currentScore = 0f
        testFinished = false

        val workCounter = AtomicLong(0)
        val workerJobs = mutableListOf<Job>()

        // Launch intensive worker on every core
        repeat(coreCount) {
            val job = launch(Dispatchers.Default) {
                while (isActive) {
                    // Intensive math: prime search & sqrt operations
                    var primesFound = 0L
                    for (n in 2..2000) {
                        var isPrime = true
                        var d = 2
                        while (d * d <= n) {
                            if (n % d == 0) { isPrime = false; break }
                            d++
                        }
                        if (isPrime) primesFound++
                    }
                    workCounter.addAndGet(primesFound)
                }
            }
            workerJobs.add(job)
        }

        // Sampling loop (every 1 second)
        while (isRunning && secondsElapsed < testDurationSeconds) {
            delay(1000)
            secondsElapsed++

            val operations = workCounter.getAndSet(0)
            val scoreGips = operations / 10_000f // Normalized GIPS score
            currentScore = scoreGips

            if (scoreGips > maxScore) {
                maxScore = scoreGips
            }
            scores.add(scoreGips)
        }

        // Clean up workers
        workerJobs.forEach { it.cancel() }
        isRunning = false
        testFinished = true
    }

    Scaffold(
        modifier = Modifier.fillMaxSize(),
        topBar = {
            TopAppBar(
                title = { PageTitle("CPU Stress Test") },
                navigationIcon = {
                    IconButton(onClick = onDismiss) {
                        Icon(
                            imageVector = Icons.AutoMirrored.Outlined.ArrowBack,
                            contentDescription = "Voltar"
                        )
                    }
                }
            )
        }
    ) { padding ->
        Column(
            modifier = Modifier
                .fillMaxSize()
                .padding(padding)
                .verticalScroll(rememberScrollState())
                .padding(16.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            // Duration selector
            Row(
                modifier = Modifier.fillMaxWidth().padding(horizontal = 4.dp),
                horizontalArrangement = Arrangement.spacedBy(10.dp),
                verticalAlignment = Alignment.CenterVertically
            ) {
                Text(
                    text = "Duration:",
                    style = MaterialTheme.typography.bodyMedium,
                    fontWeight = FontWeight.SemiBold,
                    color = MaterialTheme.colorScheme.onSurfaceVariant
                )
                listOf(15, 30, 60).forEach { sec ->
                    FilterChip(
                        selected = testDurationSeconds == sec,
                        onClick = { if (!isRunning) testDurationSeconds = sec },
                        label = {
                            Text(
                                text = "${sec}s",
                                fontWeight = if (testDurationSeconds == sec) FontWeight.Bold else FontWeight.Medium
                            )
                        },
                        enabled = !isRunning,
                        shape = CircleShape,
                        colors = FilterChipDefaults.filterChipColors(
                            selectedContainerColor = MaterialTheme.colorScheme.primary,
                            selectedLabelColor = MaterialTheme.colorScheme.onPrimary
                        ),
                        border = FilterChipDefaults.filterChipBorder(
                            enabled = !isRunning,
                            selected = testDurationSeconds == sec
                        )
                    )
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Real-Time Graph Card
            Card(
                modifier = Modifier.fillMaxWidth(),
                shape = RoundedCornerShape(20.dp),
                colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
            ) {
                Column(modifier = Modifier.padding(20.dp)) {
                    Row(
                        modifier = Modifier.fillMaxWidth(),
                        horizontalArrangement = Arrangement.SpaceBetween,
                        verticalAlignment = Alignment.CenterVertically
                    ) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.Speed,
                                contentDescription = null,
                                tint = MaterialTheme.colorScheme.primary,
                                modifier = Modifier.size(22.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = "Performance Curve",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold
                            )
                        }

                        if (isRunning) {
                            Text(
                                text = "${testDurationSeconds - secondsElapsed}s remaining",
                                color = MaterialTheme.colorScheme.primary,
                                fontWeight = FontWeight.Bold,
                                fontSize = 13.sp
                            )
                        }
                    }

                    Spacer(modifier = Modifier.height(18.dp))

                    // Live Line Chart Canvas
                    Box(
                        modifier = Modifier
                            .fillMaxWidth()
                            .height(190.dp)
                            .background(MaterialTheme.colorScheme.surfaceVariant.copy(alpha = 0.5f), RoundedCornerShape(14.dp))
                    ) {
                        if (scores.isEmpty()) {
                            Text(
                                text = "Press Start to begin $coreCount-Core Stress Test",
                                color = MaterialTheme.colorScheme.onSurfaceVariant,
                                style = MaterialTheme.typography.bodySmall,
                                modifier = Modifier.align(Alignment.Center)
                            )
                        } else {
                            CpuPerformanceChart(
                                scores = scores,
                                maxScore = maxScore,
                                totalSeconds = testDurationSeconds,
                                modifier = Modifier.fillMaxSize().padding(14.dp)
                            )
                        }
                    }

                    if (isRunning) {
                        Spacer(modifier = Modifier.height(16.dp))
                        LinearProgressIndicator(
                            progress = { secondsElapsed.toFloat() / testDurationSeconds },
                            modifier = Modifier
                                .fillMaxWidth()
                                .height(8.dp),
                            color = MaterialTheme.colorScheme.primary,
                            trackColor = MaterialTheme.colorScheme.outlineVariant,
                            strokeCap = StrokeCap.Round
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(20.dp))

            // Metric Cards
            Row(
                modifier = Modifier.fillMaxWidth(),
                horizontalArrangement = Arrangement.spacedBy(12.dp)
            ) {
                MetricCard(
                    title = "Peak Speed",
                    value = if (maxScore > 0) "${maxScore.toInt()} pts" else "--",
                    modifier = Modifier.weight(1f)
                )
                MetricCard(
                    title = "Current Speed",
                    value = if (currentScore > 0) "${currentScore.toInt()} pts" else "--",
                    modifier = Modifier.weight(1f)
                )
                val avgScore = if (scores.isNotEmpty()) scores.average().toFloat() else 0f
                MetricCard(
                    title = "Avg Speed",
                    value = if (avgScore > 0) "${avgScore.toInt()} pts" else "--",
                    modifier = Modifier.weight(1f)
                )
            }

            Spacer(modifier = Modifier.height(24.dp))

            // Action Button
            Button(
                onClick = {
                    isRunning = !isRunning
                },
                modifier = Modifier.fillMaxWidth().height(54.dp),
                shape = CircleShape,
                colors = ButtonDefaults.buttonColors(
                    containerColor = if (isRunning) MaterialTheme.colorScheme.error else MaterialTheme.colorScheme.primary
                )
            ) {
                Icon(
                    imageVector = if (isRunning) Icons.Outlined.Stop else Icons.Outlined.PlayArrow,
                    contentDescription = null,
                    modifier = Modifier.size(24.dp)
                )
                Spacer(modifier = Modifier.width(8.dp))
                Text(
                    text = if (isRunning) "Stop Stress Test" else "Start Stress Test ($coreCount Cores)",
                    fontWeight = FontWeight.Bold,
                    fontSize = 15.sp
                )
            }

            // Test Result Analysis Card
            if (testFinished && scores.isNotEmpty()) {
                Spacer(modifier = Modifier.height(24.dp))
                val peak = maxScore.coerceAtLeast(1f)
                val minScore = scores.minOrNull() ?: peak
                val retentionPercent = ((minScore / peak) * 100).toInt().coerceIn(0, 100)

                val isThrottling = retentionPercent < 80
                val resultColor = when {
                    retentionPercent >= 90 -> Color(0xFF10B981) // Green
                    retentionPercent >= 80 -> Color(0xFFF59E0B) // Amber
                    else -> Color(0xFFEF4444) // Red
                }

                Card(
                    modifier = Modifier.fillMaxWidth(),
                    shape = RoundedCornerShape(18.dp),
                    colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
                    border = BorderStroke(1.5.dp, resultColor)
                ) {
                    Column(modifier = Modifier.padding(20.dp)) {
                        Row(verticalAlignment = Alignment.CenterVertically) {
                            Icon(
                                imageVector = Icons.Outlined.LocalFireDepartment,
                                contentDescription = null,
                                tint = resultColor,
                                modifier = Modifier.size(26.dp)
                            )
                            Spacer(modifier = Modifier.width(10.dp))
                            Text(
                                text = if (isThrottling) "Thermal Throttling Detected" else "Excellent Thermal Stability",
                                style = MaterialTheme.typography.titleMedium,
                                fontWeight = FontWeight.Bold,
                                color = resultColor
                            )
                        }

                        Spacer(modifier = Modifier.height(12.dp))

                        Text(
                            text = "CPU retained $retentionPercent% of its maximum performance during the $testDurationSeconds-second load test.",
                            style = MaterialTheme.typography.bodyMedium,
                            color = MaterialTheme.colorScheme.onSurface
                        )

                        Spacer(modifier = Modifier.height(8.dp))

                        Text(
                            text = if (isThrottling) {
                                "The processor reduced clock speeds by ${100 - retentionPercent}% to prevent overheating."
                            } else {
                                "Your cooling solution maintained consistent high frequencies with minimal speed drop."
                            },
                            style = MaterialTheme.typography.bodySmall,
                            color = MaterialTheme.colorScheme.onSurfaceVariant
                        )
                    }
                }
            }

            Spacer(modifier = Modifier.height(32.dp))
        }
    }
}

@Composable
private fun MetricCard(title: String, value: String, modifier: Modifier = Modifier) {
    Card(
        modifier = modifier,
        shape = RoundedCornerShape(14.dp),
        colors = CardDefaults.cardColors(containerColor = MaterialTheme.colorScheme.surface),
        border = BorderStroke(1.dp, MaterialTheme.colorScheme.outlineVariant)
    ) {
        Column(
            modifier = Modifier
                .fillMaxWidth()
                .padding(vertical = 14.dp, horizontal = 6.dp),
            horizontalAlignment = Alignment.CenterHorizontally
        ) {
            Text(
                text = title,
                style = MaterialTheme.typography.labelSmall,
                color = MaterialTheme.colorScheme.onSurfaceVariant,
                textAlign = TextAlign.Center,
                maxLines = 1
            )
            Spacer(modifier = Modifier.height(6.dp))
            Text(
                text = value,
                style = MaterialTheme.typography.titleMedium,
                fontWeight = FontWeight.Bold,
                color = MaterialTheme.colorScheme.onSurface
            )
        }
    }
}

@Composable
private fun CpuPerformanceChart(
    scores: List<Float>,
    maxScore: Float,
    totalSeconds: Int,
    modifier: Modifier = Modifier
) {
    Canvas(modifier = modifier) {
        if (scores.isEmpty()) return@Canvas

        val w = size.width
        val h = size.height
        val maxVal = if (maxScore > 0f) maxScore * 1.15f else 100f

        // Draw horizontal grid lines (25%, 50%, 75%, 100%)
        listOf(0.25f, 0.5f, 0.75f, 1.0f).forEach { fraction ->
            val y = h - (h * fraction)
            drawLine(
                color = Color.Gray.copy(alpha = 0.2f),
                start = Offset(0f, y),
                end = Offset(w, y),
                strokeWidth = 1.dp.toPx()
            )
        }

        val stepX = w / (totalSeconds - 1).coerceAtLeast(1)
        val path = Path()
        val fillPath = Path()

        scores.forEachIndexed { index, score ->
            val x = index * stepX
            val y = h - ((score / maxVal).coerceIn(0f, 1f) * h)

            if (index == 0) {
                path.moveTo(x, y)
                fillPath.moveTo(x, h)
                fillPath.lineTo(x, y)
            } else {
                path.lineTo(x, y)
                fillPath.lineTo(x, y)
            }

            if (index == scores.size - 1) {
                fillPath.lineTo(x, h)
                fillPath.close()
            }
        }

        val lastScore = scores.last()
        val relativePerformance = if (maxScore > 0f) lastScore / maxScore else 1f
        val lineColor = when {
            relativePerformance >= 0.90f -> Color(0xFF10B981) // Green
            relativePerformance >= 0.75f -> Color(0xFFF59E0B) // Amber
            else -> Color(0xFFEF4444) // Red
        }

        // Draw Gradient Fill
        drawPath(
            path = fillPath,
            brush = Brush.verticalGradient(
                colors = listOf(lineColor.copy(alpha = 0.35f), Color.Transparent),
                startY = 0f,
                endY = h
            )
        )

        // Draw Line
        drawPath(
            path = path,
            color = lineColor,
            style = Stroke(width = 3.dp.toPx(), cap = StrokeCap.Round)
        )
    }
}
