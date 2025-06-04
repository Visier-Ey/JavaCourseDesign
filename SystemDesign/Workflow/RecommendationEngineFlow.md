```mermaid
graph LR
    A[Input User ID] --> B{Has Borrow History?}
    B -->|Yes| C[Collaborative Filtering]
    B -->|No| D[Content-Based Filtering]
    C --> E[Recommend Similar Users' Books]
    D --> F[Recommend Popular Books]
    E --> G[Output Recommendations]
    F --> G