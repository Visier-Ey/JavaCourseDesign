```mermaid
flowchart TD
    A[JavaFX Client] -->|Socket| B[Server]
    B --> C[Thread Pool]
    C --> D[Request Handler]
    D --> E[Database]
    D --> F[Recommendation Engine]