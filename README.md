## 🛠 Core Principles & Architecture

This Distributed Task Queue is built on 5 fundamental pillars to ensure efficiency and reliability in a distributed environment:

1. **Decoupling:** Complete isolation between the **Producer** (sender) and the **Worker** (executor). The producer doesn't need to know which worker processes the task, allowing for seamless scaling of worker nodes.
2. **Message Broker (Redis):** Utilizing **Redis** as a shared "mailbox" for task storage. This provides a high-speed, centralized communication layer accessible by multiple machines.
3. **Persistence & Guarantees:** Implementing the **At-least-once delivery** principle. We ensure no tasks are lost during worker crashes or power outages through a robust Acknowledgment (ACK) mechanism.
4. **Idempotency & Concurrency:** Handling distributed race conditions. The system ensures that if a task is accidentally picked up twice, the execution logic remains safe and consistent.
5. **Load Balancing:** Automatically and fairly distributing tasks across all available workers to prevent bottlenecks and ensure optimal resource utilization.

---

## 📋 Technical Requirements

### 1. Functional Requirements
- **Task Submission API:** Ability to enqueue tasks with a unique ID, task type, and payload/parameters.
- **Dynamic Workers:** Capability to attach or detach worker nodes at runtime without modifying the core system.
- **Status Tracking:** Monitoring the lifecycle of a task (e.g., `Pending`, `Processing`, `Completed`, `Failed`).
- **Retry Mechanism:** Automatic rescheduling of failed tasks based on a configurable retry policy (Exponential Backoff).

### 2. Tech Stack
- **Language:** Java 17+ (utilizing Records and modern Concurrency utilities).
- **Data Store:** Redis (serving as the primary engine for queues and distributed locking).
- **Serialization:** Jackson (converting Java objects to/from JSON for cross-platform compatibility).
- **Driver:** Jedis or Lettuce (official Java clients for Redis).
- **Build Tool:** Maven or Gradle.

---

## 🏗 Redis Data Structure Strategy
The system follows the **Reliable Queue Pattern** using Redis Lists:
- `pending_tasks`: The main queue for tasks waiting to be processed.
- `processing_tasks`: A temporary list for tasks currently in execution (to ensure fault tolerance).
