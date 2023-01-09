package dev.timatifey.posanie.model.mappers

typealias TeacherData = dev.timatifey.posanie.model.data.Teacher
typealias TeacherCache = dev.timatifey.posanie.model.cache.Teacher
typealias TeacherDomain = dev.timatifey.posanie.model.domain.Teacher

class TeacherMapper : Mapper<TeacherData, TeacherCache, TeacherDomain>() {
    override fun dataToCache(data: TeacherData): TeacherCache {
        with(data) {
            return TeacherCache(id, name)
        }
    }

    override fun dataToDomain(data: TeacherData): TeacherDomain {
        with(data) {
            return TeacherDomain(id, name)
        }
    }

    override fun cacheToDomain(cache: TeacherCache): TeacherDomain {
        with(cache) {
            return TeacherDomain(id, name, isPicked != 0)
        }
    }

    override fun domainToCache(domain: TeacherDomain): TeacherCache {
        with(domain) {
            return TeacherCache(id, name, if (isPicked) 1 else 0)
        }
    }
}