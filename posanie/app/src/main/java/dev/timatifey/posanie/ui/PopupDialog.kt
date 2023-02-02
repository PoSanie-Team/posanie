package dev.timatifey.posanie.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.ColorScheme
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.graphics.Color
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.font.FontWeight
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.timatifey.posanie.R


@Composable
fun PopupDialog(
    modifier: Modifier = Modifier,
    title: String,
    description: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = modifier
    ) {
        Column {
            Text(
                modifier = Modifier.padding(start = 16.dp, top = 16.dp, end = 16.dp, bottom = 8.dp),
                text = title,
                textAlign = TextAlign.Start,
                style = MaterialTheme.typography.titleLarge,
                color = MaterialTheme.colorScheme.onSurface
            )
            Text(
                modifier = Modifier.padding(horizontal = 16.dp),
                text = description,
                textAlign = TextAlign.Start,
            )
            DialogBar(onConfirm = onConfirm, onCancel = onCancel)
        }
    }
}

@Composable
fun DialogBar(
    modifier: Modifier = Modifier,
    cancelButtonText: String = stringResource(R.string.cancel),
    confirmButtonText: String = stringResource(R.string.ok),
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Row(
        modifier = modifier
            .fillMaxSize()
            .padding(8.dp),
        verticalAlignment = Alignment.Bottom,
        horizontalArrangement = Arrangement.End
    ) {
        DialogButton(
            text = cancelButtonText,
            textColor = MaterialTheme.colorScheme.error,
            onClick = onCancel
        )
        DialogButton(
            text = confirmButtonText,
            textColor = MaterialTheme.colorScheme.primary,
            onClick = onConfirm
        )
    }
}

@Composable
fun DialogButton(
    modifier: Modifier = Modifier
        .padding(horizontal = 8.dp, vertical = 4.dp),
    text: String,
    textColor: Color = Color.Unspecified,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onClick() }
            .then(modifier),
        contentAlignment = Alignment.Center
    ) {
        Text(
            text = text,
            textAlign = TextAlign.Center,
            color = textColor,
            fontWeight = FontWeight.Bold
        )
    }
}