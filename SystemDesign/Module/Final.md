```mermaid
graph LR
    
    U[User/Admin] --> UA[JWT]
    UA --> U1[Registration/Login]
    UA --> U3[Personalized Recommendations]
    UA --> U4[Borrowing Management]
    UA --> U5[Overdue Notifications]
    
    UA --> A1[Book CRUD Operations]
    UA --> A3[Statistics and Analytics]
    UA --> A4[User Management]