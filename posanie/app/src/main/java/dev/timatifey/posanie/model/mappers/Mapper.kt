package dev.timatifey.posanie.model.mappers

abstract class Mapper<DATA, CACHE, DOMAIN> {
    abstract fun dataToCache(data: DATA): CACHE
    abstract fun dataToDomain(data: DATA): DOMAIN
    abstract fun cacheToDomain(cache: CACHE): DOMAIN
    abstract fun domainToCache(domain: DOMAIN): CACHE
}