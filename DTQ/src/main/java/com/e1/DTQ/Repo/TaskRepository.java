package com.e1.DTQ.Repo;


import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.redis.core.RedisTemplate;
import org.springframework.stereotype.Repository;

import com.e1.DTQ.model.TaskRequest;

@Repository
public class TaskRepository {

    private final RedisTemplate<String, Object> redisTemplate;

    @Value("${queue.name.pending}")
    private String pendingQueue;

    @Value("${queue.name.processing}")
    private String processingQueue;

    public TaskRepository(RedisTemplate<String, Object> redisTemplate) {
        this.redisTemplate = redisTemplate;
    }

    // adding a task to the pending queue
    public void push(TaskRequest task) {
        redisTemplate.opsForList().leftPush(pendingQueue, task);
    }

    // moving a task from the pending queue to the processing queue (atomic operation)
    public TaskRequest popAndPrepare() {
        return (TaskRequest) redisTemplate.opsForList()
                .rightPopAndLeftPush(pendingQueue, processingQueue);
    }

    // removing a task from the processing queue after successful processing
    public void removeFromProcessing(TaskRequest task) {
        redisTemplate.opsForList().remove(processingQueue, 1, task);
    }

    // in case of failure, move the task back to the pending queue for retry
    public void returnToPending(TaskRequest task) {
        redisTemplate.opsForList().remove(processingQueue, 1, task);
        redisTemplate.opsForList().leftPush(pendingQueue, task);
    }

    public Long getPendingCount(){
        return redisTemplate.opsForList().size(pendingQueue);
    }

    
}