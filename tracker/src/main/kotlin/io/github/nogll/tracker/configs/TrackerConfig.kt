package io.github.nogll.tracker.configs

import com.fasterxml.jackson.databind.Module
import com.fasterxml.jackson.databind.ObjectMapper
import com.fasterxml.jackson.module.kotlin.kotlinModule
import com.fasterxml.jackson.module.kotlin.registerKotlinModule
import io.github.nogll.bencode.Encoder
import io.github.nogll.tracker.db.model.PeerInfo
import org.springframework.boot.autoconfigure.jackson.Jackson2ObjectMapperBuilderCustomizer
import org.springframework.context.annotation.Bean
import org.springframework.context.annotation.Configuration
import org.springframework.data.redis.connection.RedisConnectionFactory
import org.springframework.data.redis.connection.RedisStandaloneConfiguration
import org.springframework.data.redis.connection.jedis.JedisConnectionFactory
import org.springframework.data.redis.core.RedisTemplate
import org.springframework.data.redis.core.StringRedisTemplate
import org.springframework.data.redis.serializer.Jackson2JsonRedisSerializer
import org.springframework.data.redis.serializer.StringRedisSerializer


@Configuration
open class TrackerConfig {
    @Bean
    open fun encoder() = Encoder()

    /**
     * Jedis
     */
    @Bean
    open fun jedisConnectionFactory(): RedisConnectionFactory {
        return JedisConnectionFactory(RedisStandaloneConfiguration("localhost", 6379))
    }

    @Bean
    open fun redisPeersInfo(connectionFactory: RedisConnectionFactory): RedisTemplate<String, PeerInfo> {
        val template = RedisTemplate<String, PeerInfo>()
        template.connectionFactory = connectionFactory

        val objectMapper = ObjectMapper().registerKotlinModule()
        val keySerializer = StringRedisSerializer()
        val valueSerializer = Jackson2JsonRedisSerializer(objectMapper, PeerInfo::class.java)

        template.keySerializer = keySerializer
        template.hashKeySerializer = keySerializer
        template.valueSerializer = valueSerializer
        template.hashValueSerializer = valueSerializer

        return template
    }

    @Bean
    open fun redisTorrentPeers(connectionFactory: RedisConnectionFactory): StringRedisTemplate {
        val template = StringRedisTemplate()
        template.connectionFactory = connectionFactory
        return template
    }
}