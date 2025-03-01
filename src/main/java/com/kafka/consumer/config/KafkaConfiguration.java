package com.kafka.consumer.config;

import java.util.HashMap;
import java.util.List;
import java.util.Map;

import com.kafka.consumer.util.ConsumerConstant;
import org.apache.kafka.clients.consumer.ConsumerConfig;
import org.apache.kafka.common.serialization.StringDeserializer;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.converter.json.Jackson2ObjectMapperBuilder;
import org.springframework.kafka.annotation.EnableKafka;
import org.springframework.kafka.config.ConcurrentKafkaListenerContainerFactory;
import org.springframework.kafka.core.ConsumerFactory;
import org.springframework.kafka.core.DefaultKafkaConsumerFactory;
import org.springframework.kafka.listener.CommonLoggingErrorHandler;
import org.springframework.kafka.support.serializer.ErrorHandlingDeserializer;
import org.springframework.kafka.support.serializer.JsonDeserializer;

import com.fasterxml.jackson.annotation.JsonAutoDetect;
import com.fasterxml.jackson.annotation.PropertyAccessor;
import com.fasterxml.jackson.databind.DeserializationFeature;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.springframework.kafka.support.serializer.JsonSerializer;

@EnableKafka
@Configuration
public class KafkaConfiguration {

    @Value("${spring.kafka.url}")
    private String kafkaUrl;

    @Value("${spring.kafka.port}")
    private String kafkaPort;

    @Bean
    ConsumerFactory<String, List<String>> kafkaConsumerFactory() {

        final Map<String, Object> config = new HashMap<>();
        config.put(ConsumerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaUrl + ":" + kafkaPort);
        config.put(ConsumerConfig.GROUP_ID_CONFIG, ConsumerConstant.GROUP_ID_CONFIG);
        config.put(ConsumerConfig.KEY_DESERIALIZER_CLASS_CONFIG, StringDeserializer.class);
        config.put(ConsumerConfig.VALUE_DESERIALIZER_CLASS_CONFIG, JsonDeserializer.class);
        config.put(ConsumerConfig.SESSION_TIMEOUT_MS_CONFIG, "6000");
        config.put(JsonDeserializer.TRUSTED_PACKAGES, "*");

        final ErrorHandlingDeserializer<String> headerErrorHandlingDeserializer = new ErrorHandlingDeserializer<>(
                new StringDeserializer());
        final ErrorHandlingDeserializer<List<String>> errorHandlingDeserializer = new ErrorHandlingDeserializer<>(
                new JsonDeserializer());
        return new DefaultKafkaConsumerFactory<>(config, headerErrorHandlingDeserializer, errorHandlingDeserializer);
    }

    @Bean
    ConcurrentKafkaListenerContainerFactory<String, List<String>> consumerKafkaListenerFactory() {
        final ConcurrentKafkaListenerContainerFactory<String, List<String>> factory =
                new ConcurrentKafkaListenerContainerFactory<>();
        factory.setConsumerFactory(kafkaConsumerFactory());
        factory.setCommonErrorHandler(new CommonLoggingErrorHandler());
        return factory;
    }

    private ObjectMapper objectMapper() {
        return Jackson2ObjectMapperBuilder.json().visibility(PropertyAccessor.FIELD, JsonAutoDetect.Visibility.ANY)
                .featuresToDisable(DeserializationFeature.FAIL_ON_UNKNOWN_PROPERTIES).build();
    }
}
