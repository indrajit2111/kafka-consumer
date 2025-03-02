package com.kafka.consumer.service;

import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.consumer.entity.TestEntity;
import com.kafka.consumer.model.TestModel;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Component
@Slf4j
@JobScope
public class ConsumerProcessor implements ItemProcessor<List<String>, List<TestEntity>> {

    @Value("#{jobParameters['itemsListJson']}")
    private String itemsListJson;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Override
    public List<TestEntity> process(List<String> item) throws Exception {

        List<TestEntity> finalList = new ArrayList<>();
        item.parallelStream().forEach((data) -> {
            ResponseEntity<TestModel> response = restTemplate.getForEntity("http://localhost:7081/test/{message}", TestModel.class, item);
            int i = 0;
            log.info("API call successful: {} , total calls {}", response.getBody(), i + 1);
            if (response.getStatusCode().is2xxSuccessful()) {
                TestEntity testEntity = objectMapper.convertValue(response.getBody(), TestEntity.class);
                log.info("Test Data: {}", testEntity);
                finalList.add(testEntity);
            }
        });
        return finalList;
    }
}
