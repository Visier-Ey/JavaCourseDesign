```mermaid
graph TD
    E[.] --> library[library.db]
    E --> idea[.idea]
    E --> vscode[.vscode]
    E --> CourseDesign[CourseDesign]
    E --> CourseDesignServer[CourseDesignServer]
    E --> SystemDesign[SystemDesign]

    idea --> idea_files[.gitignore<br>compiler.xml<br>encodings.xml<br>jarRepositories.xml<br>misc.xml<br>vcs.xml<br>workspace.xml]

    vscode --> vscode_files[settings.json]

    CourseDesign --> CourseDesign_files[.gitignore<br>mvnw<br>mvnw.cmd<br>pom.xml]
    CourseDesign --> idea_CourseDesign[.idea<br>.gitignore<br>compiler.xml<br>encodings.xml<br>jarRepositories.xml<br>misc.xml<br>vcs.xml<br>workspace.xml]
    CourseDesign --> mvn_wrapper[.mvn<br>wrapper<br>maven-wrapper.jar<br>maven-wrapper.properties]
    CourseDesign --> src[src]
    CourseDesign --> target[target]

    src --> main[main]
    main --> java[java]
    main --> resources[resources]

    java --> module_info[module-info.java]
    java --> org[org]
    org --> visier[visier]
    visier --> coursedesign[coursedesign]
    coursedesign --> ApplicationMain[ApplicationMain.java]
    coursedesign --> ApiClient[ApiClient.java]
    coursedesign --> Controller[Controller]
    coursedesign --> Entity[Entity]
    coursedesign --> Manager[Manager]
    coursedesign --> Service[Service]
    coursedesign --> Session[Session]
    coursedesign --> Utils[Utils]

    resources --> fxml[booksManagement.fxml<br>borrowRecordsManagement.fxml<br>dashboard.fxml<br>login.fxml<br>main.fxml<br>userManagement.fxml]

    target --> classes[classes]
    classes --> module_info_class[module-info.class]
    classes --> org_classes[org<br>visier<br>coursedesign]
    org_classes --> ApplicationMain_class[ApplicationMain.class]
    org_classes --> fxml_files[booksManagement.fxml<br>borrowRecordsManagement.fxml<br>dashboard.fxml<br>login.fxml<br>main.fxml<br>userManagement.fxml]
    org_classes --> ApiClient_class[ApiClient.class]
    org_classes --> Controller_class[BooksManagementController$1.class<br>BooksManagementController.class<br>BorrowRecordsManagementController$1.class<br>BorrowRecordsManagementController.class<br>DashboardController.class<br>LoginController.class<br>MainController.class<br>UserManagementController$1.class<br>UserManagementController.class]
    org_classes --> Entity_class[Admin.class<br>Book.class<br>BorrowRecord.class<br>NormalUser.class<br>User.class]
    org_classes --> Manager_class[SceneManager.class]
    org_classes --> Service_class[BookService.class<br>BorrowRecordService.class<br>UserService.class]
    org_classes --> Session_class[UserSession.class]
    org_classes --> Utils_class[Utils.class]

    target --> generated_sources[generated-sources<br>annotations]
    target --> test_classes[test-classes]

    CourseDesignServer --> CourseDesignServer_files[.gitignore<br>pom.xml]
    CourseDesignServer --> idea_CourseDesignServer[.idea<br>.gitignore<br>compiler.xml<br>dataSources.local.xml<br>dataSources.xml<br>encodings.xml<br>jarRepositories.xml<br>misc.xml<br>vcs.xml<br>workspace.xml]
    CourseDesignServer --> dataSources[dataSources<br>408a3d31-7c68-4178-8de0-5559bee5d58c.xml<br>408a3d31-7c68-4178-8de0-5559bee5d58c<br>storage_v2<br>_src_<br>schema<br>main.uQUzAA.meta]
    CourseDesignServer --> mvn[.mvn]
    CourseDesignServer --> src[src]
    CourseDesignServer --> target[Target]

    src --> main_server[main]
    main_server --> java_server[java]
    main_server --> resources_server[resources]

    java_server --> Main[Main.java]
    java_server --> DAO[DAO]
    java_server --> Javalin[Javalin]
    java_server --> Service[Service]
    java_server --> Socket[Socket]
    java_server --> Sqlite[Sqlite]
    java_server --> Utils[Utils]

    resources_server --> resources_files[resources files]

    target --> classes_server[classes]
    classes_server --> org_server[org<br>JBR]
    org_server --> Main_class[Main.class]
    org_server --> DAO_class[BookDAO.class<br>BorrowRecordDAO.class<br>UserDAO.class]
    org_server --> Javalin_class[JavalinServer.class]
    org_server --> Service_class[BookService.class<br>BorrowRecordsService.class<br>UserService.class]
    org_server --> Socket_class[ClientHandler.class<br>Server.class]
    org_server --> Sqlite_class[AddFakeBooks$BookData.class<br>AddFakeBooks.class<br>DatabaseInitializer.class<br>SQLiteHelper$SQLTransaction.class<br>SQLiteHelper.class]
    org_server --> Utils_class[JwtUtil.class<br>Utils.class]

    target --> generated_sources_server[generated-sources<br>annotations]
    target --> test_classes_server[test-classes]

    SystemDesign --> SystemDesign_files[BookBorrowingProcess.md<br>ClassEntities.md<br>DatabaseERDiagram.md<br>RecommendationEngineFlow.md<br>SystemStructure.md<br>UserLoginSequence.md]