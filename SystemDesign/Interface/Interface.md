```mermaid
graph LR
    JavalinApp["Javalin App (Port: 8080)"]

    UserManagement["User Management"]
    JavalinApp --> UserManagement
    
    Users["/users"]
    UserManagement --> Users
    
    GET_users["GET /users"] --> UserService.getAllUsers
    Users --> GET_users
    
    POST_user["POST /users"] --> UserService.createUser
    Users --> POST_user
    
    UserById["/users/{id}"]
    Users --> UserById
    
    GET_user["GET /users/{id}"] --> UserService.getUser
    UserById --> GET_user
    
    PATCH_user["PATCH /users/{id}"] --> UserService.updateUser
    UserById --> PATCH_user
    
    DELETE_user["DELETE /users/{id}"] --> UserService.deleteUser
    UserById --> DELETE_user
    
    FreezeUser["/users/{id}/freeze"]
    UserById --> FreezeUser
    
    PATCH_freeze["PATCH /users/{id}/freeze"] --> UserService.freezeUser
    FreezeUser --> PATCH_freeze
    
    UnfreezeUser["/users/{id}/unfreeze"]
    UserById --> UnfreezeUser
    
    PATCH_unfreeze["PATCH /users/{id}/unfreeze"] --> UserService.unfreezeUser
    UnfreezeUser --> PATCH_unfreeze
    
    PromoteUser["/users/{id}/promote"]
    UserById --> PromoteUser
    
    PATCH_promote["PATCH /users/{id}/promote"] --> UserService.promoteUser
    PromoteUser --> PATCH_promote
    
    UserEvents["/users/events"]
    Users --> UserEvents
    
    WS_events["WS /users/events"] --> UserService.webSocketEvents
    UserEvents --> WS_events
    
    UserLogin["/users/login"]
    Users --> UserLogin
    
    POST_login["POST /users/login"] --> UserService.login
    UserLogin --> POST_login
    
    UserRegister["/users/register"]
    Users --> UserRegister
    
    POST_register["POST /users/register"] --> UserService.register
    UserRegister --> POST_register
    
    %% 图书管理路由树
    BookManagement["Book Management"]
    JavalinApp --> BookManagement
    
    Books["/books"]
    BookManagement --> Books
    
    GET_books["GET /books"] --> BookService.getAllBooks
    Books --> GET_books
    
    POST_book["POST /books"] --> BookService.createBook
    Books --> POST_book
    
    BookById["/books/{id}"]
    Books --> BookById
    
    GET_book["GET /books/{id}"] --> BookService.getBook
    BookById --> GET_book
    
    PATCH_book["PATCH /books/{id}"] --> BookService.updateBook
    BookById --> PATCH_book
    
    DELETE_book["DELETE /books/{id}"] --> BookService.deleteBook
    BookById --> DELETE_book
    
    %% 借阅记录管理路由树
    BorrowRecordsManagement["Borrow Records Management"]
    JavalinApp --> BorrowRecordsManagement
    
    BorrowRecords["/borrow-records"]
    BorrowRecordsManagement --> BorrowRecords
    
    GET_records["GET /borrow-records"] --> BorrowRecordsService.getAllRecords
    BorrowRecords --> GET_records
    
    POST_record["POST /borrow-records"] --> BorrowRecordsService.createRecord
    BorrowRecords --> POST_record
    
    BorrowRecordsStatistics["/borrow-records/statistics"]
    BorrowRecords --> BorrowRecordsStatistics
    
    GET_stats["GET /borrow-records/statistics"] --> BorrowRecordsService.getStatistics
    BorrowRecordsStatistics --> GET_stats
    
    BorrowRecordById["/borrow-records/{id}"]
    BorrowRecords --> BorrowRecordById
    
    GET_record["GET /borrow-records/{id}"] --> BorrowRecordsService.getRecord
    BorrowRecordById --> GET_record
    
    PATCH_record["PATCH /borrow-records/{id}"] --> BorrowRecordsService.updateRecord
    BorrowRecordById --> PATCH_record
    
    DELETE_record["DELETE /borrow-records/{id}"] --> BorrowRecordsService.deleteRecord
    BorrowRecordById --> DELETE_record
    
    BorrowRecordReturn["/borrow-records/{id}/return"]
    BorrowRecordById --> BorrowRecordReturn
    
    PATCH_return["PATCH /borrow-records/{id}/return"] --> BorrowRecordsService.returnBook
    BorrowRecordReturn --> PATCH_return
    
    %% 应用样式
    class GET_users,GET_user,GET_books,GET_book,GET_records,GET_record,GET_stats get
    class POST_user,POST_book,POST_record,POST_login,POST_register post
    class PATCH_user,PATCH_freeze,PATCH_unfreeze,PATCH_promote,PATCH_book,PATCH_record,PATCH_return patch
    class DELETE_user,DELETE_book,DELETE_record delete
    class WS_events ws