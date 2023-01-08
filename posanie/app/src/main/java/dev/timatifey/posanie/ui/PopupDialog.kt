package dev.timatifey.posanie.ui

import androidx.compose.foundation.clickable
import androidx.compose.foundation.layout.*
import androidx.compose.foundation.shape.CircleShape
import androidx.compose.material3.Card
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Alignment
import androidx.compose.ui.Modifier
import androidx.compose.ui.draw.clip
import androidx.compose.ui.res.stringResource
import androidx.compose.ui.text.style.TextAlign
import androidx.compose.ui.unit.dp
import dev.timatifey.posanie.R


@Composable
fun PopupDialog(
    modifier: Modifier = Modifier,
    description: String,
    onConfirm: () -> Unit,
    onCancel: () -> Unit
) {
    Card(
        modifier = modifier
    ) {
        Column {
            Text(modifier = Modifier.padding(6.dp), text = description, textAlign = TextAlign.Center)
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
        horizontalArrangement = Arrangement.SpaceEvenly
    ) {
        DialogButton(text = cancelButtonText, onClick = onCancel)
        DialogButton(text = confirmButtonText, onClick = onConfirm)
    }
}

@Composable
fun DialogButton(
    modifier: Modifier = Modifier.padding(horizontal = 8.dp, vertical = 4.dp),
    text: String,
    onClick: () -> Unit
) {
    Box(
        modifier = Modifier
            .clip(CircleShape)
            .clickable { onClick() }
            .then(modifier)
    ) {
        Text(text = text, textAlign = TextAlign.Center)
    }
}