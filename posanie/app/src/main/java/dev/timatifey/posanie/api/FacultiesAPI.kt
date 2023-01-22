package dev.timatifey.posanie.api

import dev.timatifey.posanie.api.Constants.Companion.BASE_URL
import dev.timatifey.posanie.model.data.Faculty

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class FacultiesAPI(private val dispatcher: CoroutineDispatcher) {

    suspend fun getFacultiesList(): List<Faculty> = withContext(dispatcher) {
        return@withContext buildList {
            Jsoup.connect(BASE_URL).get()
                .select(".faculty-list__link")
                .forEach { element ->
                    // /faculty/100/groups
                    val id = element.attr("href").split("/")[2].toLong()
                    add(Faculty(id = id, title = element.text()))
                }
        }
    }

}
