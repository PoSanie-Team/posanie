package dev.timatifey.posanie.model.mappers

typealias LessonData = dev.timatifey.posanie.model.data.Lesson
typealias LessonCache = dev.timatifey.posanie.model.cache.Lesson
typealias LessonDomain = dev.timatifey.posanie.model.domain.Lesson

class LessonMapper: Mapper<LessonData, LessonCache, LessonDomain>() {
    override fun dataToCache(data: LessonData): LessonCache {
        with(data) {
            return LessonCache(id, start, end, name, type, place, teacher, lmsUrl)
        }
    }

    override fun dataToDomain(data: LessonData): LessonDomain {
        with(data) {
            return LessonDomain(id, start, end, name, type, place, teacher, lmsUrl)
        }
    }

    override fun cacheToDomain(cache: LessonCache): LessonDomain {
        with(cache) {
            return LessonDomain(id, start, end, name, type, place, teacher, lmsUrl)
        }
    }

    override fun domainToCache(domain: LessonDomain): LessonCache {
        with(domain) {
            return LessonCache(id, start, end, name, type, place, teacher, lmsUrl)
        }
    }
}