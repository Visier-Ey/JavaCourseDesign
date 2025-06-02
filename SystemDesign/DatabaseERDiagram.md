```mermaid
erDiagram
    USER ||--o{ BORROW_RECORD : "borrows"
    USER {
        string user_id PK "User ID"
        string username "Username"
        string password_hash "Password Hash"
        string role "Role"
        boolean frozen "Is Frozen"
    }
    
    BOOK ||--o{ BORROW_RECORD : "is borrowed by"
    BOOK {
        string book_id PK "Book ID"
        string title "Title"
        string author "Author"
        string isbn "ISBN"
        boolean available "Is Available"
    }
    
    BORROW_RECORD {
        string record_id PK "Record ID"
        date borrow_date "Borrow Date"
        date due_date "Due Date"
        date return_date "Return Date"
        string status "Status"
        string user_id FK "User ID"
        string book_id FK "Book ID"
    }