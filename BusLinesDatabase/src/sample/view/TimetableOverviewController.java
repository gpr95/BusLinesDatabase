package sample.view;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.fxml.FXML;
import javafx.scene.control.*;
import sample.controller.MainApp;
import sample.model.Drive;
import sample.model.TimeTablePosition;

import java.text.DecimalFormat;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

/**
 * Created by Marek on 2017-01-02.
 */
public class TimetableOverviewController {

    @FXML
    private TableView<TimeTableList> tableTimetable;
    @FXML
    private TableColumn<TimeTableList, String> columnTimetable;

    @FXML
    private TableColumn<TimeTableList, String> columnMon;
    @FXML
    private TableColumn<TimeTableList, String> columnTue;
    @FXML
    private TableColumn<TimeTableList, String> columnWen;
    @FXML
    private TableColumn<TimeTableList, String> columnThu;
    @FXML
    private TableColumn<TimeTableList, String> columnFri;
    @FXML
    private TableColumn<TimeTableList, String> columnSat;
    @FXML
    private TableColumn<TimeTableList, String> columnSun;

    @FXML
    private ChoiceBox<Drive> choiceBoxDrive;

    private MainApp mainApp;
    private List<Drive> driveList;
    private List<TimeTablePosition> timetable;
    private List<TimeTableList> timeTableForTable;

    @FXML
    private void initialize() {

        choiceBoxDrive.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
            @Override
            public void changed(ObservableValue<?> ov, Object t, Object t1) {
                Drive selectedDrive = choiceBoxDrive.getSelectionModel().getSelectedItem();
                if (selectedDrive != null) {
                    timetable = mainApp.getTimeTablePositionsForDrive(selectedDrive.getId());
                    timeTableForTable = new ArrayList<>();
                    for(int i = 0; i < 24; i++)
                    {
                        TimeTableList ttl = new TimeTableList(timetable, i);
                        timeTableForTable.add(ttl);
                    }

                    tableTimetable.setItems(FXCollections.observableArrayList (timeTableForTable));
                    columnMon.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getMonday()));
                    columnTue.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getTue()));
                    columnWen.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getWen()));
                    columnThu.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getThu()));
                    columnFri.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getFri()));
                    columnSat.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSat()));
                    columnSun.setCellValueFactory(cellData -> new SimpleStringProperty(cellData.getValue().getSun()));
                } else {
                    // Error.
                    Alert alert = new Alert(Alert.AlertType.WARNING);
                    alert.initOwner(mainApp.getPrimaryStage());
                    alert.setTitle("No Selection");
                    alert.setHeaderText("No Drive Selected");
                    alert.setContentText("Please select a bus in the drop down menu.");

                    alert.showAndWait();
                }
            }
        });
    }

    public void setMainApp(MainApp mainApp) {

        this.mainApp = mainApp;
        driveList = mainApp.getDriveData();
        choiceBoxDrive.setItems(FXCollections.observableArrayList(driveList));//(driveList.stream().map(d -> d.getFrom()+ " ->" + d.getTo())
                //.collect(Collectors.toCollection(FXCollections::observableArrayList)));
    }

    private class TimeTableList {
        private String monday;
        private String tue = "";
        private String wen = "";
        private String thu = "";
        private String fri = "";
        private String sat = "";
        private String sun = "";

        public TimeTableList(List<TimeTablePosition> time, int hour)
        {
            DecimalFormat myFormatter = new DecimalFormat("00");
            String output = myFormatter.format(hour);
            monday = output + "\t |";
            for (TimeTablePosition t : time) {
                switch (t.getWeekDay()){
                    case "MONDAY":
                        if(Integer.parseInt(t.getLeavingHour().split(":")[0]) == hour)
                            monday = monday + " " + t.getLeavingHour().split(":")[1];
                        break;
                    case "TUESDAY":
                        if(Integer.parseInt(t.getLeavingHour().split(":")[0]) == hour)
                            tue = tue + " " + t.getLeavingHour().split(":")[1];
                        break;
                    case "WEDNESDAY":
                        if(Integer.parseInt(t.getLeavingHour().split(":")[0]) == hour)
                            wen = wen + " " + t.getLeavingHour().split(":")[1];
                        break;
                    case "THURSDAY":
                        if(Integer.parseInt(t.getLeavingHour().split(":")[0]) == hour)
                            thu = thu + " " + t.getLeavingHour().split(":")[1];
                        break;
                    case "FRIDAY":
                        if(Integer.parseInt(t.getLeavingHour().split(":")[0]) == hour)
                            fri = fri + " " + t.getLeavingHour().split(":")[1];
                        break;
                    case "SATURDAY":
                        if(Integer.parseInt(t.getLeavingHour().split(":")[0]) == hour)
                            sat = sat + " " + t.getLeavingHour().split(":")[1];
                        break;
                    case "SUNDAY":
                        if(Integer.parseInt(t.getLeavingHour().split(":")[0]) == hour)
                            sun = sun + " " + t.getLeavingHour().split(":")[1];
                        break;
                }

            }
        }

        public String getSun() {
            return sun;
        }

        public void setSun(String sun) {
            this.sun = sun;
        }

        public String getMonday() {
            return monday;
        }

        public void setMonday(String monday) {
            this.monday = monday;
        }

        public String getTue() {
            return tue;
        }

        public void setTue(String tue) {
            this.tue = tue;
        }

        public String getWen() {
            return wen;
        }

        public void setWen(String wen) {
            this.wen = wen;
        }

        public String getThu() {
            return thu;
        }

        public void setThu(String thu) {
            this.thu = thu;
        }

        public String getFri() {
            return fri;
        }

        public void setFri(String fri) {
            this.fri = fri;
        }

        public String getSat() {
            return sat;
        }

        public void setSat(String sat) {
            this.sat = sat;
        }
    }
}
