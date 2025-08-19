package io.github.nogll.tracker.controllers

import io.github.nogll.bencode.Encoder
import io.github.nogll.bencode.model.BString
import io.github.nogll.tracker.model.TrackerRequest
import io.github.nogll.tracker.services.TrackerService
import jakarta.servlet.http.HttpServletRequest
import org.springframework.web.bind.annotation.GetMapping
import org.springframework.web.bind.annotation.RequestParam
import org.springframework.web.bind.annotation.RestController

@RestController
class TrackerController(
    val trackerService: TrackerService,
    val encoder: Encoder
) {
    @GetMapping("/tracker")
    fun trackerRequest(
        @RequestParam(name = "info_hash") infoHash: ByteArray,
        @RequestParam(name = "peer_id") peerId: ByteArray,
        @RequestParam port: Int,
        @RequestParam uploaded: Long,
        @RequestParam downloaded: Long,
        @RequestParam left: Long,
        @RequestParam(defaultValue = "0") compact: Int,
        @RequestParam(name = "no_peer_id", defaultValue = "0") noPeerId: Int,
        @RequestParam(required = false) event: String?,
        @RequestParam ip: String?,
        @RequestParam(defaultValue = "50") numWant: Int,
        @RequestParam(required = false) key: String?,
        @RequestParam(required = false) trackerId: String?,
        request: HttpServletRequest
        ): ByteArray {
        val peerIP = ip ?: request.remoteHost

        val trackerRequest = TrackerRequest(
            BString(infoHash),
            BString(peerId),
            port,
            uploaded,
            downloaded,
            left,
            compact == 1,
            noPeerId == 1,
            event,
            peerIP,
            numWant,
            key,
            trackerId
        )

        val response = trackerService.getPeers(trackerRequest)
        return encoder.encode(response.toBDict())
    }

    @GetMapping("/ping")
    fun ping() = "pong"
}