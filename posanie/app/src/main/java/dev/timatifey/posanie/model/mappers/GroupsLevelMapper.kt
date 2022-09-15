package dev.timatifey.posanie.model.mappers

import javax.inject.Inject

typealias GroupsLevelData = dev.timatifey.posanie.model.data.GroupsLevel
typealias GroupsLevelCache = dev.timatifey.posanie.model.cache.GroupsLevel
typealias GroupsLevelDomain = dev.timatifey.posanie.model.domain.GroupsLevel

class GroupsLevelMapper @Inject constructor(private val groupMapper: GroupMapper) : Mapper<GroupsLevelData, GroupsLevelCache, GroupsLevelDomain>() {
    override fun dataToCache(data: GroupsLevelData): GroupsLevelCache {
        val cache = GroupsLevelCache(data.level)
        for (group in data.getGroups()) {
            cache.add(groupMapper.dataToCache(group))
        }
        return cache
    }

    override fun dataToDomain(data: GroupsLevelData): GroupsLevelDomain {
        val domain = GroupsLevelDomain(data.level)
        for (group in data.getGroups()) {
            domain.add(groupMapper.dataToDomain(group))
        }
        return domain
    }

    override fun cacheToDomain(cache: GroupsLevelCache): GroupsLevelDomain {
        val domain = GroupsLevelDomain(cache.level)
        for (group in cache.getGroups()) {
            domain.add(groupMapper.cacheToDomain(group))
        }
        return domain
    }

    override fun domainToCache(domain: GroupsLevelDomain): GroupsLevelCache {
        val cache = GroupsLevelCache(domain.level)
        for (group in domain.getGroups()) {
            cache.add(groupMapper.domainToCache(group))
        }
        return cache
    }
}