package ru.altmanea.edu.server.repo

import java.lang.System.currentTimeMillis
import java.util.concurrent.ConcurrentHashMap

class ListRepo<E> : Repo<E> {
    private val list = ConcurrentHashMap<String, Pair<E, Long>>()

    override fun get(uuid: String): RepoItem<E>? =
        list[uuid]?.let {
            RepoItem(it.first, uuid, it.second)
        }

    override fun find(predicate: (E) -> Boolean): List<RepoItem<E>> =
        list
            .filter { (_, value) -> predicate(value.first) }
            .map { RepoItem(it.value.first, it.key, it.value.second) }

    override fun findAll(): List<RepoItem<E>> =
        list
            .map { RepoItem(it.value.first, it.key, it.value.second) }

    @Suppress("KotlinConstantConditions")
    override fun create(element: E): Boolean =
        RepoItem(element).let {
            list[it.uuid] = it.elem to it.etag
            true
        }


    override fun update(uuid: String, value: E): Boolean =
        if (list.containsKey(uuid)) {
            list[uuid] = value to currentTimeMillis()
            true
        } else false

    override fun delete(uuid: String): Boolean =
        list.remove(uuid) != null


    override fun isEmpty(): Boolean =
        list.isEmpty()

}