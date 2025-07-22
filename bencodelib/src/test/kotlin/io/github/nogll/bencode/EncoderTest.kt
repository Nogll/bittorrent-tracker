package io.github.nogll.bencode

import io.github.nogll.bencode.model.BDict
import io.github.nogll.bencode.model.BElement
import io.github.nogll.bencode.model.BInt
import io.github.nogll.bencode.model.BList
import io.github.nogll.bencode.model.BString
import org.junit.jupiter.api.Assertions.*
import org.junit.jupiter.api.Test
import org.junit.jupiter.params.ParameterizedTest
import org.junit.jupiter.params.provider.CsvSource

class EncoderTest {
    val encoder = Encoder()

    @ParameterizedTest
    @CsvSource(
        "1, i1e",
        "2, i2e",
        "-1, i-1e",
        "0, i0e",
        "1234567890, i1234567890e",
        "-1234567890, i-1234567890e",
    )
    fun testEncodeInteger(num: Long, expected: String) {
        val actual = encoder.encode(BInt(num))
        assertArrayEquals(expected.toByteArray(), actual)
    }

    @ParameterizedTest
    @CsvSource(
        "a, 1:a",
        "abc, 3:abc",
        "1, 1:1",
        "qwerty, 6:qwerty"
    )
    fun testEncodeString(str: String, expected: String) {
        val actual = encoder.encode(BString(str))
        assertArrayEquals(expected.toByteArray(), actual)
    }

    @Test
    fun testEncodeList() {
        val list = BList(listOf<BElement>(
            BInt(1),
            BString("str"),
            BInt(2),
            BString("ab")
        ))

        val expected = "li1e3:stri2e2:abe".toByteArray() // l i1e 3:str i2e 2:ab e
        val actual = encoder.encode(list)
        assertArrayEquals(expected, actual)
    }

    @Test
    fun testEncodeDict() {
        val dict = BDict(mapOf(
            BString("key1") to BString("val1"),
            BString("key2") to BInt(2)
        ))

        val expected = "d4:key14:val14:key2i2ee".toByteArray() // d 4:key1 4:val1 4:key2 i2e e
        val actual = encoder.encode(dict)
        assertArrayEquals(expected, actual)
    }

}