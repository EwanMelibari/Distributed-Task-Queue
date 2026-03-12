package com.e1.DTQ.Controller;

import java.util.Map;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.e1.DTQ.Service.TaskProducerService;
import com.e1.DTQ.model.TaskType;

import lombok.RequiredArgsConstructor;

@RestController
@RequestMapping("/api/tasks")
@RequiredArgsConstructor
public class TaskController {

    private final TaskProducerService taskProducerService;

    @PostMapping("/enqueue")
    public ResponseEntity<String> enqueueTask(@RequestParam TaskType type, @RequestBody Map<String,Object> payload){

        taskProducerService.sendTask(type, payload);

        return ResponseEntity.ok("Task of type " + type + " enqueued successfully.");

    }

    @GetMapping("/status")
    public ResponseEntity<Map<String, Object>> getStatus(){
       return ResponseEntity.ok(Map.of("pendingTasks", taskProducerService.getPendingTaskCount()));
    }
    
}
