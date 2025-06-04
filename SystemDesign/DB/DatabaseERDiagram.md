```mermaid
erDiagram
    %% ================ TABLES ================
    users {
        TEXT user_id PK
        TEXT username
        TEXT password_hash
        TEXT role
        BOOLEAN frozen
    }

    books {
        TEXT book_id PK
        TEXT title
        TEXT author
        TEXT isbn UK
        BOOLEAN available
    }

    borrow_records {
        TEXT record_id PK
        TEXT borrow_date
        TEXT due_date
        TEXT return_date
        TEXT status "BORROWED|RETURNED|OVERDUE"
        TEXT user_id FK
        TEXT book_id FK
    }

    %% ================ RELATIONSHIPS ================
    users ||--o{ borrow_records : "1:N"
    books ||--o{ borrow_records : "1:N"