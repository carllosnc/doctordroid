package com.carlosnc.doctordroid.ui.components

import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.unit.sp
import java.util.Locale

@Composable
fun PageTitle(
    text: String,
    modifier: Modifier = Modifier
) {
    Text(
        text = text.uppercase(Locale.getDefault()),
        modifier = modifier,
        style = MaterialTheme.typography.titleSmall.copy(
            fontWeight = FontWeight.ExtraBold,
            letterSpacing = 1.sp
        )
    )
}
