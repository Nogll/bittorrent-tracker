package io.github.nogll.bencode.schemas

import io.github.nogll.bencode.Encoder
import io.github.nogll.bencode.Parser
import io.github.nogll.bencode.model.BDict
import io.github.nogll.bencode.model.toBString
import io.github.nogll.bencode.schemas.TrackerResponse.Companion.toTrackerResponse
import io.github.nogll.bencode.schemas.TrackerResponse.Companion.toTrackerResponseOrNull
import org.junit.jupiter.api.Assertions.assertEquals
import org.junit.jupiter.api.Assertions.assertThrows
import org.junit.jupiter.api.Test
import kotlin.time.Duration.Companion.seconds

class TrackerResponseTest {
    val parser = Parser()
    val encoder = Encoder()

    @Test
    fun testFromBDictAndToBDict_DictModel() {
        val peers = listOf(
            TrackerResponse.Peer("peer1".toBString(), "192.168.0.1", 6881),
            TrackerResponse.Peer("peer2".toBString(), "192.168.0.2", 6882)
        )
        val response = TrackerResponse(
            warningMessage = "test warning",
            interval = 1800.seconds,
            minInterval = 900.seconds,
            trackerId = "tracker123".toBString(),
            complete = 10,
            incomplete = 5,
            peers = peers
        )

        val bdict = response.toBDict()
        val parsed = TrackerResponse.fromBDict(bdict)

        assertEquals(response, parsed)
    }

    @Test
    fun testFromBDictAndToBDict_BinaryModel() {
        val peers = listOf(
            TrackerResponse.Peer(null, "10.0.0.1", 6881),
            TrackerResponse.Peer(null, "10.0.0.2", 6882)
        )
        val response = TrackerResponse(
            warningMessage = null,
            interval = 1200.seconds,
            minInterval = null,
            trackerId = "trackerABC".toBString(),
            complete = 20,
            incomplete = 8,
            peers = peers
        )

        val bdict = response.toBDict()
        val parsed = TrackerResponse.fromBDict(bdict)

        assertEquals(response, parsed)
    }

    @Test
    fun testFailureResponseThrowsException() {
        val failureDict = BDict(
            mapOf(
                TrackerResponse.FAILURE_REASON to "Something went wrong".toBString()
            )
        )

        assertThrows(FailureResponseException::class.java) { TrackerResponse.fromBDict(failureDict) }
    }

    @Test
    fun testConvertFromAndToByteArray_DictModel() {
        val peers = listOf(
            TrackerResponse.Peer("peer1".toBString(), "192.168.0.1", 6881),
            TrackerResponse.Peer("peer2".toBString(), "192.168.0.2", 6882)
        )
        val response = TrackerResponse(
            warningMessage = "test warning",
            interval = 1800.seconds,
            minInterval = 900.seconds,
            trackerId = "tracker123".toBString(),
            complete = 10,
            incomplete = 5,
            peers = peers
        )

        val encoded = encoder.encode(response.toBDict())
        val parsed = parser.parse(encoded).toTrackerResponseOrNull()

        assertEquals(response, parsed)
    }

}