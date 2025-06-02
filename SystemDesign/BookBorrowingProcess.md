```mermaid
stateDiagram-v2
    [*] --> Available
    Available --> Borrowed: User borrows book
    Borrowed --> Available: User returns book
    Borrowed --> Overdue: Not returned by due date
    Overdue --> Available: Book returned
    Overdue --> [*]: Admin marks as lost