package com.e1.DTQ.Service;

import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

import com.e1.DTQ.Repo.TaskRepository;
import com.e1.DTQ.model.TaskRequest;

import lombok.extern.slf4j.Slf4j;

@Component
@Slf4j
public class TaskWorker {

    private final TaskRepository taskRepository;
    private final TaskProcessorService taskProcessorService;

    public TaskWorker(TaskRepository taskRepository, TaskProcessorService taskProcessorService) {
        this.taskRepository = taskRepository;
        this.taskProcessorService = taskProcessorService;
    }

    @Scheduled(fixedDelay= 1000)
    public void fetchAndProcess(){

        TaskRequest task = taskRepository.popAndPrepare();

        if (task == null){
            log.info("No tasks to process at the moment.");
            return;
        }

        log.info("Fetched task with ID: {} and type: {}", task.taskId(), task.taskType());

        try{
            taskProcessorService.processTask(task);
            taskRepository.removeFromProcessing(task);
            log.info("Marked task with ID: {} as completed.", task.taskId());
        } catch (Exception e){
            log.error("Error processing task with ID: {}", task.taskId(), e);
            // Handle retry logic if failed terminates with an exception
            handleRetry(task);
        }
    }

    private void handleRetry(TaskRequest task) {
        if (task.retryCount() < 3) {
            log.info("Retrying task: {} [Attempt {}/3]", task.taskId(), task.retryCount() + 1);
            
            // create a new TaskRequest with incremented retry count
            TaskRequest retryTask = new TaskRequest(
                task.taskId(),
                task.taskType(),
                task.payload(),
                task.retryCount() + 1,
                task.creatingTimestamp()
            );
            
            // add back to pending queue for retry
            taskRepository.returnToPending(retryTask);
        } else {
            log.error("Task {} failed after maximum retries. Moving to graveyard (manual check needed).", task.taskId());
            // remove from processing and log for manual intervention
            taskRepository.removeFromProcessing(task); 
        }
    }

    
}
