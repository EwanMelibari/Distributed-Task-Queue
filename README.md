# Distributed Task Queue (DTQ)

## 🏗 Project Overview
This project is a high-performance **Distributed Task Queue** built using **Spring Boot 3** and **Redis (Valkey)**. It is designed to handle asynchronous workloads across a distributed environment, ensuring complete decoupling between task production and execution.

## 🛠 Core Principles & Architecture

1. **Decoupling:** Complete isolation between the **Producer** (Spring MVC Controller) and the **Worker** (Scheduled Task Executor).
2. **Reliable Broker:** Utilizing **Redis** as a centralized, high-speed message broker.
3. **At-Least-Once Delivery:** Using the **Reliable Queue Pattern** to ensure no tasks are lost if a worker crashes during execution.
4. **Polymorphic Serialization:** Leveraging Jackson's `@class` type-handling to support multiple task types (Heavy/Light) in a single pipeline.

---

## 📋 Technical Stack
*   **Language:** Java 21 (Records, Modern Concurrency).
*   **Framework:** Spring Boot 3.3.0.
*   **Data Store:** Redis / Valkey.
*   **Serialization:** Jackson JSON with Default Typing.
*   **Scheduling:** Spring `@Scheduled` with dedicated `ThreadPoolTaskExecutor`.

---

## 🏗 Redis Data Structure Strategy
The system manages tasks through three primary lists for maximum reliability:

| List Key | Description | Redis Operation |
| :--- | :--- | :--- |
| `tasks:pending` | Tasks waiting to be processed. | `LPUSH` / `RPOPLPUSH` |
| `tasks:processing` | Tasks currently handled by a worker. | `RPOPLPUSH` |
| `tasks:dead_letter` | Failed tasks exceeding retry limits. | Manual Intervention |

---

## 📐 Task Lifecycle


1.  **Submission:** Producer serializes `TaskRequest` to JSON and pushes it to `tasks:pending`.
2.  **Atomic Polling:** Worker uses `RPOPLPUSH` to move the task to `tasks:processing` atomically.
3.  **Execution:** `TaskProcessorService` routes the task based on its type (`HEAVY_TASK` or `LIGHT_TASK`).
4.  **Acknowledgment (ACK):** On success, the task is removed from `tasks:processing`.
5.  **Retry Logic:** On failure, the retry count is incremented. If under the limit (3), it returns to `pending`. Otherwise, it moves to `dead_letter`.

---

## 🚀 Getting Started

### Prerequisites
*   Java 21 installed.
*   Redis or Valkey server running on `localhost:6379`.

### Installation
1.  **Clone the repository:**
    ```bash
    git clone [https://github.com/your-username/Distributed-Task-Queue.git](https://github.com/your-username/Distributed-Task-Queue.git)
    ```
2.  **Start the Application:**
    ```bash
    mvn spring-boot:run
