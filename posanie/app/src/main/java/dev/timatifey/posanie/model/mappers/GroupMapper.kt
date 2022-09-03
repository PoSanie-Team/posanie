package dev.timatifey.posanie.model.mappers

typealias GroupData = dev.timatifey.posanie.model.data.Group
typealias GroupCache = dev.timatifey.posanie.model.cache.Group
typealias GroupDomain = dev.timatifey.posanie.model.domain.Group

class GroupMapper : Mapper<GroupData, GroupCache, GroupDomain>() {
    override fun dataToCache(data: GroupData): GroupCache {
        with(data) {
            return GroupCache(id, title)
        }
    }

    override fun dataToDomain(data: GroupData): GroupDomain {
        with(data) {
            return GroupDomain(id, title)
        }
    }

    override fun cacheToDomain(cache: GroupCache): GroupDomain {
        with(cache) {
            return GroupDomain(id, title, isPicked)
        }
    }

    override fun domainToCache(domain: GroupDomain): GroupCache {
        with(domain) {
            return GroupCache(id, title, isPicked)
        }
    }
}