package io.github.nogll.bencode.model

sealed class BElement(val from: Int, val end: Int) {
    open fun toNumOrThrow(): Long {
        throw IllegalArgumentException()
    }

    open fun toListOrThrow(): List<BElement> {
        throw IllegalArgumentException()
    }

    open fun toBStringOrThrow(): BString {
        throw IllegalArgumentException()
    }

    open fun toDictOrThrow(): Map<BString, BElement> {
        throw IllegalArgumentException()
    }

    open fun asNum(): Long? = null

    open fun asList(): List<BElement>? = null

    open fun asBString(): BString? = null

    open fun asDict(): Map<BString, BElement>? = null
}