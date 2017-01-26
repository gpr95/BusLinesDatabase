package sample.view;

import java.util.ArrayList;
import java.util.Collection;
import java.util.List;
import java.util.stream.Collectors;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.paint.Color;
import javafx.scene.text.Text;
import javafx.stage.Stage;
import sample.controller.MainApp;
import sample.model.Drive;
import sample.model.IntermediateDrive;

/**
 * Created by Marek on 2016-12-25.
 */
public class PlanDriveController {

    @FXML
    private TableView<Drive> tableDrive;
    @FXML
    private TableColumn<Drive, String> columnFrom;
    @FXML
    private TableColumn<Drive, String> columnTo;

    @FXML
    private TableView<IntermediateDrive> tableStops;
    @FXML
    private TableColumn<IntermediateDrive, String> columnStops;
    @FXML
    private TableView<IntermediateDrive> tablePossibleStops;
    @FXML
    private TableColumn<IntermediateDrive, String> columnPossibleStops;

    @FXML
    private Text textDriveStart;
    @FXML
    private Label labelTime;
    @FXML
    private Label labelDistance;
    @FXML
    private Label labelPrice;

    private MainApp mainApp;
    private Stage dialogStage;
    private Drive drive;
    private boolean okClicked = false;

    @FXML
    private void initialize() {

        columnFrom.setCellValueFactory(cellData -> cellData.getValue().fromProperty());
        columnTo.setCellValueFactory(cellData -> cellData.getValue().toProperty());

        columnStops.setSortable(false);
    }

    public void setDialogStage(Stage dialogStage) {
        this.dialogStage = dialogStage;
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        tableDrive.setItems(mainApp.getDriveData());


    }

    public void setDrive(Drive drive)
    {
        this.drive = drive;
        tableDrive.getSelectionModel().select(drive);


        if(drive.getListOfIntermediateDrive() != null)
        {
            this.labelTime.setText(String.valueOf(drive.getTime()));
            this.labelDistance.setText(String.valueOf(drive.getDistance()));
            this.labelPrice.setText(String.valueOf(drive.getPrice()));

            this.textDriveStart.setText(drive.getFrom());
            this.textDriveStart.setFill(Color.DARKORANGE);

            tableStops.setItems(FXCollections.observableArrayList (drive.getListOfIntermediateDrive()));
            columnStops.setCellValueFactory(cellData -> cellData.getValue().cityToProperty());

            tablePossibleStops.setItems(mainApp.getIntermediateDrivesFromData(tableStops.getItems().get(tableStops.getItems().size() - 1).getCityTo()));
            columnPossibleStops.setCellValueFactory(cellData -> cellData.getValue().cityToProperty());
        }
        else
        {
            this.labelTime.setText("0");
            this.labelDistance.setText("0");
            this.labelPrice.setText("0");
            this.textDriveStart.setText("");

            Collection<IntermediateDrive> tmp = mainApp.getIntermediateDrivesData().stream()
            		.collect(Collectors.toMap(IntermediateDrive::getCityFrom, p->p , (p,q) -> p))
            		.values();
            ObservableList<IntermediateDrive> tmp2 = tmp.stream()
            		.collect(Collectors.toCollection(FXCollections::observableArrayList));
            tablePossibleStops.setItems(tmp2);
            columnPossibleStops.setCellValueFactory(cellData -> cellData.getValue().cityFromProperty());
        }
    }

    public boolean isOkClicked() {
        return okClicked;
    }

    @FXML
    private void handleOk() {
        if (isInputValid()) {
            drive.setTo(tableStops.getItems().get(tableStops.getItems().size() - 1).getCityTo());
            okClicked = true;
            
            if(drive.getId() == -1)
            	mainApp.insertDrive(drive);
            else
            	mainApp.editDrive(drive);
            mainApp.showDriveOverview();
        }
    }

    @FXML
    private void addStop() {

        if(tablePossibleStops.getSelectionModel().getSelectedItem() == null)
            return;
            if ((drive.getListOfIntermediateDrive() == null || drive.getListOfIntermediateDrive().isEmpty()) && drive.getFrom() == null) {
                drive.setFrom(tablePossibleStops.getSelectionModel().selectedItemProperty().get().getCityFrom());

                tablePossibleStops.setItems(mainApp.getIntermediateDrivesFromData(drive.getFrom()));
                columnPossibleStops.setCellValueFactory(cellData -> cellData.getValue().cityToProperty());

                this.textDriveStart.setText(drive.getFrom());
                this.textDriveStart.setFill(Color.DARKORANGE);

                drive.setDistance(0);
                drive.setTime(0);
                drive.setPrice(0);
                columnPossibleStops.setText("Next station");
            } else if ((drive.getListOfIntermediateDrive() == null || drive.getListOfIntermediateDrive().isEmpty())) {
                List<IntermediateDrive> temp = new ArrayList<>();
                temp.add(tablePossibleStops.getSelectionModel().selectedItemProperty().get());
                drive.setListOfIntermediateDrive(temp);
                tableStops.setItems(FXCollections.observableArrayList(drive.getListOfIntermediateDrive()));
                columnStops.setCellValueFactory(cellData -> cellData.getValue().cityToProperty());
                tablePossibleStops.setItems(mainApp.getIntermediateDrivesFromData(tableStops.getItems().get(tableStops.getItems().size() - 1).getCityTo()));
                columnPossibleStops.setCellValueFactory(cellData -> cellData.getValue().cityToProperty());

                drive.setDistance(drive.getListOfIntermediateDrive().stream().mapToInt(d -> d.getDistance()).sum());
                drive.setTime(drive.getListOfIntermediateDrive().stream().mapToInt(d -> d.getTime()).sum());
                drive.setPrice(((float) drive.getListOfIntermediateDrive().stream().mapToDouble(d -> d.getPrice()).sum()));
            } else {
                drive.getListOfIntermediateDrive().add(tablePossibleStops.getSelectionModel().selectedItemProperty().get());
                tableStops.setItems(FXCollections.observableArrayList(drive.getListOfIntermediateDrive()));
                columnStops.setCellValueFactory(cellData -> cellData.getValue().cityToProperty());
                tablePossibleStops.setItems(mainApp.getIntermediateDrivesFromData(tableStops.getItems().get(tableStops.getItems().size() - 1).getCityTo()));
                columnPossibleStops.setCellValueFactory(cellData -> cellData.getValue().cityToProperty());

                drive.setDistance(drive.getListOfIntermediateDrive().stream().mapToInt(d -> d.getDistance()).sum());
                drive.setTime(drive.getListOfIntermediateDrive().stream().mapToInt(d -> d.getTime()).sum());
                drive.setPrice(((float) drive.getListOfIntermediateDrive().stream().mapToDouble(d -> d.getPrice()).sum()));
            }

        this.labelTime.setText(String.valueOf(drive.getTime()));
        this.labelDistance.setText(String.valueOf(drive.getDistance()));
        this.labelPrice.setText(String.valueOf(drive.getPrice()));

    }

