```mermaid
graph TD
    BE[Backend: CourseDesignServer] -->|HTTP/WebSocket| Javalin
    subgraph CourseDesignServer
        Javalin --> Service
        Service --> DAO
        DAO --> Sqlite
        Service --> Utils
    end
