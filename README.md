## This project is under construction
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
- **Language:** Java 21+ (utilizing Records and modern Concurrency utilities).
- **Data Store:** Redis (serving as the primary engine for queues and distributed locking).
- **Serialization:** Jackson (converting Java objects to/from JSON for cross-platform compatibility).
- **Driver:** Jedis or Lettuce (official Java clients for Redis).
- **Build Tool:** Maven.

---

## 🏗 Redis Data Structure Strategy
The system follows the **Reliable Queue Pattern** using Redis Lists:
- `pending_tasks`: The main queue for tasks waiting to be processed.
- `processing_tasks`: A temporary list for tasks currently in execution (to ensure fault tolerance).
## 📐 System Design

The system is designed to handle asynchronous workloads across a distributed environment, ensuring that task production and consumption are entirely decoupled.

### 1. High-Level Architecture
The architecture consists of three main layers: the **Producer Layer** (Java Applications), the **Message Broker** (Redis), and the **Worker Cluster**. This setup allows for horizontal scaling by simply adding more worker nodes to the cluster.

![High-Level Architecture](./Java%20Worker%20Cluster%20Task-2026-03-02-222132.png)

* **Producers:** Java applications that create tasks and push them into the global queue.
* **Redis Broker:** Acts as the centralized task store, managing `Pending`, `Processing`, and `Dead Letter` queues.
* **Worker Cluster:** A set of independent Java instances that pull tasks, execute logic, and provide feedback (ACK).

---

### 2. Task Lifecycle (Activity Diagram)
To ensure reliability, the system follows a strict state-transition logic for every task. This prevents task loss even in the event of a worker crash (Fault Tolerance).

![Task Activity Diagram](./Java%20Worker%20Cluster%20Task%20Activity%20diagram-2026-03-02-222542.png)

#### Key Workflow Steps:
1.  **Submission:** The producer serializes the task into JSON and pushes it to the `pending_tasks` list.
2.  **Reliable Polling:** A worker uses the `RPOPLPUSH` atomic command to move the task to a `processing_tasks` list while fetching it.
3.  **Execution:** The worker executes the task's business logic.
4.  **Acknowledgment (ACK):** Upon success, the task is permanently removed from the `processing_tasks`.
5.  **Error Handling & Retries:** If execution fails, the system increments the retry counter and re-queues the task until it reaches the maximum limit, after which it is moved to the **Dead Letter Queue (DLQ)**.
