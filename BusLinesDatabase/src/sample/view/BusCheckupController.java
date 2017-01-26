package sample.view;

import java.sql.Date;
import java.time.LocalDate;
import java.time.temporal.ChronoUnit;
import java.util.Calendar;
import java.util.stream.Collectors;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import sample.controller.MainApp;
import sample.model.Bus;
import sample.model.Service;

/**
 * Created by Marek on 2016-12-28.
 */
public class BusCheckupController {

    @FXML
    private TableView<Service> tableServices;
    @FXML
    private TableColumn<Service, String> columnServices;

    @FXML
    private TableView<Service> tableDoneServices;
    @FXML
    private TableColumn<Service, String> columnDoneServices;
    
    @FXML
    private ChoiceBox<Bus> choiceBoxBus;
    @FXML
    private TextField textFieldAge;
    @FXML
    private TextField textFieldMileage;
    @FXML
    private ChoiceBox<String> choiceBoxService;
    @FXML
    private DatePicker dateOfService;
    @FXML 
    private TextField carServiceData;

    private MainApp mainApp;

    @FXML
    private void initialize() {
    	columnServices.setCellValueFactory(cellData -> 
    	
    	new SimpleStringProperty(cellData.getValue().getOperation() + 
    			"\nneed to be done: \n (every:" + cellData.getValue().getKmToDo() + " km )" + 
    			" (every:" + cellData.getValue().getmonthsToDo() + " months )" )
    	
    	);
    	columnDoneServices.setCellValueFactory(cellData -> 
    	
    	new SimpleStringProperty("Date: " + cellData.getValue().getDoneServiceOptionalDate()
    			+" Mileage: " + cellData.getValue().getDoneMileageOptionalValue()
    			+"\n   Operation: "+ cellData.getValue().getOperation())
    	
    	);
     
        
        choiceBoxBus.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> ov, Object t, Object t1) {
            	Bus selectedBus = choiceBoxBus.getSelectionModel().getSelectedItem();
                if (selectedBus != null) {
                    Calendar cal = Calendar.getInstance();
                    cal.setTimeInMillis(selectedBus.getDateOfBuy().getTime());
                    LocalDate today = LocalDate.now();
                    LocalDate birthdayOfBus = LocalDate.of(cal.get(Calendar.YEAR), cal.get(Calendar.MONTH) + 1, cal.get(Calendar.DAY_OF_MONTH));

                    long periodMonths = ChronoUnit.MONTHS.between(birthdayOfBus, today);
                    textFieldAge.setText(periodMonths + " months");
                    textFieldMileage.setText(String.valueOf(selectedBus.getMileage()));
                    tableDoneServices.setItems(mainApp.getServiceHistory(selectedBus));
                } else {
                    // Error.
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.initOwner(mainApp.getPrimaryStage());
                    alert.setTitle("No Selection");
                    alert.setHeaderText("No Bus Selected");
                    alert.setContentText("Please select a bus in the drop down menu.");

                    alert.showAndWait();
                }
            }
        });
    }


    public void setMainApp(MainApp mainApp) {
        this.mainApp = mainApp;

        choiceBoxBus.setItems(mainApp.getBusData());
        choiceBoxService.setItems(mainApp.getService().stream().map(s -> s.getOperation())
                .collect(Collectors.toCollection(FXCollections::observableArrayList)));
    }

    
    @FXML
    private void handleCheck() {
    	ObservableList<Service> tmpNeeded =mainApp.getServicesFromPlan(choiceBoxBus.getSelectionModel().getSelectedItem(), 
    			Integer.valueOf(textFieldAge.getText().substring(0,textFieldAge.getText().indexOf(' '))),
    			Integer.valueOf(textFieldMileage.getText()));    	

    	tableServices.setItems(tmpNeeded);
    }
    
    @FXML 
    private void handleServiceAdding() {
    	ObservableList<Service> tmp = tableServices.getItems();
    	tmp.add(mainApp.getService().get(choiceBoxService.getSelectionModel().getSelectedIndex()));
    }
    
    @FXML 
    private void handleServicing() {
    	ObservableList<Service> toAdd = tableServices.getItems();
    	String serviceData = carServiceData.getText();
    	LocalDate serviceLocalDate = dateOfService.getValue();
		Calendar c =  Calendar.getInstance();
		c.set(serviceLocalDate.getYear(), serviceLocalDate.getMonthValue()-1, serviceLocalDate.getDayOfMonth());
		Date serviceDate = new Date(c.getTimeInMillis());
		mainApp.insertServicesPosition(choiceBoxBus.getSelectionModel().getSelectedItem(),
				toAdd, serviceDate, Integer.valueOf(textFieldMileage.getText()), "COMPLETE", serviceData);
    }
    
    @FXML
    private void handleServiceDelete() {
    	 int selectedIndex = tableServices.getSelectionModel().getSelectedIndex();
    	 if (selectedIndex >= 0) {
    		 tableServices.getItems().remove(selectedIndex);
         } else {
             // Nothing selected.
             Alert alert = new Alert(Alert.AlertType.WARNING);
             alert.initOwner(mainApp.getPrimaryStage());
             alert.setTitle("No Selection");
             alert.setHeaderText("No Service Selected");
             alert.setContentText("Please select a service in the table.");
             alert.showAndWait();
         }
    }
}
