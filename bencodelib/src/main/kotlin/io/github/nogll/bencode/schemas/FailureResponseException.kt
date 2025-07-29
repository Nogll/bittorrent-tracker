package io.github.nogll.bencode.schemas

class FailureResponseException(val failureReason: String) : RuntimeException()