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
import javafx.geometry.Insets;

import org.visier.coursedesign.Service.*;

public class BooksManagementController {
    @FXML private TextField searchField;
    @FXML private Button searchButton;
    @FXML private Button addButton;
    @FXML private TableView<Book> booksTable;
    
    // 表格列定义
    @FXML private TableColumn<Book, String> idColumn;
    @FXML private TableColumn<Book, String> titleColumn;
    @FXML private TableColumn<Book, String> authorColumn;
    @FXML private TableColumn<Book, String> isbnColumn;
    @FXML private TableColumn<Book, Boolean> availableColumn;
    
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
        
        // 添加一些示例数据
        getAllBooks();
        
        // 设置按钮事件处理
        setupButtonActions();
    }
    
    private void getAllBooks() {
        try {
            JSONObject response = BookService.getAllBooks();
            bookList.clear();
            JSONArray booksArray = response.getJSONArray("books");
            for (int i = 0; i < booksArray.length(); i++) {
                JSONObject bookJson = booksArray.getJSONObject(i);
                Book book = new Book(
                    bookJson.getString("book_id"),
                    bookJson.getString("title"),
                    bookJson.getString("author"),
                    bookJson.getString("isbn"),
                    bookJson.getInt("available") == 1 
                );
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