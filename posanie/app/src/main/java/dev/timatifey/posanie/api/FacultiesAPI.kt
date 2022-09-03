package dev.timatifey.posanie.api

import dev.timatifey.posanie.model.data.Faculty

import kotlinx.coroutines.CoroutineDispatcher
import kotlinx.coroutines.withContext
import org.jsoup.Jsoup

class FacultiesAPI(private val dispatcher: CoroutineDispatcher) {

    suspend fun getFacultiesList(): List<Faculty> = withContext(dispatcher) {
        val faculties = mutableListOf<Faculty>()
        val doc = Jsoup.connect(Constants.BASE_URL).get()
        doc.select(".faculty-list__link")
            .forEach { element ->
                // /faculty/100/groups
                val id = element.attr("href").split("/")[2].toLong()
                faculties.add(
                    Faculty(
                        id = id,
                        title = element.text()
                    )
                )
            }
        return@withContext faculties
    }

}
