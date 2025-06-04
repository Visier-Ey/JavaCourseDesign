package org.JBR.Javalin;

import org.JBR.Service.*;

import io.javalin.Javalin;
import static io.javalin.apibuilder.ApiBuilder.*;
import org.JBR.Utils.JwtUtil;

public class JavalinServer {
    public static void run() {
        // ! Javalin Server Initialization
        Javalin app = Javalin.create(
                config -> {
                    config.router.apiBuilder(
                            () -> {
                                // ! User Management Routes
                                path("/users", () -> {
                                    get(UserService::getAllUsers);
                                    post(UserService::createUser);
                                    path("/{id}", () -> {
                                        get(UserService::getUser);
                                        patch(UserService::updateUser);
                                        delete(UserService::deleteUser);
                                        path("freeze", () -> {
                                            patch(UserService::freezeUser);
                                        });
                                        path("unfreeze", () -> {
                                            patch(UserService::unfreezeUser);
                                        });
                                        path("/promote", () -> {
                                            patch(UserService::promoteUser);
                                        });
                                    });
                                    ws("/events", UserService::webSocketEvents);
                                    path("/login", () -> {
                                        post(UserService::login);
                                    });
                                    path("/register", () -> {
                                        post(UserService::register);
                                    });
                                });
                                // ! Book Management Routes
                                path("/books", () -> {
                                    get(BookService::getAllBooks);
                                    post(BookService::createBook);
                                    path("/{id}", () -> {
                                        get(BookService::getBook);
                                        patch(BookService::updateBook);
                                        delete(BookService::deleteBook);
                                    });
                                });
                                // ! Borrow Records Management Routes
                                path("/borrow-records", () -> {
                                    get(BorrowRecordsService::getAllRecords);
                                    post(BorrowRecordsService::createRecord);
                                    path("/statistics", () -> {
                                            get(BorrowRecordsService::getStatistics);
                                        });
                                    path("/{id}", () -> {
                                        get(BorrowRecordsService::getRecord);
                                        patch(BorrowRecordsService::updateRecord);
                                        delete(BorrowRecordsService::deleteRecord);
                                        path("/return", () -> {
                                            patch(BorrowRecordsService::returnBook);
                                        });
                                    });
                                });
                            });
                }).start(8080);

        // ! JWT Verification
        app.before(ctx -> {
            if (!ctx.path().equals("/users/login") && !ctx.path().equals("/users/register")) {
                String token = ctx.header("Authorization");
                if (token == null || !token.startsWith("Bearer ")) {
                    ctx.status(401).json("Missing or invalid token");
                    return;
                }
                token = token.substring(7); // * */ Remove "Bearer " prefix
                // TODO: Implement JWT validation logic
                if (!JwtUtil.validateToken(token)) {
                    ctx.status(401).json("Invalid or expired token");
                }
            }
        });

    }
}