    @FXML
    private void removeStop() {

        if(drive.getListOfIntermediateDrive() == null)
            return;
        if(drive.getListOfIntermediateDrive().isEmpty())
        {
            drive.setFrom(null);
            textDriveStart.setText("");
            Collection<IntermediateDrive> tmp = mainApp.getIntermediateDrivesData().stream()
            		.collect(Collectors.toMap(IntermediateDrive::getCityFrom, p->p , (p,q) -> p))
            		.values();
            ObservableList<IntermediateDrive> tmp2 = tmp.stream()
            		.collect(Collectors.toCollection(FXCollections::observableArrayList));
            tablePossibleStops.setItems(tmp2);
            columnPossibleStops.setCellValueFactory(cellData -> cellData.getValue().cityFromProperty());
            columnPossibleStops.setText("First station");
            return;
        }

        drive.getListOfIntermediateDrive().remove(tableStops.getItems().get(tableStops.getItems().size() - 1));
        if(drive.getListOfIntermediateDrive().isEmpty())
        {
        	
            tablePossibleStops.setItems(mainApp.getIntermediateDrivesFromData(drive.getFrom()));
            columnPossibleStops.setCellValueFactory(cellData -> cellData.getValue().cityToProperty());

            tableStops.setItems(null);
            this.labelTime.setText("0");
            this.labelDistance.setText("0");
            this.labelPrice.setText("0");
            return;
        }
        tableStops.setItems(FXCollections.observableArrayList(drive.getListOfIntermediateDrive()));
        columnStops.setCellValueFactory(cellData -> cellData.getValue().cityToProperty());
        tablePossibleStops.setItems(mainApp.getIntermediateDrivesFromData(tableStops.getItems().get(tableStops.getItems().size() - 1).getCityTo()));
        columnPossibleStops.setCellValueFactory(cellData -> cellData.getValue().cityToProperty());

        drive.setDistance(drive.getListOfIntermediateDrive().stream().mapToInt(d -> d.getDistance()).sum());
        drive.setTime(drive.getListOfIntermediateDrive().stream().mapToInt(d -> d.getTime()).sum());
        drive.setPrice(((float) drive.getListOfIntermediateDrive().stream().mapToDouble(d -> d.getPrice()).sum()));
        this.labelTime.setText(String.valueOf(drive.getTime()));
        this.labelDistance.setText(String.valueOf(drive.getDistance()));
        this.labelPrice.setText(String.valueOf(drive.getPrice()));
    }

    private boolean isInputValid() {
        String errorMessage = "";

        if (tableStops.getItems().isEmpty()) {
            errorMessage += "No valid stops!\n";
        }

        if (errorMessage.length() == 0) {
            return true;
        } else {
            // Show the error message.
            Alert alert = new Alert(Alert.AlertType.ERROR);
            alert.initOwner(dialogStage);
            alert.setTitle("Invalid Stops");
            alert.setHeaderText("Please correct route");
            alert.setContentText(errorMessage);

            alert.showAndWait();

            return false;
        }
    }

    @FXML
    private void frezzeSelection() {
        tableDrive.getSelectionModel().select(drive);
    }

    @FXML
    private void handleCancel() {
        mainApp.showDriveOverview();
    }

    public Label getLabelPrice() {
        return labelPrice;
    }

    public void setLabelPrice(Label labelPrice) {
        this.labelPrice = labelPrice;
    }

    public Label getLabelTime() {
        return labelTime;
    }

    public void setLabelTime(Label labelTime) {
        this.labelTime = labelTime;
    }

    public Label getLabelDistance() {
        return labelDistance;
    }

    public void setLabelDistance(Label labelDistance) {
        this.labelDistance = labelDistance;
    }

    public TableView<Drive> getTableDrive() {
        return tableDrive;
    }

    public void setTableDrive(TableView<Drive> tableDrive) {
        this.tableDrive = tableDrive;
    }

    public TableColumn<Drive, String> getColumnFrom() {
        return columnFrom;
    }

    public void setColumnFrom(TableColumn<Drive, String> columnFrom) {
        this.columnFrom = columnFrom;
    }

    public TableColumn<Drive, String> getColumnTo() {
        return columnTo;
    }

    public void setColumnTo(TableColumn<Drive, String> columnTo) {
        this.columnTo = columnTo;
    }
}
