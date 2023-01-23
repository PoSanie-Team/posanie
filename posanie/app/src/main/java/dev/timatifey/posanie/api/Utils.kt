package dev.timatifey.posanie.api

import org.json.JSONObject
import org.jsoup.Jsoup

internal fun String.getJsonObjectIgnoringContentType(): JSONObject = Jsoup
    .connect(this)
    .ignoreContentType(true)
    .execute()
    .body()
    .run {
        JSONObject(this)
    }