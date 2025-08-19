package io.github.nogll.tracker.services

import io.github.nogll.tracker.db.model.PeerInfo
import io.github.nogll.tracker.model.TrackerRequest
import io.github.nogll.tracker.utils.toBase64
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.stereotype.Service

@Service
class PeersInfoService(
    val redisTemplate: RedisTemplate<String, PeerInfo>
) {

    /**
     * Обновляет информацию о пире если он есть в БД или создает новую запись о нем
     * @param request - запрос пира из которого будет получена информация о нем
     */
    fun savePeer(request: TrackerRequest) {
        val peerInfo = PeerInfo(
            request.peerId.toBase64(),
            request.ip,
            request.port,
            false
        )
        val key = "peer.${request.peerId.toBase64()}"
        redisTemplate.opsForValue().set(key, peerInfo)
    }

    /**
     * @param peerIds - список peerId в Base64
     * @return - информация о пирах
     */
    fun getPeersById(peerIds: List<String>): List<PeerInfo> {
        val keys = peerIds.map { "peer.$it" }
        return redisTemplate.opsForValue().multiGet(keys) ?: emptyList()
    }
}