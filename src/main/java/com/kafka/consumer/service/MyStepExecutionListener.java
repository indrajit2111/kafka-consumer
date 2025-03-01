package com.kafka.consumer.service;

import org.springframework.batch.core.ExitStatus;
import org.springframework.batch.core.JobParameters;
import org.springframework.batch.core.StepExecution;
import org.springframework.batch.core.StepExecutionListener;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.util.Arrays;
import java.util.List;

@Component
public class MyStepExecutionListener implements StepExecutionListener {

    @Autowired
    private StringReader reader;

    @Override
    public void beforeStep(StepExecution stepExecution) {
        JobParameters parameters = stepExecution.getJobExecution()
                .getJobParameters();
        // Retrieve JobParameters (e.g., itemsList) from the StepExecution
        String itemsString = stepExecution.getJobExecution().getJobParameters().getString("itemsList");

        // Convert the comma-separated string back to a List<String>
        List<String> items = Arrays.asList(itemsString.split(","));

        // Pass the List<String> to the ItemReader
        reader.setItems(items);
    }

    @Override
    public ExitStatus afterStep(StepExecution stepExecution) {
        // Perform any post-step actions here (optional)
        return ExitStatus.COMPLETED;
    }

}
