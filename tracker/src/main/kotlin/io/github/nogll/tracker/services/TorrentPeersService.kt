package io.github.nogll.tracker.services

import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.stereotype.Service
import kotlin.time.Clock
import kotlin.time.ExperimentalTime

@Service
class TorrentPeersService(
    val redisTemplate: StringRedisTemplate
) {
    fun addPeer(infoHash: String, peerId: String) {
        redisTemplate.opsForZSet().add(genKey(infoHash), peerId, now())
    }

    fun randomPeersString(infoHash: String, numWant: Int): List<String> {
        return redisTemplate.opsForZSet()
            .randomMembers(genKey(infoHash), numWant.toLong()) ?: emptyList()
    }

    @OptIn(ExperimentalTime::class)
    private fun now() = Clock.System.now().epochSeconds.toDouble()

    private fun genKey(infoHash: String) = "torrent.$infoHash.peers"
}