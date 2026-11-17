package application;

import javafx.application.Application;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.geometry.Insets;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.*;
import javafx.stage.Stage;

public class HospitalManagementApp extends Application {

    PatientDao dao = new PatientDao();

    TextField idField = new TextField();
    TextField nameField = new TextField();
    TextField ageField = new TextField();
    ComboBox<String> genderBox = new ComboBox<>();
    TextField phoneField = new TextField();
    TextField diagnosisField = new TextField();
    DatePicker datePicker = new DatePicker();

    TextField searchField = new TextField();

    TableView<Patient> table = new TableView<>();
    ObservableList<Patient> list = FXCollections.observableArrayList();

    Label status = new Label("Ready");

    @Override
    public void start(Stage stage) {

        BorderPane root = new BorderPane();
        root.setPadding(new Insets(15));

        Label title = new Label("Hospital Management System");
        title.setStyle("-fx-font-size:20px; -fx-font-weight:bold;");

        VBox top = new VBox(5);
        top.getChildren().addAll(title, new Label("Patient Registration"));

        root.setTop(top);

        GridPane form = new GridPane();
        form.setHgap(10);
        form.setVgap(10);

        idField.setEditable(false);

        genderBox.getItems().addAll("Male", "Female");

        form.add(new Label("ID:"), 0, 0);
        form.add(idField, 1, 0);

        form.add(new Label("Name:"), 0, 1);
        form.add(nameField, 1, 1);

        form.add(new Label("Age:"), 0, 2);
        form.add(ageField, 1, 2);

        form.add(new Label("Gender:"), 0, 3);
        form.add(genderBox, 1, 3);

        form.add(new Label("Phone:"), 0, 4);
        form.add(phoneField, 1, 4);

        form.add(new Label("Diagnosis:"), 0, 5);
        form.add(diagnosisField, 1, 5);

        form.add(new Label("Date:"), 0, 6);
        form.add(datePicker, 1, 6);

        Button addBtn = new Button("Add");
        Button updateBtn = new Button("Update");
        Button deleteBtn = new Button("Delete");
        Button clearBtn = new Button("Clear");

        HBox row1 = new HBox(10, addBtn, updateBtn);
        HBox row2 = new HBox(10, deleteBtn, clearBtn);

        VBox buttons = new VBox(8, row1, row2);

        form.add(buttons, 1, 7);

        Button searchBtn = new Button("Search");
        Button showBtn = new Button("Show All");

        HBox searchBar = new HBox(10, searchField, searchBtn, showBtn);

        addColumn("ID", "patientId");
        addColumn("Name", "name");
        addColumn("Age", "age");
        addColumn("Gender", "gender");
        addColumn("Phone", "phone");
        addColumn("Diagnosis", "diagnosis");
        addColumn("Date", "admissionDate");

        table.setItems(list);

        VBox right = new VBox(10, searchBar, table);

        HBox center = new HBox(15, form, right);

        root.setCenter(center);
        root.setBottom(status);

        addBtn.setOnAction(e -> addPatient());
        updateBtn.setOnAction(e -> updatePatient());
        deleteBtn.setOnAction(e -> deletePatient());
        clearBtn.setOnAction(e -> clear());
        searchBtn.setOnAction(e -> search());
        showBtn.setOnAction(e -> loadAll());

        table.setOnMouseClicked(e -> {

            Patient p = table.getSelectionModel().getSelectedItem();

            if (p != null) {
                idField.setText(String.valueOf(p.getPatientId()));
                nameField.setText(p.getName());
                ageField.setText(String.valueOf(p.getAge()));
                genderBox.setValue(p.getGender());
                phoneField.setText(p.getPhone());
                diagnosisField.setText(p.getDiagnosis());
                datePicker.setValue(p.getAdmissionDate());
            }
        });

        loadAll();

        Scene scene = new Scene(root, 900, 500);

        stage.setScene(scene);
        stage.setTitle("Hospital Management");
        stage.show();
    }

    private <T> void addColumn(String title, String field) {

        TableColumn<Patient, T> col = new TableColumn<>(title);

        col.setCellValueFactory(
                new PropertyValueFactory<>(field));

        table.getColumns().add(col);
    }

    private void addPatient() {

        if (!checkInput())
            return;

        try {

            Patient p = new Patient(
                    0,
                    nameField.getText(),
                    Integer.parseInt(ageField.getText()),
                    genderBox.getValue(),
                    phoneField.getText(),
                    diagnosisField.getText(),
                    datePicker.getValue()
            );

            dao.add(p);

            status.setText("Patient added");

            clear();
            loadAll();

        } catch (Exception ex) {

            status.setText("Error");
        }
    }

    private void updatePatient() {

        if (idField.getText().isEmpty())
            return;

        if (!checkInput())
            return;

        try {

            Patient p = new Patient(
                    Integer.parseInt(idField.getText()),
                    nameField.getText(),
                    Integer.parseInt(ageField.getText()),
                    genderBox.getValue(),
                    phoneField.getText(),
                    diagnosisField.getText(),
                    datePicker.getValue()
            );

            dao.update(p);

            status.setText("Patient updated");

            clear();
            loadAll();

        } catch (Exception ex) {

            status.setText("Error");
        }
    }

    private void deletePatient() {

        if (idField.getText().isEmpty())
            return;

        try {

            dao.delete(Integer.parseInt(idField.getText()));

            status.setText("Patient deleted");

            clear();
            loadAll();

        } catch (Exception ex) {

            status.setText("Error");
        }
    }

    private void search() {

        try {

            list.setAll(dao.search(searchField.getText()));

            status.setText("Search complete");

        } catch (Exception ex) {

            status.setText("Error");
        }
    }

    private void loadAll() {

        try {

            list.setAll(dao.getAll());

            status.setText("Patients loaded");

        } catch (Exception ex) {

            status.setText("Error");
        }
    }

    private boolean checkInput() {

        if (nameField.getText().isEmpty()
                || ageField.getText().isEmpty()
                || genderBox.getValue() == null
                || phoneField.getText().isEmpty()
                || diagnosisField.getText().isEmpty()
                || datePicker.getValue() == null) {

            status.setText("Fill all fields");
            return false;
        }

        try {

            int age = Integer.parseInt(ageField.getText());

            if (age <= 0) {

                status.setText("Invalid age");
                return false;
            }
            if (datePicker.getValue().isAfter(java.time.LocalDate.now())) {
                status.setText("Date cannot be later than today");
                return false;
            }
        } catch (Exception ex) {

            status.setText("Age must be number");
            return false;
        }

        return true;
    }

    private void clear() {

        idField.clear();
        nameField.clear();
        ageField.clear();

        genderBox.setValue(null);

        phoneField.clear();
        diagnosisField.clear();

        datePicker.setValue(null);

        table.getSelectionModel().clearSelection();
    }

    public static void main(String[] args) {

        launch(args);
    }
}