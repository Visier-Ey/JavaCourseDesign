package org.visier.coursedesign.Controller;

import javafx.fxml.FXML;
import javafx.scene.control.*;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import org.visier.coursedesign.Entity.BorrowRecord;
import org.visier.coursedesign.Entity.Book;
import org.visier.coursedesign.Entity.User;

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
        statusColumn.setCellValueFactory(cellData -> {
            Date now = new Date();
            Date dueDate = cellData.getValue().getDueDate();
            return new SimpleStringProperty(
                    now.after(dueDate) ? "Overdue" : "Active"
            );
        });

        //* Action column for returning books */
        actionColumn.setCellFactory(param -> new TableCell<>() {
            private final Button returnBtn = new Button("Return");

            {
                returnBtn.setOnAction(event -> {
                    BorrowRecord record = getTableView().getItems().get(getIndex());
                    returnBook(record);
                });
            }

            @Override
            protected void updateItem(Void item, boolean empty) {
                super.updateItem(item, empty);
                if (empty) {
                    setGraphic(null);
                } else {
                    setGraphic(returnBtn);
                }
            }
        });

        //? Load sample data
        loadSampleData();

        //! binding the data to the table
        recordsTable.setItems(recordList);

        //* Set up search and action buttons */
        searchButton.setOnAction(e -> searchRecords());
        addButton.setOnAction(e -> showAddRecordDialog());
        reportButton.setOnAction(e -> generateOverdueReport());
        returnButton.setOnAction(e -> returnSelectedBook());
    }

    private void loadSampleData() {
        //* Sample data for demonstration purposes */
        User user1 = new User("U001", "Alice", "hash1", "NORMAL", false);
        User user2 = new User("U002", "Bob", "hash2", "NORMAL", false);

        Book book1 = new Book("B001", "Java Programming", "John Doe", "123456789", true);
        Book book2 = new Book("B002", "Advanced Java", "Jane Smith", "987654321", true);

        recordList.add(new BorrowRecord(user1, book1));
        recordList.add(new BorrowRecord(user2, book2));
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
        recordList.remove(record);

        Alert alert = new Alert(Alert.AlertType.INFORMATION);
        alert.setTitle("Book Returned");
        alert.setHeaderText("Book successfully returned");
        alert.setContentText(record.getBook().getTitle() + " is now available");
        alert.showAndWait();
    }
}