package com.kafka.consumer.service;

import com.kafka.consumer.entity.TestEntity;
import com.kafka.consumer.repository.TestRepository;
import org.springframework.batch.item.Chunk;
import org.springframework.batch.item.ItemWriter;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.List;

@Component
public class ConsumerRepositoryWriter implements ItemWriter<List<TestEntity>> {

    @Autowired
    private TestRepository testRepository;

    @Override
    public void write(Chunk<? extends List<TestEntity>> chunk) throws Exception {

        for(var response: chunk) {
            testRepository.saveAll(response);
        }

    }
}
