package io.github.nogll.tracker.services

import io.github.nogll.bencode.model.BString
import io.github.nogll.bencode.schemas.TrackerResponse
import io.github.nogll.tracker.db.model.PeerInfo
import io.github.nogll.tracker.model.TrackerRequest
import io.github.nogll.tracker.utils.fromBase64
import io.github.nogll.tracker.utils.toBase64
import io.github.oshai.kotlinlogging.KotlinLogging
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service
import kotlin.streams.toList
import kotlin.time.Duration.Companion.minutes

@Service
class TrackerService(
    val torrentPeersService: TorrentPeersService,
    val peersInfoService: PeersInfoService
) {
    val log = KotlinLogging.logger { }

    fun getPeers(request: TrackerRequest): TrackerResponse {
        log.info { "Requested $request" }
        peersInfoService.savePeer(request)

        val infoHash = request.infoHash.toBase64()
        val peerId = request.peerId.toBase64()

        torrentPeersService.addPeer(infoHash, peerId)
        val peerIds = torrentPeersService.randomPeersString(infoHash, request.numWant + 1)
            .asSequence()
            .filter { it != peerId }
            .take(request.numWant)
            .toList()

        val peers = peersInfoService.getPeersById(peerIds)
            .map { peer -> TrackerResponse.Peer(peer.peerId.fromBase64(), peer.ip, peer.port) }

        return TrackerResponse(
            null,
            1.minutes,
            1.minutes,
            BString("123"),
            0,
            peers.size,
            peers
        )
    }
}