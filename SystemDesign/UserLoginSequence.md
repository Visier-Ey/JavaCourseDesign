```mermaid
sequenceDiagram
    participant Client
    participant Server
    participant Database
    
    Client->>Server: Login Request (username, password)
    Server->>Database: Verify Credentials
    alt Valid Credentials
        Database-->>Server: User Data
        Server-->>Client: Success + User Role
    else Invalid Credentials
        Database-->>Server: Error
        Server-->>Client: Failure Message
    end