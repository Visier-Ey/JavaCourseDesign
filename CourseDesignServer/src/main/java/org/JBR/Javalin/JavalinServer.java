package org.JBR.Javalin;

import org.JBR.Service.*;

import io.javalin.Javalin;
import static io.javalin.apibuilder.ApiBuilder.*;
import org.JBR.Utils.JwtUtil;

public class JavalinServer {
    public static void run() {
        //! Javalin Server Initialization
        Javalin app = Javalin.create(
                config -> {
                    config.router.apiBuilder(
                            () -> {
                                // ! User Management Routes
                                path("/users", () -> {
                                    get(UserController::getAllUsers);
                                    post(UserController::createUser);
                                    path("/{id}", () -> {
                                        get(UserController::getUser);
                                        patch(UserController::updateUser);
                                        delete(UserController::deleteUser);
                                    });
                                    ws("/events", UserController::webSocketEvents);
                                    path("/login", () -> {
                                        post(UserController::login);
                                    });
                                    path("/register", () -> {
                                        post(UserController::register);
                                    });
                                });
                                // ! Book Management Routes
                                path("/books", () -> {
                                    get(BookController::getAllBooks);
                                    post(BookController::createBook);
                                    path("/{id}", () -> {
                                        get(BookController::getBook);
                                        patch(BookController::updateBook);
                                        delete(BookController::deleteBook);
                                    });
                                });
                                // ! Borrow Records Management Routes
                                path("/borrow-records", () -> {
                                    get(BorrowRecordsController::getAllRecords);
                                    post(BorrowRecordsController::createRecord);
                                    path("/{id}", () -> {
                                        get(BorrowRecordsController::getRecord);
                                        patch(BorrowRecordsController::updateRecord);
                                        delete(BorrowRecordsController::deleteRecord);
                                    });
                                });
                            });
                }).start(8080);

        //! JWT Verification
        app.before(ctx -> {
            if (!ctx.path().equals("/users/login") && !ctx.path().equals("/users/register")) {
                String token = ctx.header("Authorization");
                if (token == null || !token.startsWith("Bearer ")) {
                    ctx.status(401).json("Missing or invalid token");
                    return;
                }
                token = token.substring(7); //* */ Remove "Bearer " prefix
                // TODO: Implement JWT validation logic
                if (!JwtUtil.validateToken(token)) {
                    ctx.status(401).json("Invalid or expired token");
                }
            }
        });

    }
}