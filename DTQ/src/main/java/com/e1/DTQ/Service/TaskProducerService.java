package com.e1.DTQ.Service;

import java.util.Map;

import org.springframework.stereotype.Service;

import com.e1.DTQ.Repo.TaskRepository;
import com.e1.DTQ.model.TaskRequest;
import com.e1.DTQ.model.TaskType;

import lombok.extern.slf4j.Slf4j;

@Service
@Slf4j
public class TaskProducerService {

   private final TaskRepository taskRepository;

   // Constructor injection of TaskRepository
    public TaskProducerService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    // Method to send a task to the queue and push it to the pending queue using the TaskRepository
    public void sendTask(TaskType type, Map<String, Object> payload) {
        try {
            // Create a new TaskRequest and push it to the pending queue using the TaskRepository
            taskRepository.push(new TaskRequest(type, payload));
            log.info("Task of type {} successfully submitted", type);
        } catch (Exception e) {
            // if(redis connection fails) log the error and throw a runtime exception to indicate the queue service is unavailable
            log.error("Critical: Could not connect to Redis to enqueue task", e);
            throw new RuntimeException("Queue Service Unavailable");
        }
    }

}
