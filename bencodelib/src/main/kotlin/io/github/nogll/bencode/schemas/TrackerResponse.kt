package io.github.nogll.bencode.schemas

import io.github.nogll.bencode.model.*
import java.nio.ByteBuffer
import kotlin.time.Duration
import kotlin.time.Duration.Companion.seconds

data class TrackerResponse(
    val warningMessage: String?,
    val interval: Duration,
    val minInterval: Duration?,
    val trackerId: BString,
    val complete: Int,
    val incomplete: Int,
    val peers: List<Peer>
) {
    companion object {
        // Basic response fields
        val FAILURE_REASON = "failure reason".toBString()
        val WARNING_MESSAGE = "warning message".toBString()
        val INTERVAL = "interval".toBString()
        val MIN_INTERVAL = "min interval".toBString()
        val TRACKER_ID = "tracker id".toBString()
        val COMPLETE = "complete".toBString()
        val INCOMPLETE = "incomplete".toBString()
        val PEERS = "peers".toBString()

        // Peer dictionary model fields
        val PEER_ID = "peer id".toBString()
        val IP = "ip".toBString()
        val PORT = "port".toBString()

        private fun Int.toDottedQuad(): String =
            "${this ushr 24 and 0xFF}.${this ushr 16 and 0xFF}.${this ushr 8 and 0xFF}.${this and 0xFF}"


        /**
         * @param dict - словарь из которого будет создан TrackerResponse
         * @return TrackerResponse если не было ошибки
         *
         * @throws IllegalArgumentException если не было найдено обязательное поле
         * @throws FailureResponseException если ответ является сообщением об ошибке
         */
        fun fromBDict(dict: BDict): TrackerResponse {
            dict.map[FAILURE_REASON]?.let {
                val message = it.asBString()?.asString()
                    ?: throw IllegalArgumentException("Сообщение об ошибке должно быть String")
                throw FailureResponseException(message)
            }

            val peersElement = dict.map[PEERS]
            val peers: List<Peer> = when (peersElement) {
                is BString -> buildList {
                    val n = peersElement.bytes.size
                    if (n % 6 != 0)
                        throw IllegalArgumentException("Длина строки должна быть кратна 6 в Binary model")
                    val buffer = ByteBuffer.wrap(peersElement.bytes)
                    for (i in 0..<n/6) {
                        add(Peer(
                            null,
                            buffer.getInt().toDottedQuad(),
                            buffer.getShort().toInt()
                        ))
                    }

                }
                is BList -> buildList {
                    peersElement.list
                        .map { it as? BDict ?: throw IllegalArgumentException("Peer должен быть словарем") }
                        .forEach { add(Peer(
                            it.getBStringOrThrow(PEER_ID),
                            it.getStringOrThrow(IP),
                            it.getIntOrThrow(PORT)
                        )) }
                }

                else -> throw IllegalArgumentException("Не найден список пиров")
            }

            return TrackerResponse(
                dict.map[WARNING_MESSAGE]?.asBString()?.asString(),
                dict.getLongOrThrow(INTERVAL).seconds,
                dict.map[MIN_INTERVAL]?.asNum()?.seconds,
                dict.getBStringOrThrow(TRACKER_ID),
                dict.getIntOrThrow(COMPLETE),
                dict.getIntOrThrow(INCOMPLETE),
                peers
            )
        }

        /**
         * Вызывает TrackerResponse.fromBDict(this)
         * @return TrackerResponse если не было ошибки
         * @throws IllegalArgumentException если не было найдено обязательное поле
         * @throws FailureResponseException если ответ является сообщением об ошибке
         */
        fun BDict.toTrackerResponse(): TrackerResponse = fromBDict(this)


        /**
         * Вызывает TrackerResponse.fromBDict(this), если BElement BDict
         * Иначе возвращает null
         * @return TrackerResponse если не было ошибки
         * Если BDict то могут быть выброшены исключения
         * @throws IllegalArgumentException если не было найдено обязательное поле
         * @throws FailureResponseException если ответ является сообщением об ошибке
         */
        fun BElement.toTrackerResponseOrNull(): TrackerResponse? =
            (this as? BDict)?.let { fromBDict(it) }
    }

    private fun String.fromDottedQuad(): Int {
        val parts = split(".")
        if (parts.size != 4) throw IllegalArgumentException("IP $this is not valid")
        var ip = 0
        parts.forEach {
            val byte = it.toIntOrNull() ?: throw IllegalArgumentException("IP $this is not valid")
            if (byte > 255) throw IllegalArgumentException("IP $this is not valid, byte $byte > 255")
            ip = (ip shl 8) or byte
        }

        return ip
    }

    /**
     * Преобразует класс к BDict
     * @param dictModel - выбор модели Dict or Binary, по умолчанию Dict
     * @throws IllegalArgumentException если не удается преобразовать ip в Binary model
     */
    fun toBDict(dictModel: Boolean = true): BDict {
        val peersElement: BElement = when(dictModel) {
            true -> {
                BList(peers.map {
                    buildMap {
                        it.peerId?.let { peerId -> put(PEER_ID, peerId) }
                        put(IP, it.ip.toBString())
                        put(PORT, BInt(it.port.toLong()))
                    }
                }.map { BDict(it) })
            }

            else -> {
                val buffer = ByteBuffer.allocate(6 * peers.size).apply {
                    peers.forEach {
                        putInt(it.ip.fromDottedQuad())
                        putShort(it.port.toShort())
                    }
                    flip()
                }
                BString(buffer.array())
            }
        }

        return BDict(buildMap {
            put(INTERVAL, BInt(interval.inWholeSeconds))
            put(TRACKER_ID, trackerId)
            put(COMPLETE, BInt(complete.toLong()))
            put(INCOMPLETE, BInt(incomplete.toLong()))
            put(PEERS, peersElement)
            warningMessage?.let { put(WARNING_MESSAGE, it.toBString()) }
            minInterval?.let { put(MIN_INTERVAL, BInt(minInterval.inWholeSeconds)) }
        })
    }

    data class Peer (val peerId: BString?, val ip: String, val port: Int)
}
