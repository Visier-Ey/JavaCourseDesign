```mermaid
flowchart TD
    A[Client] -->|HTTP| B[JavalinServer]
    B --> C[BookService]
    B --> D[UserService]
    B --> E[BorrowRecordService]
    C --> F[BookDAO]
    D --> G[UserDAO]
    E --> H[BorrowRecordDAO]
    F --> I[SQLite]
    G --> I
    H --> I