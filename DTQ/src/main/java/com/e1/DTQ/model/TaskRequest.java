package com.e1.DTQ.model;

import java.io.Serializable;
import java.time.Instant;
import java.util.Map;

public record TaskRequest (

    String taskId,
    String taskType,
    Map<String, Object> payload,
    int retryCount,
    long creatingTimestamp

) implements Serializable {

 public TaskRequest( String taskId, String taskType, Map<String, Object> payload) {
        this(taskId, taskType, payload, 0, Instant.now().toEpochMilli());
    }

 }

