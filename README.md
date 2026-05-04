# Distributed Task Queue (DTQ)

## 🏗 Project Overview
This project is a high-performance **Distributed Task Queue** built using **Spring Boot 3** and **Redis (Valkey)**. It is designed to handle asynchronous workloads across a distributed environment, ensuring complete decoupling between task production and execution.

## 🏗 System Architecture
The architecture consists of three main layers: the **Producer Layer**, the **Message Broker (Redis)**, and the **Worker Cluster**.

![High-Level Architecture](./Java%20Worker%20Cluster%20Task-2026-03-02-222132.png)

1. **Producers:** Java applications that create tasks and push them into the global queue via `LPUSH`.
2. **Redis Broker:** Acts as the centralized task store, managing `Pending`, `Processing`, and `Dead Letter` queues.
3. **Worker Cluster:** Independent Java instances that pull tasks, execute logic, and provide feedback (ACK).

---

## 📐 Task Lifecycle (Activity Diagram)
To ensure reliability, the system follows a strict state-transition logic for every task.

![Task Activity Diagram](./Java%20Worker%20Cluster%20Task%20Activity%20diagram-2026-03-02-222542.png)

#### Key Workflow Steps:
1. **Submission:** The producer serializes the task into JSON and pushes it to the `tasks:pending` list.
2. **Reliable Polling:** A worker uses the `RPOPLPUSH` atomic command to move the task to `tasks:processing` while fetching it.
3. **Execution:** The worker executes the task's business logic via `TaskProcessorService`.
4. **Acknowledgment (ACK):** Upon success, the task is permanently removed from the `tasks:processing`.
5. **Error Handling & Retries:** If execution fails, the system increments the retry counter. If it reaches the limit (3), it is moved to the **Dead Letter Queue**.

---

## 🚀 Getting Started

### 1. Prerequisites
*   **Java 21** or higher.
*   **Redis** (or **Valkey**) server running on `localhost:6379`.

### 2. Configuration
Ensure your `application.properties` has the correct Redis and Queue settings:
```properties
spring.data.redis.host=localhost
spring.data.redis.port=6379
queue.name.pending=tasks:pending
queue.name.processing=tasks:processing
```

### 3. Testing the System
Enqueue a Task:

```Bash
curl -X POST "http://localhost:8080/api/tasks/enqueue?type=HEAVY_TASK" \
     -H "Content-Type: application/json" \
     -d '{"description": "My first heavy task", "data": "12345"}'
```

Check Queue Status:

```Bash
curl -X GET "http://localhost:8080/api/tasks/status"
