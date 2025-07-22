package io.github.nogll.bencode

import io.github.nogll.bencode.model.BElement
import io.github.nogll.bencode.model.BInt
import io.github.nogll.bencode.model.BString
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource
import org.junit.jupiter.params.provider.ValueSource

class ParserTest {
    val parser = Parser()

    @ParameterizedTest
    @CsvSource(
        "i0e, 0",
        "i1e,1",
        "i-1e, -1",
        "i1234e, 1234",
        "i-4321e, -4321",
    )
    fun testParseBIntCorrectly(data: String, expected: Long) {
        val bint = parser.parse(data.toByteArray()) as BInt
        assertEquals(expected, bint.toNumOrThrow())
    }

    @ParameterizedTest
    @ValueSource(strings = ["i00e", "i01e", "ie", "i-01e"])
    fun testWhenParseIncorrectBIntMustThrow(data: String) {
        assertThrows(IllegalArgumentException::class.java) {
            parser.parse(data.toByteArray()) }
    }

    @ParameterizedTest
    @CsvSource(
        "1:a, a",
        "2:ab, ab",
        "3:abc, abc"
    )
    fun testParseBStringCorrectly(data: String, expected: String) {
        val bstr = parser.parse(data.toByteArray()) as BString
        assertEquals(expected, bstr.asString())
    }

    @ParameterizedTest
    @ValueSource(strings = ["1:", "2:", "01:a", "0:", "-1:aaaa"])
    fun testWhenParsesIncorrectBStringMustThrow(data: String) {
        assertThrows(IllegalArgumentException::class.java) {
            parser.parse(data.toByteArray())
        }
    }

    @Test
    fun testDictionaryParse() {
        val data = "d4:key14:val14:key2i123ee".toByteArray() // d 4:key1 4:val1 4:key2 i123e e
        val expected = mapOf<BString, BElement>(
            BString("key1") to BString("val1"),
            BString("key2") to BInt(123)
        )

        assertEquals(expected, parser.parse(data).toDictOrThrow())
    }

    @Test
    fun testListParse() {
        val data = "l4:val1i123e4:val3e".toByteArray()// l 4:val1 i123e 4:val3 e
        val expected = listOf<BElement>(
            BString("val1"), BInt(123), BString("val3"))
        assertEquals(expected, parser.parse(data).toListOrThrow())
    }

}