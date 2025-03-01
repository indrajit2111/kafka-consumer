package com.kafka.consumer.service;


import com.fasterxml.jackson.databind.ObjectMapper;
import com.kafka.consumer.entity.TestEntity;
import com.kafka.consumer.model.TestModel;
import com.kafka.consumer.repository.TestRepository;
import com.kafka.consumer.util.ConsumerConstant;
import com.kafka.consumer.util.Util;
import lombok.SneakyThrows;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.Job;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.JobParametersBuilder;
import org.springframework.batch.core.JobParametersInvalidException;
import org.springframework.batch.core.launch.JobLauncher;
import org.springframework.batch.core.repository.JobExecutionAlreadyRunningException;
import org.springframework.batch.core.repository.JobInstanceAlreadyCompleteException;
import org.springframework.batch.core.repository.JobRestartException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;
import org.springframework.util.CollectionUtils;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Service
@Slf4j
public class ConsumerService {
    @Autowired
    private Util util;

    @Autowired
    private RestTemplate restTemplate;

    @Autowired
    private ObjectMapper objectMapper;

    @Autowired
    private TestRepository testRepository;

    @Autowired
    private JobLauncher jobLauncher;

    @Autowired
    private Job job;

//    @KafkaListener(topics = "#{'${consumer.topic}'}", groupId = ConsumerConstant.GROUP_ID_CONFIG,
//            containerFactory = "consumerKafkaListenerFactory")
//    @SneakyThrows
//    public void consumeJson(final List<String> message) {
//        log.info("Received message: {}", message);
//        String itemsJson = objectMapper.writeValueAsString(message);
//        JobParameters jobParameters = new JobParametersBuilder()
//                .addString("itemsListJson", itemsJson)
//                .addLong("startAt", System.currentTimeMillis()).toJobParameters();
//        try {
//            jobLauncher.run(job, jobParameters);
//        } catch (JobExecutionAlreadyRunningException | JobRestartException | JobInstanceAlreadyCompleteException |
//                 JobParametersInvalidException e) {
//            e.printStackTrace();
//        }
//
//    }

    @KafkaListener(topics = "#{'${consumer.topic}'}", groupId = ConsumerConstant.GROUP_ID_CONFIG,
            containerFactory = "consumerKafkaListenerFactory")
    public void consumeJson(final List<String> message) {
        log.info("Received message: {}", message);
        final boolean apiCall = makeApiCall(message);
        if (apiCall) {
            log.info("Received success from api");
        } else {
            log.info("Received failure from api");
        }

    }

    public boolean makeApiCall(final List<String> message) {
        if (!CollectionUtils.isEmpty(message)) {
            message.parallelStream().forEach((data) -> {
                ResponseEntity<TestModel> response = restTemplate.getForEntity("http://localhost:7081/test/{message}", TestModel.class, data);
                int i = 0;
                log.info("API call successful: {} , total calls {}", response.getBody(), i + 1);
                if (response.getStatusCode().is2xxSuccessful()) {
                    TestEntity testEntity = objectMapper.convertValue(response.getBody(), TestEntity.class);
                    log.info("Test Data: {}", testEntity);
                    dataSave(testEntity);
                }
            });
            return true;
        }
        return false;
    }

    public synchronized void dataSave(TestEntity testEntity) {
        testRepository.save(testEntity);
    }
}
