```mermaid
sequenceDiagram
    participant User as User
    participant LoginController as LoginController
    participant UserSession as UserSession
    participant BooksManagementController as BooksManagementController
    participant BorrowRecordsService as BorrowRecordsService
    participant BorrowRecordDAO as BorrowRecordDAO
    participant Database as Database

    Note over User, Database: User Borrowing Process
    User->>LoginController: Enter username and password to login
    LoginController->>UserSession: Authenticate user and create session
    UserSession-->>LoginController: Return session information
    LoginController-->>User: Login success/failure response

    User->>BooksManagementController: Browse available books list
    BooksManagementController->>Database: Query available books
    Database-->>BooksManagementController: Return books list
    BooksManagementController-->>User: Display books list

    User->>BooksManagementController: Select book to borrow
    BooksManagementController->>BorrowRecordsService: Create borrowing record
    BorrowRecordsService->>BorrowRecordDAO: Insert borrowing record into database
    BorrowRecordDAO-->>BorrowRecordsService: Return insertion result
    BorrowRecordsService-->>BooksManagementController: Return borrowing result
    BooksManagementController-->>User: Display borrowing success/failure message

    Note over User, Database: Borrowing process completed