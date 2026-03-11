package com.e1.DTQ.Service;

import org.springframework.stereotype.Service;

import com.e1.DTQ.model.TaskRequest;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TaskProcessorService { // a service class responsible for processing tasks, with methods to handle different types of tasks and a main method to route tasks based on their type

    public void heavyProcess(String taskId) {
        log.info("Processing heavy task with ID: {}", taskId);
        try {
            // Simulate heavy processing with a sleep
            Thread.sleep(5000); // Simulate a 5-second processing time
            log.info("Completed processing task with ID: {}", taskId);
        } catch (InterruptedException e) {
            log.error("Task processing interrupted for task ID: {}", taskId, e);
            Thread.currentThread().interrupt(); // Restore the interrupted status
        }
    }

    public void lightProcess(String taskId) {
        log.info("Processing task with ID: {}", taskId);
        try {
            // Simulate light processing with a shorter sleep
            Thread.sleep(1000); // Simulate a 1-second processing time
            log.info("Completed processing task with ID: {}", taskId);
        } catch (InterruptedException e) {
            log.error("Task processing interrupted for task ID: {}", taskId, e);
            Thread.currentThread().interrupt(); // Restore the interrupted status
        }
    }
    
    public void processTask(TaskRequest taskRequest) { // a method to process tasks based on their type, using a switch statement to determine the appropriate processing method
        switch (taskRequest.taskType()) {
            case HEAVY_TASK -> heavyProcess(taskRequest.taskId());
            case LIGHT_TASK -> lightProcess(taskRequest.taskId());
            default -> log.warn("Unknown task type for task ID: {}", taskRequest.taskId());
        }

    }
    
}
