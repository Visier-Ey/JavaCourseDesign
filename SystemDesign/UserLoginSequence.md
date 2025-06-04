```mermaid
sequenceDiagram
    participant Client
    participant JavalinServer
    participant UserService
    participant Database
    
    Client->>JavalinServer: POST /login
    JavalinServer->>UserService: authenticate()
    UserService->>Database: SELECT user
    Database-->>UserService: user data
    UserService-->>JavalinServer: JWT token
    JavalinServer-->>Client: return token