package io.github.nogll.bencode.model

class BDict(
    val map: Map<BString, BElement>,
    from: Int,
    end: Int
) : BElement(from, end) {
    constructor(map: Map<BString, BElement>) : this(map, 0, 0)

    override fun toDictOrThrow(): Map<BString, BElement> = map

    override fun asDict(): Map<BString, BElement>? = map

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BDict

        return map == other.map
    }

    override fun hashCode(): Int {
        return map.hashCode()
    }
}
