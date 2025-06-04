```mermaid
graph LR
    S[Library Management System] --> U[User Module]
    S --> A[Administrator Module]
    S --> B[Core Functionality Module]
    
    U --> U1[Registration/Login]
    U --> U2[Book Search]
    U --> U3[Personalized Recommendations]
    U --> U4[Borrowing Management]
    U --> U5[Overdue Notifications]
    
    A --> A1[Book CRUD Operations]
    A --> A2[Borrowing Record Auditing]
    A --> A3[Statistics and Analytics]
    A --> A4[User Management]
    
    B --> B1[Permission Control]
    B --> B2[Data Persistence]
    B --> B3[Notification Service]