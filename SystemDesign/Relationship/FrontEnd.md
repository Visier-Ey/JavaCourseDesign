```mermaid
graph TD
    ApiClient -->|HTTP/WebSocket| FE[Frontend: CourseDesign]
    subgraph CourseDesign
        Service --> ApiClient
        Controller --> Service
        Service --> Utils
        Manager --> Controller
        Controller --> Entity
        Controller --> Session
    end
    
