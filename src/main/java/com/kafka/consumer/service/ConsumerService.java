package com.kafka.consumer.service;


import com.kafka.consumer.model.TestModel;
import com.kafka.consumer.util.ConsumerConstant;
import com.kafka.consumer.util.Util;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
@Slf4j
public class ConsumerService {
    @Autowired
    private Util util;

    @Autowired
    private RestTemplate restTemplate;

    @KafkaListener(topics = "#{'${consumer.topic}'}", groupId = ConsumerConstant.GROUP_ID_CONFIG,
            containerFactory = "consumerKafkaListenerFactory")
    public void consumeJson(final String message) {
        log.info("Received message: {}", message);
        final boolean apiCall = makeApiCall(message);
        if (apiCall) {
            log.info("Received success from api");
        } else {
            log.info("Received failure from api");
        }

    }

    public boolean makeApiCall(final String message) {
        ResponseEntity<TestModel> response = restTemplate.getForEntity("http://localhost:7081/test/{message}", TestModel.class, message);
        int i = 0;
        if(response.getStatusCode().is2xxSuccessful()) {
            log.info("API call successful: {} , total calls {}", response.getBody(), i++);
            return true;
        }
        return false;
    }
}
