package com.kafka.consumer.service;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.core.type.TypeReference;
import com.fasterxml.jackson.databind.ObjectMapper;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.batch.core.configuration.annotation.JobScope;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.core.listener.StepExecutionListenerSupport;
import org.springframework.batch.core.partition.support.StepExecutionAggregator;
import org.springframework.batch.core.repository.dao.StepExecutionDao;
import org.springframework.batch.item.ItemReader;
import org.springframework.batch.item.NonTransientResourceException;
import org.springframework.batch.item.ParseException;
import org.springframework.batch.item.UnexpectedInputException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import org.springframework.stereotype.Service;

import java.util.Arrays;
import java.util.Collection;
import java.util.Iterator;
import java.util.List;

@Service
@Setter
@Slf4j
@JobScope
public class StringReader implements ItemReader<List<String>>, StepExecutionListener {

    private List<String> items;
    private Iterator<String> iterator;
    @Value("#{jobParameters['itemsListJson']}")
    private String itemsListJson;


    @Autowired
    private ObjectMapper objectMapper;

//    public StringReader() {
//
//        // Convert the comma-separated string back to a List<String>
//        List<String> items = Arrays.asList(itemsListJson.split(","));
//        this.iterator = items.iterator();
//    }



    @Override
    public List<String> read() throws Exception, UnexpectedInputException, ParseException, NonTransientResourceException {
        log.info("itemsListJson inside processor: {}", itemsListJson);
        List<String> myList = Arrays.asList(itemsListJson.split(","));
        log.info("myList: {}", myList);
        return myList;
    }

}
