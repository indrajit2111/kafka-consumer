package com.kafka.consumer.mockcontroller;

import com.kafka.consumer.model.TestModel;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class TestController {

    @GetMapping("/test/{message}")
    public ResponseEntity<TestModel> testApi(@PathVariable("message") final String message) {
        TestModel testModel = new TestModel();
        testModel.setName("Indrajit");
        testModel.setSubject("Testing Kafka");
        testModel.setMessageBody("Testing Kafka Consumer");
        testModel.setFileName("abc.jpg");
        testModel.setFilePath("/downloads");
        return new ResponseEntity<>(testModel, HttpStatus.OK);
    }
}
