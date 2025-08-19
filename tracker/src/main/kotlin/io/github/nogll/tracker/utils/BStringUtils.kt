package io.github.nogll.tracker.utils

import io.github.nogll.bencode.model.BString
import kotlin.io.encoding.Base64
import kotlin.io.encoding.ExperimentalEncodingApi

@OptIn(ExperimentalEncodingApi::class)
fun BString.toBase64() = Base64.Default.encode(this.asBytes())

@OptIn(ExperimentalEncodingApi::class)
fun String.fromBase64() = BString(Base64.Default.decode(this))