package com.moviedrinkers.moviedrinkers.network

class Cache<T, U>(
    private val map: HashMap<T, U> = hashMapOf()
) {

    fun get(key: T): U? {
        if (this.map.containsKey(key))
            return this.map[key]
        return null
    }

    fun put(key: T, value: U) {
        this.map[key] = value
    }

}