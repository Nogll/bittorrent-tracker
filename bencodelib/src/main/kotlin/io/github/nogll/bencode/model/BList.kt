package io.github.nogll.bencode.model

class BList(
    val list: List<BElement>,
    from: Int,
    end: Int
) : BElement(from, end) {
    constructor(list: List<BElement>) : this(list, 0, 0)

    override fun toListOrThrow(): List<BElement> = list

    override fun asList(): List<BElement>? = list

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BList

        return list == other.list
    }

    override fun hashCode(): Int {
        return list.hashCode()
    }
}
