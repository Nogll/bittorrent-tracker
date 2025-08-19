package io.github.nogll.tracker.model

import io.github.nogll.bencode.model.BString

data class TrackerRequest(
    val infoHash: BString,
    val peerId: BString,
    val port: Int,
    val uploaded: Long,
    val downloaded: Long,
    val left: Long,
    val compact: Boolean,
    val noPeerId: Boolean,
    val event: String?,
    val ip: String,
    val numWant: Int,
    val key: String?,
    val trackerId: String?
)
