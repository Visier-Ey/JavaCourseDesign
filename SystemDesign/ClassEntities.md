```mermaid
classDiagram
    class User{
        <<Abstract>>
        -StringProperty userId
        -StringProperty username
        -StringProperty passwordHash
        -StringProperty role
        -BooleanProperty frozen
        +register(String username, String password) Boolean
        +login(String username, String password) Boolean
        +hashPassword(String password) String
        +verifyPassword(String password) Boolean
        +get/setUserId()
        +get/setUsername()
        +get/setPasswordHash()
        +get/setRole()
        +is/setFrozen()
    }
    
    class NormalUser{
        +searchBooks(String query)
        +borrowBook(Book book) BorrowRecord
        +returnBook(BorrowRecord record) Boolean
    }
    
    class Admin{
        +addBook(String bookId, String title, String author, String isbn) Book
        +updateBook(Book book, String title, String author) Boolean
        +deleteBook(Book book) Boolean
        +freezeUser(NormalUser user)
        +unfreezeUser(NormalUser user)
        +promoteUser(NormalUser user) Boolean
    }
    
    class Book{
        -StringProperty bookId
        -StringProperty title
        -StringProperty author
        -StringProperty isbn
        -BooleanProperty available
        +get/setBookId()
        +get/setTitle()
        +get/setAuthor()
        +get/setIsbn()
        +is/setAvailable()
    }
    
    class BorrowRecord{
        -String recordId
        -Date borrowDate
        -Date dueDate
        -User user
        -Book book
        +generateRecordId() String
        +calculateDueDate() Date
        +generateOverdueNotice()
        +getRecordId()
        +getBorrowDate()
        +getDueDate()
        +getUser()
        +getBook()
    }
    
    User <|-- NormalUser
    User <|-- Admin
    Book "1" *-- "0..*" BorrowRecord : "is borrowed in"
    User "1" *-- "0..*" BorrowRecord : "creates"
    
    note for User "Password hashing is simplified\nfor demonstration purposes"
    note for BorrowRecord "Automatically sets book.available=false\nwhen created"