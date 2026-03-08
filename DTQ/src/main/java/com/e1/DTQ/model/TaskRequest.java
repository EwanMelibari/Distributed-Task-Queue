package com.e1.DTQ.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

public record TaskRequest (

    String taskId,
    TaskType taskType,
    Map<String, Object> payload, // 2. Use Map<String, Object> for flexible payload structure
    int retryCount,
    long creatingTimestamp

) implements Serializable { // 1. Implement Serializable for potential future use in distributed systems

 public TaskRequest( String taskId, TaskType taskType, Map<String, Object> payload) {
        this(taskId, taskType, payload, 0, Instant.now().toEpochMilli());
    }

 }

