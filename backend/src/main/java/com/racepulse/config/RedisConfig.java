package com.racepulse.config;

import io.lettuce.core.ClientOptions;
import io.lettuce.core.SocketOptions;
import io.lettuce.core.TimeoutOptions;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.data.redis.serializer.GenericJackson2JsonRedisSerializer;
import org.springframework.data.redis.serializer.StringRedisSerializer;

import java.time.Duration;

@Configuration
public class RedisConfig {

  private static final Logger log = LoggerFactory.getLogger(RedisConfig.class);

  @Value("${spring.data.redis.host:localhost}")
  private String redisHost;

  @Value("${spring.data.redis.port:6379}")
  private int redisPort;

  @Value("${spring.data.redis.ssl.enabled:false}")
  private boolean sslEnabled;

  @Value("${spring.data.redis.timeout:10000ms}")
  private Duration commandTimeout;

  @Bean
  public LettuceClientConfigurationBuilderCustomizer lettuceClientCustomizer() {
    log.info("[Redis Diagnostic] Initializing Lettuce client: host={}, port={}, sslEnabled={}, commandTimeout={}ms",
        redisHost, redisPort, sslEnabled, commandTimeout.toMillis());

    return builder -> {
      if (sslEnabled) {
        builder.useSsl();
      }
      builder.commandTimeout(commandTimeout);
      builder.clientOptions(ClientOptions.builder()
          .socketOptions(SocketOptions.builder()
              .connectTimeout(Duration.ofSeconds(10))
              .keepAlive(true)
              .build())
          .timeoutOptions(TimeoutOptions.enabled())
          .build());
    };
  }

  @Bean
  public RedisTemplate<String, Object> redisTemplate(RedisConnectionFactory connectionFactory) {
    RedisTemplate<String, Object> template = new RedisTemplate<>();
    template.setConnectionFactory(connectionFactory);
    template.setKeySerializer(new StringRedisSerializer());
    template.setHashKeySerializer(new StringRedisSerializer());
    template.setValueSerializer(new GenericJackson2JsonRedisSerializer());
    template.setHashValueSerializer(new GenericJackson2JsonRedisSerializer());
    template.afterPropertiesSet();
    return template;
  }
}
