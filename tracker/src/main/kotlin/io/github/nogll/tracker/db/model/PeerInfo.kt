package io.github.nogll.tracker.db.model

data class PeerInfo(
    var peerId: String,
    var ip: String,
    var port: Int,
    var finished: Boolean
)  /*{
    constructor(): this("", "", 0, false)
}*/
