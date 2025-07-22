package io.github.nogll.bencode

import io.github.nogll.bencode.model.*
import java.io.ByteArrayOutputStream
import java.io.OutputStream

class Encoder {
    fun encode(root: BElement, to: OutputStream) {
        StatefulEncoder(root, to).encode()
    }

    fun encode(root: BElement) = ByteArrayOutputStream().run {
        encode(root, this)
        toByteArray()
    }
}

private class StatefulEncoder(val root: BElement, val out: OutputStream) {
    companion object {
        const val INT = 'i'.code
        const val DICT = 'd'.code
        const val LIST = 'l'.code
        const val END = 'e'.code
        const val SPLIT = ':'.code
    }

    fun encode() = root.encode()

    private fun BElement.encode() {
        when (this) {
            is BInt -> add(this.toNumOrThrow())
            is BString -> add(this)
            is BList -> add(this.toListOrThrow())
            is BDict -> add(this.toDictOrThrow())
        }
    }

    private fun Long.toByteArray() = this.toString().encodeToByteArray()

    private fun Int.toByteArray() = this.toString().encodeToByteArray()

    private fun add(num: Long) {
        out.run {
            write(INT)
            write(num.toByteArray())
            write(END)
        }
    }

    private fun add(bString: BString) {
        out.run {
            write(bString.bytes.size.toByteArray())
            write(SPLIT)
            write(bString.bytes)
        }
    }

    private fun add(blist: List<BElement>) {
        out.run {
            write(LIST)
            blist.forEach { it.encode() }
            write(END)
        }
    }

    private fun add(bdict: Map<BString, BElement>) {
        out.run {
            write(DICT)
            bdict.forEach { (key, value) ->
                key.encode()
                value.encode()
            }
            write(END)
        }
    }
}