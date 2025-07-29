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

    fun getLongOrThrow(name: BString): Long =
        map[name]?.asNum() ?: throw IllegalArgumentException("Не найдено Integer ${name.asString()}")

    fun getIntOrThrow(name: BString): Int =
        map[name]?.asNum()?.toInt() ?: throw IllegalArgumentException("Не найдено Integer ${name.asString()}")

    fun getStringOrThrow(name: BString): String =
        map[name]?.asBString()?.asString() ?: throw IllegalArgumentException("Не найдено String ${name.asString()}")

    fun getBStringOrThrow(name: BString): BString =
        map[name]?.asBString() ?: throw IllegalArgumentException("Не найдено BString ${name.asString()}")


}
