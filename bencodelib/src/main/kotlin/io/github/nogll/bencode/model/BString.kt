package io.github.nogll.bencode.model

import java.nio.charset.Charset

class BString(
    internal val bytes: ByteArray,
    from: Int,
    end: Int
) : BElement(from, end) {
    companion object {
        val DEFAULT_CHARSET = Charsets.UTF_8
    }

    constructor(str: String) : this(str.toByteArray(), 0, str.length - 1)

    override fun toBStringOrThrow(): BString = this

    override fun asBString(): BString? = this

    fun asString(charset: Charset) = String(bytes, charset)

    fun asString() = String(bytes, DEFAULT_CHARSET)

    fun asBytes() = bytes.copyOf(bytes.size)

    override fun equals(other: Any?): Boolean {
        if (this === other) return true
        if (javaClass != other?.javaClass) return false

        other as BString

        if (!bytes.contentEquals(other.bytes)) return false

        return true
    }

    override fun hashCode(): Int {
        return bytes.contentHashCode()
    }
}
