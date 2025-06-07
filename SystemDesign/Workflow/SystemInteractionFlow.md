<br /><br />

```mermaid
sequenceDiagram
    participant F as Frontend (CourseDesign)
    participant B as Backend (CourseDesignServer)
    
    F->>B: HTTP API Requests (ApiClient → Javalin)
    B->>F: JSON Responses
    F->>B: WebSocket Connections (Socket)
    B->>F: Real-time Updates
    loop Database Operations
        B->>B: DAO → Sqlite
    end