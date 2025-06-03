package org.visier.coursedesign.Controller;

import java.util.Optional;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;

import org.json.JSONArray;
import org.json.JSONObject;
import org.visier.coursedesign.Entity.Book;

import javafx.scene.layout.GridPane;
import javafx.scene.layout.HBox;
import javafx.geometry.Insets;

import org.visier.coursedesign.Service.*;
import org.visier.coursedesign.Session.UserSession;

public class BooksManagementController {
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private Button addButton;
    @FXML
    private TableView<Book> booksTable;

    // 表格列定义
    @FXML
    private TableColumn<Book, String> idColumn;
    @FXML
    private TableColumn<Book, String> titleColumn;
    @FXML
    private TableColumn<Book, String> authorColumn;
    @FXML
    private TableColumn<Book, String> isbnColumn;
    @FXML
    private TableColumn<Book, Boolean> availableColumn;
    @FXML
    private TableColumn<Book, Void> actionsColumn;

    // 书籍数据列表
    private ObservableList<Book> bookList = FXCollections.observableArrayList();

    @FXML
    public void initialize() {

        // 初始化表格列
        idColumn.setCellValueFactory(new PropertyValueFactory<>("bookId"));
        titleColumn.setCellValueFactory(new PropertyValueFactory<>("title"));
        authorColumn.setCellValueFactory(new PropertyValueFactory<>("author"));
        isbnColumn.setCellValueFactory(new PropertyValueFactory<>("isbn"));
        availableColumn.setCellValueFactory(new PropertyValueFactory<>("available"));

        // 设置表格数据
        booksTable.setItems(bookList);

        setupActionButtons();
        // 添加一些示例数据
        getAllBooks();

        // 设置按钮事件处理
        setupButtonActions();
    }

    private void setupActionButtons() {
        actionsColumn.setCellFactory(param -> new TableCell<>() {
            private final Button borrowButton = new Button("Borrow");
            private final Button returnButton = new Button("Return");
            private final HBox buttons = new HBox(2, borrowButton, returnButton);
            {
                borrowButton.setOnAction(event -> handleBorrow(getTableView().getItems().get(getIndex())));
                returnButton.setOnAction(event -> handleReturn(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Book book = getTableView().getItems().get(getIndex());
                    borrowButton.setDisable(!book.isAvailable());
                    returnButton.setDisable(book.isAvailable());
                    setGraphic(buttons);
                }
            }
        });
    }

    private void getAllBooks() {
        try {
            JSONObject response = BookService.getAllBooks();
            if (response.getBoolean("success") == false) {
                return;
            }
            bookList.clear();
            JSONArray booksArray = response.getJSONArray("books");
            for (int i = 0; i < booksArray.length(); i++) {
                JSONObject bookJson = booksArray.getJSONObject(i);
                Book book = new Book(
                        bookJson.getString("book_id"),
                        bookJson.getString("title"),
                        bookJson.getString("author"),
                        bookJson.getString("isbn"),
                        bookJson.getInt("available") == 1);
                bookList.add(book);
            }
        } catch (Exception e) {
            e.printStackTrace();
            // showAlert("Error", "Failed to load books: " + e.getMessage());
        }
    }

    private void setupButtonActions() {
        // 搜索按钮事件
        searchButton.setOnAction(event -> {
            String searchText = searchField.getText().toLowerCase();
            if (searchText.isEmpty()) {
                booksTable.setItems(bookList); // 显示全部
            } else {
                ObservableList<Book> filteredList = FXCollections.observableArrayList();
                for (Book book : bookList) {
                    if (book.getTitle().toLowerCase().contains(searchText) ||
                            book.getAuthor().toLowerCase().contains(searchText) ||
                            book.getIsbn().contains(searchText)) {
                        filteredList.add(book);
                    }
                }
                booksTable.setItems(filteredList);
            }
        });

        // 添加按钮事件
        addButton.setOnAction(event -> {
            // 这里可以打开添加书籍的对话框
            showAddBookDialog();
        });
    }

    void handleBorrow(Book book) {
        try {
            JSONObject response = BookService.borrowBook(book.getBookId(), UserSession.getCurrentUser().getUserId());
            if (response.getBoolean("success")) {
                book.setAvailable(false);
                booksTable.refresh();
                // showAlert("Success", "Book borrowed successfully.");
            } else {
                // showAlert("Error", "Failed to borrow book: " +
                // response.getString("message"));
                System.out.println("Failed to borrow book: " + response.getString("error"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            // showAlert("Error", "Failed to borrow book: " + e.getMessage());
        }
    }

    void handleReturn(Book book) {
        // try {
        // JSONObject response = BookService.returnBook(book.getBookId(),
        // UserSession.getCurrentUser().getUserId());
        // if (response.getBoolean("success")) {
        // book.setAvailable(true);
        // booksTable.refresh();
        // // showAlert("Success", "Book returned successfully.");
        // } else {
        // // showAlert("Error", "Failed to return book: " +
        // response.getString("message"));
        // }
        // } catch (Exception e) {
        // e.printStackTrace();
        // // showAlert("Error", "Failed to return book: " + e.getMessage());
        // }
    }

    private void showAddBookDialog() {
        // 创建对话框
        Dialog<Book> dialog = new Dialog<>();
        dialog.setTitle("Add New Book");
        dialog.setHeaderText("Enter book details");

        // 设置按钮类型
        ButtonType addButtonType = new ButtonType("Add", ButtonBar.ButtonData.OK_DONE);
        dialog.getDialogPane().getButtonTypes().addAll(addButtonType, ButtonType.CANCEL);

        // 创建表单
        GridPane grid = new GridPane();
        grid.setHgap(10);
        grid.setVgap(10);
        grid.setPadding(new Insets(20, 150, 10, 10));

        TextField idField = new TextField();
        idField.setPromptText("Book ID");
        TextField titleField = new TextField();
        titleField.setPromptText("Title");
        TextField authorField = new TextField();
        authorField.setPromptText("Author");
        TextField isbnField = new TextField();
        isbnField.setPromptText("ISBN");

        grid.add(new Label("Book ID:"), 0, 0);
        grid.add(idField, 1, 0);
        grid.add(new Label("Title:"), 0, 1);
        grid.add(titleField, 1, 1);
        grid.add(new Label("Author:"), 0, 2);
        grid.add(authorField, 1, 2);
        grid.add(new Label("ISBN:"), 0, 3);
        grid.add(isbnField, 1, 3);

        dialog.getDialogPane().setContent(grid);

        // 设置结果转换器
        dialog.setResultConverter(dialogButton -> {
            if (dialogButton == addButtonType) {
                return new Book(
                        idField.getText(),
                        titleField.getText(),
                        authorField.getText(),
                        isbnField.getText(),
                        true // 默认新书籍可用
                );
            }
            return null;
        });

        // 显示对话框并处理结果
        Optional<Book> result = dialog.showAndWait();
        result.ifPresent(book -> {
            bookList.add(book);
            booksTable.refresh();
        });
    }
}