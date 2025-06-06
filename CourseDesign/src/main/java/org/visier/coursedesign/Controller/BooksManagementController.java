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
import javafx.util.Pair;
import javafx.geometry.Insets;

import org.visier.coursedesign.Service.*;
import org.visier.coursedesign.Session.UserSession;
import org.visier.coursedesign.Utils.Utils;

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
    @FXML
    private ComboBox<String> searchTypeComboBox;
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
        searchTypeComboBox.getItems().addAll("Title", "Author", "ISBN");
        searchTypeComboBox.getSelectionModel().selectFirst();
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
            private final Button deleteButton = new Button("Delete");
            private final HBox buttons = new HBox(5, borrowButton, deleteButton);
            {
                borrowButton.setOnAction(event -> handleBorrow(getTableView().getItems().get(getIndex())));
                deleteButton.setOnAction(event -> handleDelete(getTableView().getItems().get(getIndex())));
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    Book book = getTableView().getItems().get(getIndex());
                    borrowButton.setDisable(!book.isAvailable());
                    deleteButton.setVisible(UserSession.getCurrentUser().getRole().equals("ADMIN"));
                    HBox hbox = new HBox(5);
                    hbox.getChildren().add(borrowButton);
                    hbox.getChildren().add(deleteButton);
                    setGraphic(hbox);
                }
            }
        });
    }

    private void handleDelete(Book book) {
        try {
            JSONObject response = BookService.deleteBook(book.getBookId());
            if (response.getBoolean("success")) {
                bookList.remove(book);
                booksTable.refresh();
                // showAlert("Success", "Book deleted successfully.");
            } else {
                Utils.showErrorAlert("Failed to delete book:", "Failed to delete book: " + response.getString("error"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showErrorAlert("Exception:", "Failed to delete book: " + e.getMessage());
        }
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
            Utils.showErrorAlert("Error", "Failed to load books: " + e.getMessage());
        }
    }

    private void setupButtonActions() {
        // 搜索按钮事件
        setSearchButton();

        // 添加按钮事件
        addButton.setOnAction(event -> {
            // 这里可以打开添加书籍的对话框
            showAddBookDialog();
        });
    }

    void setSearchButton() {
        searchButton.setOnAction(event -> {
            String searchText = searchField.getText().trim().toLowerCase();

            if (searchText.isEmpty()) {
                booksTable.setItems(bookList); // 显示全部
                return;
            }

            // 创建搜索类型选择对话框
            Dialog<String> dialog = new Dialog<>();
            dialog.setTitle("Order By..");
            dialog.setHeaderText("Search Condition:");

            // 设置按钮类型
            ButtonType titleButton = new ButtonType("Title", ButtonBar.ButtonData.OK_DONE);
            ButtonType authorButton = new ButtonType("Author", ButtonBar.ButtonData.OK_DONE);
            ButtonType isbnButton = new ButtonType("ISBN", ButtonBar.ButtonData.OK_DONE);

            dialog.getDialogPane().getButtonTypes().addAll(
                    titleButton,
                    authorButton,
                    isbnButton,
                    ButtonType.CANCEL);

            // 设置结果转换器
            dialog.setResultConverter(buttonType -> {
                if (buttonType == titleButton)
                    return "title";
                if (buttonType == authorButton)
                    return "author";
                if (buttonType == isbnButton)
                    return "isbn";
                return null;
            });

            // 显示对话框并等待用户选择
            Optional<String> result = dialog.showAndWait();

            // 根据用户选择执行搜索
            result.ifPresent(searchType -> {
                ObservableList<Book> filteredList = FXCollections.observableArrayList();

                for (Book book : bookList) {
                    boolean match = false;

                    switch (searchType) {
                        case "title":
                            match = book.getTitle().toLowerCase().contains(searchText);
                            break;
                        case "author":
                            match = book.getAuthor().toLowerCase().contains(searchText);
                            break;
                        case "isbn":
                            match = book.getIsbn().contains(searchText);
                            break;
                    }

                    if (match) {
                        filteredList.add(book);
                    }
                }

                booksTable.setItems(filteredList);

                if (filteredList.isEmpty()) {
                    Alert alert = new Alert(Alert.AlertType.INFORMATION);
                    alert.setTitle("搜索结果");
                    alert.setHeaderText(null);
                    alert.setContentText("没有找到匹配的书籍");
                    alert.showAndWait();
                }
            });
        });
    }

    void handleBorrow(Book book) {
        try {
            JSONObject response = BookService.borrowBook(book.getBookId(), UserSession.getCurrentUser().getUserId());
            if (response.getBoolean("success")) {
                book.setAvailable(false);
                booksTable.refresh();
                Utils.showSuccessAlert("Success", "Book borrowed successfully.");
            } else {
                Utils.showErrorAlert("Error", "Failed to borrow book: " +
                        response.getString("message"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showErrorAlert("Error", "Failed to borrow book: " + e.getMessage());
        }
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

        Optional<Book> result = dialog.showAndWait();

        result.ifPresent(book -> {
            try {
                JSONObject bookJson = new JSONObject();
                bookJson.put("book_id", book.getBookId());
                bookJson.put("title", book.getTitle());
                bookJson.put("author", book.getAuthor());
                bookJson.put("isbn", book.getIsbn());
                if (book.getBookId().isEmpty() || book.getTitle().isEmpty() || book.getAuthor().isEmpty() || book.getIsbn().isEmpty()) {
                    Utils.showErrorAlert("Invalid Input", "All fields must be filled out.");
                    return;
                    
                }

                JSONObject response = BookService.createBook(bookJson.toString());

                if (response.getBoolean("success")) {
                    bookList.add(book);
                    booksTable.refresh();
                } else {
                    Utils.showErrorAlert("Failed to add book", response.getString("error"));
                }
            } catch (Exception e) {
                e.printStackTrace();
                Utils.showErrorAlert("Failed to add book", e.getMessage());
            }
        });
    }
}