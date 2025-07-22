package io.github.nogll.bencode.model

class BInt(
    val num: Long,
    from: Int,
    end: Int
) : BElement(from, end) {
    constructor(num: Long) : this(num, 0, 0)

    override fun toNumOrThrow(): Long = num

    override fun asNum(): Long? = num

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BInt

        return num == other.num
    }

    override fun hashCode(): Int {
        return num.hashCode()
    }
}
