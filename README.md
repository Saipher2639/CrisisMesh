# CrisisMesh

## Real-Time Emergency Communication & Dynamic Routing System

CrisisMesh is a real-time emergency communication system designed to deliver high-priority messages through a dynamically changing network of nodes.

The system uses **Spring Boot, WebSockets, MongoDB, and dynamic routing** to ensure that emergency messages can be routed through currently active nodes while acknowledging successful delivery.

## Key Features

* Real-time communication using WebSockets
* Dynamic node activation/deactivation
* Priority-based emergency message routing
* Dynamic route calculation between nodes
* Automatic message forwarding
* End-to-end acknowledgement mechanism
* REST APIs for node management
* MongoDB for persistent data storage
* Concurrent handling of connected WebSocket nodes

## Tech Stack

* **Backend:** Java, Spring Boot
* **Database:** MongoDB
* **Communication:** WebSocket
* **API:** REST API
* **Build Tool:** Maven
* **Architecture:** Service-based backend architecture

## System Architecture

```text
                ┌───────────────┐
                │   REST Client │
                └───────┬───────┘
                        │
                        ▼
              ┌──────────────────┐
              │  Node Controller │
              └────────┬─────────┘
                       │
                       ▼
              ┌──────────────────┐
              │  Routing Service │
              └────────┬─────────┘
                       │
                       ▼
             ┌────────────────────┐
             │ Active Node Network│
             └─────────┬──────────┘
                       │
                       ▼
                 WebSocket Layer
                       │
          ┌────────────┼────────────┐
          ▼            ▼            ▼
       Node A        Node B        Node C
          │            │            │
          └────────────┼────────────┘
                       ▼
                    Node D
```

## How It Works

1. Nodes are registered in the system through REST APIs.
2. Each node can be marked as active or inactive.
3. Connected nodes establish WebSocket connections.
4. When an emergency message is generated, the system calculates a route using currently active nodes.
5. The message is forwarded through the calculated route.
6. The destination node sends an acknowledgement.
7. The acknowledgement travels back through the route to confirm successful delivery.

### Example

For an active network:

```text
A → B → D
```

An emergency message generated at **Node A** can be routed to **Node D** through Node B.

If Node B becomes inactive:

```text
A → B → D    ❌
```

The routing system recalculates the available path using active nodes.

## REST APIs

### Create Node

```http
POST /api/nodes
```

Creates a new network node.

### Get All Nodes

```http
GET /api/nodes
```

Returns all registered nodes.

### Update Node Status

```http
PUT /api/nodes/{id}/status?active=true
```

Activates or deactivates a node.

## WebSocket

WebSocket endpoint:

```text
ws://localhost:8080/ws?node=A
```

Each connected node maintains a WebSocket session with the server.

Example nodes:

```text
A
B
C
D
```

## Routing Example

Suppose the network contains:

```text
A → B → D
A → C → D
```

For a priority emergency message, the routing service determines an available route based on the current node state and routing priority.

Example:

```text
Message:
Emergency message - Priority 1

Route:
A → B → D

Acknowledgement:
D → B → A
```

## Project Structure

```text
crisismesh
│
├── controller
│   └── NodeController
│
├── service
│   └── RoutingService
│
├── websocket
│   └── WebSocket Handler
│
├── model
│   └── Node
│
├── repository
│   └── Node Repository
│
└── CrisisMeshApplication
```

## Database

CrisisMesh uses **MongoDB** to store node information and system data.

Example node:

```json
{
  "name": "A",
  "active": true
}
```

## Running the Project

### Prerequisites

* Java 21+
* Maven
* MongoDB
* IntelliJ IDEA or another Java IDE

### Start MongoDB

Make sure MongoDB is running on:

```text
localhost:27017
```

### Run the Application

Using Maven:

```bash
mvn spring-boot:run
```

The application runs on:

```text
http://localhost:8080
```

## Future Improvements

* Authentication and authorization
* Multi-region disaster network simulation
* Message persistence and replay
* Delivery status dashboard
* Route optimization based on latency and node reliability
* Fault-tolerance and automatic node recovery
* React-based monitoring dashboard

## Author

Sai Swaroop Moharana
