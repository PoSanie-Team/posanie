package dev.timatifey.posanie.api

import org.json.JSONObject
import org.jsoup.Jsoup

internal fun String.getFooterNextJsonObject(): JSONObject = Jsoup.connect(this)
    .get()
    .select("footer")
    .next()
    .html()
    .run { JSONObject(substring(indexOf("{"))) }