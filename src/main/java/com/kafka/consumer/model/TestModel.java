package com.kafka.consumer.model;

import lombok.Data;

@Data
public class TestModel {

    private String name;
    private String messageBody;
    private String subject;
    private String fileName;
    private String filePath;
}
