package org.visier.coursedesign.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.scene.layout.HBox;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.visier.coursedesign.Entity.BorrowRecord;
import org.json.JSONArray;
import org.json.JSONObject;
import org.visier.coursedesign.Entity.Book;
import org.visier.coursedesign.Entity.User;
import org.visier.coursedesign.Service.BorrowRecordService;
import org.visier.coursedesign.Session.UserSession;
import org.visier.coursedesign.Utils.Utils;

import java.text.SimpleDateFormat;

import java.util.Date;

import javafx.beans.property.SimpleStringProperty;

public class BorrowRecordsManagementController {
    @FXML
    private TableView<BorrowRecord> recordsTable;
    @FXML
    private TableColumn<BorrowRecord, String> idColumn;
    @FXML
    private TableColumn<BorrowRecord, String> bookColumn;
    @FXML
    private TableColumn<BorrowRecord, String> userColumn;
    @FXML
    private TableColumn<BorrowRecord, Date> borrowDateColumn;
    @FXML
    private TableColumn<BorrowRecord, Date> dueDateColumn;
    @FXML
    private TableColumn<BorrowRecord, String> statusColumn;
    @FXML
    private TableColumn<BorrowRecord, Void> actionColumn;
    @FXML
    private TextField searchField;
    @FXML
    private Button searchButton;
    @FXML
    private Button addButton;
    @FXML
    private Button reportButton;
    @FXML
    private Button returnButton;

    private ObservableList<BorrowRecord> recordList = FXCollections.observableArrayList();

    @FXML
    private void initialize() {
        //* Set the table cell map to BorrowRecord properties */
        idColumn.setCellValueFactory(cellData -> cellData.getValue().recordIdProperty());
        bookColumn.setCellValueFactory(cellData -> cellData.getValue().getBook().titleProperty());
        userColumn.setCellValueFactory(cellData -> cellData.getValue().getUser().usernameProperty());
        borrowDateColumn.setCellValueFactory(cellData -> cellData.getValue().borrowDateProperty());
        dueDateColumn.setCellValueFactory(cellData -> cellData.getValue().dueDateProperty());

        //* Status column custom display */
        statusColumn.setCellValueFactory(cellData -> cellData.getValue().statusProperty());

        //* Action column for returning books */
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button returnBtn = new Button("Return");
            private final Button deleteBtn = new Button("Delete");
            {
                returnBtn.setOnAction(event -> {
                    BorrowRecord record = getTableView().getItems().get(getIndex());
                    returnBook(record);
                });
                 deleteBtn.setOnAction(event -> {
                    BorrowRecord record = getTableView().getItems().get(getIndex());
                    deleteBook(record);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    returnBtn.setDisable(!getTableView().getItems().get(getIndex()).getStatus().equals("BORROWED"));
                    deleteBtn.setVisible(UserSession.getCurrentUser().getRole().equals("ADMIN"));
                    HBox buttonContainer = new HBox(5); 
                    buttonContainer.getChildren().addAll(returnBtn, deleteBtn);
                    setGraphic(buttonContainer);
                }
            }
        });

        //? Load sample data
        getAllBorrowRecords();

        //! binding the data to the table
        recordsTable.setItems(recordList);

        //* Set up search and action buttons */
        searchButton.setOnAction(e -> searchRecords());
        addButton.setOnAction(e -> showAddRecordDialog());
        reportButton.setOnAction(e -> generateOverdueReport());
        returnButton.setOnAction(e -> returnSelectedBook());
    }

    private void getAllBorrowRecords() {
        //* Sample data for demonstration purposes */
        try {
            JSONObject response = BorrowRecordService.getAllBorrowRecords();
             if (response.getBoolean("success")) {
                JSONArray recordsArray = response.getJSONArray("book-records");
                for (int i = 0; i < recordsArray.length(); i++) {
                    String recordId = recordsArray.getJSONObject(i).getString("record_id");
                    String userId = recordsArray.getJSONObject(i).getString("user_id");
                    String bookId = recordsArray.getJSONObject(i).getString("book_id");
                    String borrowDate = recordsArray.getJSONObject(i).getString("borrow_date");
                    String dueDate = recordsArray.getJSONObject(i).getString("due_date");
                    String status = recordsArray.getJSONObject(i).getString("status");
                     String bookTitle = recordsArray.getJSONObject(i).getString("book_title");
                    String username = recordsArray.getJSONObject(i).getString("username");

                    User user = new User(userId, username, "", "NORMAL", false);
                    Book book = new Book(bookId, bookTitle, "", "", false);
                    BorrowRecord record = new BorrowRecord(user, book, recordId ,status);

                    recordList.add(record);
                }
             }
        }catch (Exception e) {
            e.printStackTrace();
        }
    }

    private void searchRecords() {
        String keyword = searchField.getText().toLowerCase();
        if (keyword.isEmpty()) {
            recordsTable.setItems(recordList);
            return;
        }

        ObservableList<BorrowRecord> filteredList = recordList.filtered(record ->
                record.getRecordId().toLowerCase().contains(keyword) ||
                        record.getBook().getTitle().toLowerCase().contains(keyword) ||
                        record.getUser().getUsername().toLowerCase().contains(keyword)
        );

        recordsTable.setItems(filteredList);
    }

    private void showAddRecordDialog() {
        // TODO: Implementation for adding a new borrow record dialog
    }

    private void generateOverdueReport() {
        recordList.stream()
                .filter(record -> new Date().after(record.getDueDate()))
                .forEach(BorrowRecord::generateOverdueNotice);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Overdue Report");
        alert.setHeaderText("Overdue notices generated");
        alert.setContentText("Check console for overdue notices");
        alert.showAndWait();
    }

    private void returnSelectedBook() {
        BorrowRecord selected = recordsTable.getSelectionModel().getSelectedItem();
        if (selected != null) {
            returnBook(selected);
        }
    }

    private void returnBook(BorrowRecord record) {
        record.getBook().setAvailable(true);
        try {
            JSONObject response = BorrowRecordService.returnBorrowRecord(record.getRecordId());
            if (response.getBoolean("success")) {
                record.setStatus("RETURNED");
                recordsTable.refresh();
            } else {
                Utils.showAlert("Error", "Failed to return book: " + response.getString("error"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showAlert("Error", "Failed to return book: " + e.getMessage());
        }
    }

    private void deleteBook(BorrowRecord record) {
        try {
            JSONObject response = BorrowRecordService.deleteBorrowRecord(record.getRecordId());
            if (response.getBoolean("success")) {
                recordList.remove(record);
                recordsTable.refresh();
            } else {
                Utils.showAlert("Error", "Failed to delete record: " + response.getString("error"));
            }
        } catch (Exception e) {
            e.printStackTrace();
            Utils.showAlert("Error", "Failed to delete record: " + e.getMessage());
        }
    }
}