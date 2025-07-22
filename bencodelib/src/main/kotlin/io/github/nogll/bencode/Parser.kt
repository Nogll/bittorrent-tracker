package io.github.nogll.bencode

import io.github.nogll.bencode.model.BDict
import io.github.nogll.bencode.model.BElement
import io.github.nogll.bencode.model.BInt
import io.github.nogll.bencode.model.BList
import io.github.nogll.bencode.model.BString

class Parser {
    fun parse(bytes: ByteArray): BElement {
        return StatefulParser(bytes).parse()
    }
}

private class StatefulParser(val bytes: ByteArray) {
    companion object {
        const val INT = 'i'.code
        const val DICT = 'd'.code
        const val LIST = 'l'.code
        const val END = 'e'.code
        const val SPLIT = ':'.code
        const val ERROR_MESSAGE_UNEXPECTED_END = "Неожиданный конец данных"
    }

    var pos = 0

    fun parse() = parseBElement()

    /**
     * Определяет тип элемента и парсит его
     * @return возвращает сам элемент, pos указывает после конца элемента
     */
    private fun parseBElement(): BElement {
        if (pos >= bytes.size)
            throw IllegalArgumentException(ERROR_MESSAGE_UNEXPECTED_END)

        return when(bytes[pos].toInt()) {
            INT -> parseBInt()
            LIST -> parseBList()
            DICT -> parseBDict()
            else -> parseBString()
        }
    }

    /**
     * pos - указывает на 'i'
     * i123e
     * после завершения работы pos будет указывать на позицию после 'e'
     */
    private fun parseBInt(): BInt {
        val from = pos
        pos += 1 // указываем на число
        val num = parseNum() // указывает на 'e
        val end = pos
        val ret = BInt(num, from, end)
        pos++
        return ret
    }

    /**
     * pos должно указывать на начало длины строки
     * после pos будет указывать на символ после строки
     */
    private fun parseBString(): BString {
        val from = pos
        val len = parseNum().toInt() // указывает на ':'
        if (len < 1) {
            throw IllegalArgumentException("Длина строки должна быть > 0")
        }

        if (bytes[pos].toInt() != SPLIT) {
            throw IllegalArgumentException("Не найден разделитель '$SPLIT'")
        }

        pos++ // пропуск ':'
        val copyFrom = pos
        val copyEnd = pos + len

        if (copyEnd > bytes.size) {
            throw IllegalArgumentException(ERROR_MESSAGE_UNEXPECTED_END)
        }

        pos += len
        return BString(bytes.copyOfRange(copyFrom, copyEnd), from, copyEnd - 1)
    }

    /**
     * pos должно указывать на 'l'
     * после будет указывать на символ после 'e'
     */
    private fun parseBList(): BList {
        val from = pos++
        val list = mutableListOf<BElement>()
        while (pos < bytes.size && bytes[pos].toInt() != END) {
            list.add(parseBElement())
        }
        if (pos == bytes.size) {
            throw IllegalArgumentException(ERROR_MESSAGE_UNEXPECTED_END)
        }

        val end = pos++

        return BList(list, from, end)
    }

    /**
     * Парсит словарь, pos указывает на 'd'
     * в конце 'pos' будет указывать после 'e'
     */
    private fun parseBDict(): BDict {
        val from = pos++
        val map = mutableMapOf<BString, BElement>()
        while (pos < bytes.size && bytes[pos].toInt() != END) {
            val key = parseBString()
            val value = parseBElement()
            map[key] = value
        }
        val end = pos++
        return BDict(map, from, end)
    }

    /**
     * Парсит число, после pos указывает на символ после числа
     */
    private fun parseNum(): Long {
        val sign = when (bytes[pos].toInt().toChar()) {
            '-' -> {
                pos++
                -1
            }
            else -> 1
        }

        if (pos + 1 < bytes.size && bytes[pos].toInt() == '0'.code) {
            if (bytes[pos + 1].toInt().toChar().isDigit()) {
                throw IllegalArgumentException("Число с ведущим 0 недопустимо (позиция $pos)")
            }
            pos++
            return 0
        }

        if (pos >= bytes.size || !bytes[pos].toInt().toChar().isDigit()) {
            throw IllegalArgumentException("Число должно содержать хотя бы одну цифру (позиция $pos)")
        }

        var num = 0L
        while (pos < bytes.size && bytes[pos].toInt().toChar().isDigit()) {
            num = num * 10 + bytes[pos].toInt().toChar().digitToInt()
            pos++
        }

        if (num == 0L && sign == -1) {
            throw IllegalArgumentException("'-0' запрещено как число (позиция $pos)")
        }

        return sign * num
    }
}