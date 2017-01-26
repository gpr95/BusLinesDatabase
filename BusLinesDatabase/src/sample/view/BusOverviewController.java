package sample.view;

import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Label;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import sample.controller.MainApp;
import sample.model.Bus;

/**
 * Created by Marek on 2016-12-26.
 */
public class BusOverviewController {

    @FXML
    private TableView<Bus> tableBus;
    @FXML
    private TableColumn<Bus, String> columnBusPlate;

    @FXML
    private Label labelPlate;
    @FXML
    private Label labelCategory;
    @FXML
    private Label LabelModel;
    @FXML
    private Label labelSeats;
    @FXML
    private Label labelMileage;
    @FXML
    private Label labelSerialNumber;
    @FXML
    private Label labelBoughtDate;

    private MainApp mainApp;

    @FXML
    private void initialize() {
        columnBusPlate.setCellValueFactory(cellData -> cellData.getValue().licensePlateProperty());
        showBusDetails(null);
        tableBus.getSelectionModel().selectedItemProperty().addListener(
                (observable, oldValue, newValue) -> showBusDetails(newValue));
    }

    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;
        tableBus.setItems(mainApp.getBusData());
    }

    public void showBusDetails(Bus bus)
    {
        if(bus != null) {
            labelPlate.setText(bus.getLicensePlate());
            labelCategory.setText(String.valueOf(bus.getClassRate()));
            LabelModel.setText(String.valueOf(bus.getModelName()));
            labelSeats.setText(String.valueOf(bus.getSeats()));
            labelMileage.setText(String.valueOf(bus.getMileage()));
            labelSerialNumber.setText(String.valueOf(bus.getSereialNumber()));
            labelBoughtDate.setText(String.valueOf(bus.getDateOfBuy()));
        }else
        {
            labelPlate.setText("");
            labelCategory.setText("");
            LabelModel.setText("");
            labelSeats.setText("");
            labelMileage.setText("");
            labelSerialNumber.setText("");
            labelBoughtDate.setText("");
        }
    }
    
    @FXML
    private void handleNewBus() {
        Bus tempBus = new Bus();
        boolean okClicked = mainApp.showBusEditDialog(tempBus);
        if (okClicked) {
            tableBus.setItems(mainApp.getBusData());
        }
    }
    
    @FXML
    private void handleEditBus() {
        Bus selectedBus = tableBus.getSelectionModel().getSelectedItem();
        if (selectedBus != null) {
            boolean okClicked = mainApp.showBusEditDialog(selectedBus);
            if (okClicked) {
            	showBusDetails(selectedBus);
            }

        } else {
            // Nothing selected.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Bus Selected");
            alert.setContentText("Please select a bus in the table.");

            alert.showAndWait();
        }
    }
    
    @FXML
    private void handleDeleteBus() {
        int selectedIndex = tableBus.getSelectionModel().getSelectedIndex();
        Bus selectedBus = tableBus.getSelectionModel().getSelectedItem();
        if (selectedIndex >= 0) {
        	tableBus.getItems().remove(selectedIndex);
        	mainApp.deleteBus(selectedBus);
        } else {
            // Nothing selected.
            Alert alert = new Alert(Alert.AlertType.WARNING);
            alert.initOwner(mainApp.getPrimaryStage());
            alert.setTitle("No Selection");
            alert.setHeaderText("No Bus Selected");
            alert.setContentText("Please select a bus in the table.");

            alert.showAndWait();
        }
    }
   
}
