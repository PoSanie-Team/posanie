package dev.timatifey.posanie.model.mappers

typealias FacultyData = dev.timatifey.posanie.model.data.Faculty
typealias FacultyCache = dev.timatifey.posanie.model.cache.Faculty
typealias FacultyDomain = dev.timatifey.posanie.model.domain.Faculty

class FacultyMapper : Mapper<FacultyData, FacultyCache, FacultyDomain>() {
    override fun dataToCache(data: FacultyData): FacultyCache {
        with(data) {
            return FacultyCache(id, title)
        }
    }

    override fun dataToDomain(data: FacultyData): FacultyDomain {
        with(data) {
            return FacultyDomain(id, title)
        }
    }

    override fun cacheToDomain(cache: FacultyCache): FacultyDomain {
        with(cache) {
            return FacultyDomain(id, title)
        }
    }

    override fun domainToCache(domain: FacultyDomain): FacultyCache {
        with(domain) {
            return FacultyCache(id, title)
        }
    }
}