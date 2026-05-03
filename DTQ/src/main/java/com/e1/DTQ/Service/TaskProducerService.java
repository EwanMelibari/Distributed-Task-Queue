package com.e1.DTQ.Service;

import java.util.Map;
import org.springframework.stereotype.Service;
// استيرادات الـ Logger الصحيحة
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import com.e1.DTQ.Repo.TaskRepository;
import com.e1.DTQ.model.TaskRequest;
import com.e1.DTQ.model.TaskType;

@Service
public class TaskProducerService {

    // Adding a logger for better error handling and debugging information
    private static final Logger log = LoggerFactory.getLogger(TaskProducerService.class);

    private final TaskRepository taskRepository;

    public TaskProducerService(TaskRepository taskRepository) {
        this.taskRepository = taskRepository;
    }

    public void sendTask(TaskType type, Map<String, Object> payload) {
        try {
            taskRepository.push(new TaskRequest(type, payload));
            log.info("Task of type {} successfully submitted", type);
        } catch (Exception e) {
            log.error("Critical: Could not connect to Redis to enqueue task", e);
            throw new RuntimeException("Queue Service Unavailable");
        }
    }

    public Long getPendingTaskCount() {
        try {
            return taskRepository.getPendingCount();
        } catch (Exception e) {
            log.error("Critical: Could not connect to Redis to get pending task count", e);
            throw new RuntimeException("Queue Service Unavailable");
        }
    }
}