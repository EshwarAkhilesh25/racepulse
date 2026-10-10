package com.racepulse.config;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.data.redis.LettuceClientConfigurationBuilderCustomizer;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.data.redis.connection.RedisConnectionFactory;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.test.context.TestPropertySource;

import static org.junit.jupiter.api.Assertions.assertNotNull;

@SpringBootTest
@TestPropertySource(properties = {
    "spring.data.redis.host=localhost",
    "spring.data.redis.port=6379",
    "spring.data.redis.ssl.enabled=false",
    "spring.data.redis.timeout=5000ms"
})
class RedisConfigTest {

  @Autowired
  private LettuceClientConfigurationBuilderCustomizer lettuceCustomizer;

  @Autowired
  private RedisTemplate<String, Object> redisTemplate;

  @Autowired(required = false)
  private RedisConnectionFactory connectionFactory;

  @Test
  void testLettuceCustomizerBeanIsCreated() {
    assertNotNull(lettuceCustomizer, "LettuceClientConfigurationBuilderCustomizer should be instantiated");
  }

  @Test
  void testRedisTemplateBeanIsCreated() {
    assertNotNull(redisTemplate, "RedisTemplate should be instantiated");
    assertNotNull(redisTemplate.getKeySerializer(), "Key serializer should be set");
    assertNotNull(redisTemplate.getValueSerializer(), "Value serializer should be set");
  }
}
