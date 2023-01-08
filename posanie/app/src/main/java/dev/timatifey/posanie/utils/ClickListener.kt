package dev.timatifey.posanie.utils

class ClickListener<T>(
    val onClick: (T) -> Unit = {},
    val onLongClick: (T) -> Unit = {}
)