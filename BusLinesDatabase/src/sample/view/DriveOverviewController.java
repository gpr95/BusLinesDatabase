package sample.view;

import java.util.ArrayList;
import java.util.List;

import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.ListView;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sample.controller.MainApp;
import sample.model.Drive;

/**
 * Created by Marek on 2016-12-25.
 */
public class DriveOverviewController {
    @FXML
    private TableView<Drive> tableDrive;
    @FXML
    private TableColumn<Drive, String> columnFrom;
    @FXML
    private TableColumn<Drive, String> columnTo;

    @FXML
    private Label labelFrom;
    @FXML
    private Label labelTo;
    @FXML
    private Label labelTime;
    @FXML
    private Label labelDistance;
    @FXML
    private Label labelPrice;
    @FXML
    private ListView<String> listIntermediateDrive;

    private MainApp mainApp;

    public DriveOverviewController() {}

    @FXML
    private void initialize()
    {
        columnFrom.setCellValueFactory(cellData -> cellData.getValue().fromProperty());
        columnTo.setCellValueFactory(cellData -> cellData.getValue().toProperty());

        showDriveDetails(null);

        tableDrive.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showDriveDetails(newValue));
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        tableDrive.setItems(mainApp.getDriveData());
    }

    public void showDriveDetails(Drive drive)
    {
        if(drive != null) {
            labelFrom.setText(drive.getFrom());
            labelTo.setText(drive.getTo());
            labelTime.setText(String.valueOf(drive.getTime()));
            labelDistance.setText(String.valueOf(drive.getDistance()));
            labelPrice.setText(String.valueOf(drive.getPrice()));
            List<String> intermediateNames = new ArrayList<>();
            intermediateNames.add(drive.getFrom());
            drive.getListOfIntermediateDrive().forEach(d -> intermediateNames.add(d.getCityTo()));
            ObservableList<String> items = FXCollections.observableArrayList (intermediateNames);
            listIntermediateDrive.setItems(items);
        }else
        {
            labelFrom.setText("");
            labelTo.setText("");
            labelTime.setText("");
            labelDistance.setText("");
            labelPrice.setText("");
        }

    }

   

    @FXML
    private void handleNewDrive() {
        Drive tempDrive = new Drive();
        boolean okClicked = mainApp.showDriveEditDialog(tempDrive);
        if (okClicked) {
        	tableDrive.setItems(mainApp.getDriveData());
        }
    }

    /**
     * Called when the user clicks the edit button. Opens a dialog to edit
     * details for the selected person.
     */
    @FXML
    private void handleEditDrive() {
        Drive selectedDrive = tableDrive.getSelectionModel().getSelectedItem();
        if (selectedDrive != null) {
            boolean okClicked = mainApp.showDriveEditDialog(selectedDrive);
            if (okClicked) {
                showDriveDetails(selectedDrive);
            }

        } else {
            // Nothing selected.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Drive Selected");
            alert.setContentText("Please select a drive in the table.");

            alert.showAndWait();
        }
    }
    
    @FXML
    private void handleDeleteDrive() {
        int selectedIndex = tableDrive.getSelectionModel().getSelectedIndex();
        Drive selectedDrive = tableDrive.getSelectionModel().getSelectedItem();
        if (selectedIndex >= 0) {
            tableDrive.getItems().remove(selectedIndex);
            mainApp.deleteDrive(selectedDrive);
        } else {
            // Nothing selected.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Drive Selected");
            alert.setContentText("Please select a drive in the table.");

            alert.showAndWait();
        }
    }
}
