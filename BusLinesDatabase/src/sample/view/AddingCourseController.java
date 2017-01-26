package sample.view;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.time.LocalDate;
import java.util.Calendar;
import java.util.List;
import java.util.stream.Collectors;

import consts.WeekDays;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.value.ChangeListener;
import javafx.beans.value.ObservableValue;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.scene.control.Alert;
import javafx.scene.control.Button;
import javafx.scene.control.ChoiceBox;
import javafx.scene.control.DatePicker;
import javafx.scene.control.TableColumn;
import javafx.scene.control.TableView;
import javafx.scene.control.TextField;
import javafx.scene.control.TextFormatter;
import javafx.util.converter.DateTimeStringConverter;
import sample.controller.MainApp;
import sample.model.Bus;
import sample.model.Category;
import sample.model.Course;
import sample.model.Drive;
import sample.model.Person;
import sample.model.TimeTablePosition;

/**
 * Created by Marek on 2016-12-28.
 */
public class AddingCourseController {

	@FXML
	private TableView<Person> tableWorkers;
	@FXML
	private TableColumn<Person, String> columnWorkers;

	@FXML
	private ChoiceBox<String> choiceBoxDrive;
	@FXML
	private ChoiceBox<String> choiceBoxBusCategory;
	@FXML
	private ChoiceBox<String> choiceBoxTimeTablePosition;
	@FXML
	private ChoiceBox<String> choiceBoxDrivers;
	@FXML
	private Button addDriverBtn;
	@FXML
	private ChoiceBox<String> choiceBoxHostess;
	@FXML
	private Button addHostessBtn;

	@FXML
	private DatePicker datePicker;
	@FXML
	private TextField timePicker;
	@FXML
	private ChoiceBox<String> choiceBoxBuses;
	@FXML
	private Button okBtn;

	private MainApp mainApp;
	private List<Drive> driveList;
	private List<Category> categoryList;
	private List<TimeTablePosition> timeTablePositionList;
	private List<Bus> busList;
	private Course courseData;

	@FXML
	private void initialize() {
		courseData = new Course();

		columnWorkers.setCellValueFactory(cellData -> new SimpleStringProperty(
				cellData.getValue().getName() + " " + cellData.getValue().getSurname()));

		choiceBoxDrive.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue<?> ov, Object t, Object t1) {
				timeTablePositionList = mainApp.getTimeTablePositionsForDrive(
						driveList.get(choiceBoxDrive.getSelectionModel().getSelectedIndex()).getId());
				choiceBoxTimeTablePosition.setItems(
						timeTablePositionList.stream().map(ttp -> ttp.getWeekDay() + " - " + ttp.getLeavingHour())
								.collect(Collectors.toCollection(FXCollections::observableArrayList)));

				courseData.setCourseDrive(driveList.get(choiceBoxDrive.getSelectionModel().getSelectedIndex()));
			}
		});

		choiceBoxTimeTablePosition.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue<?> ov, Object t, Object t1) {
				TimeTablePosition ttp = timeTablePositionList
						.get(choiceBoxTimeTablePosition.getSelectionModel().getSelectedIndex());
				courseData.setTimeTablePosition(ttp);
				datePicker.setDisable(false);
				timePicker.setText(ttp.getLeavingHour());
			}
		});

		choiceBoxBusCategory.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue<?> ov, Object t, Object t1) {
				if (datePicker.getValue() != null) {
					busList = mainApp.getFreeBusesByCategory(courseData,
							categoryList.get(choiceBoxBusCategory.getSelectionModel().getSelectedIndex()));
					choiceBoxBuses.setItems(busList.stream().map(b -> b.getLicensePlate())
							.collect(Collectors.toCollection(FXCollections::observableArrayList)));
				}
				choiceBoxTimeTablePosition.setDisable(false);
			}
		});

		datePicker.valueProperty().addListener(new ChangeListener<LocalDate>() {
			@Override
			public void changed(ObservableValue<? extends LocalDate> arg0, LocalDate arg1, LocalDate arg2) {
				if (datePicker.getValue() == null)
					return;

				String leavingHour = timeTablePositionList
						.get(choiceBoxTimeTablePosition.getSelectionModel().getSelectedIndex()).getLeavingHour();
				String hour = leavingHour.substring(0, leavingHour.indexOf(':'));
				String minute = leavingHour.substring(leavingHour.indexOf(':') + 1, leavingHour.length());

				LocalDate serviceLocalDate = datePicker.getValue();
				Calendar c = Calendar.getInstance();
				c.set(serviceLocalDate.getYear(), serviceLocalDate.getMonthValue() - 1,
						serviceLocalDate.getDayOfMonth());
				c.set(Calendar.HOUR_OF_DAY, Integer.valueOf(hour));
				c.set(Calendar.MINUTE, Integer.valueOf(minute));
				c.set(Calendar.SECOND, 0);

				int day_of_week = c.get(Calendar.DAY_OF_WEEK);
				if (WeekDays
						.valueOf(timeTablePositionList
								.get(choiceBoxTimeTablePosition.getSelectionModel().getSelectedIndex()).getWeekDay())
						.getMask() != day_of_week) {
					Alert alert = new Alert(Alert.AlertType.WARNING);
					alert.initOwner(mainApp.getPrimaryStage());
					alert.setTitle("Wrong weekday");
					alert.setHeaderText("Wrong weekday selected");
					alert.setContentText("Please select proper weekday.");
					alert.showAndWait();

					return;
				}
				courseData.setDate(c);
				busList = mainApp.getFreeBusesByCategory(courseData,
						categoryList.get(choiceBoxBusCategory.getSelectionModel().getSelectedIndex()));

				choiceBoxBuses.setItems(busList.stream().map(b -> b.getLicensePlate())
						.collect(Collectors.toCollection(FXCollections::observableArrayList)));
				choiceBoxDrivers.setItems(
						mainApp.getFreeDrivers(courseData).stream().map(d -> d.getName() + " " + d.getSurname())
								.collect(Collectors.toCollection(FXCollections::observableArrayList)));

				choiceBoxHostess.setItems(
						mainApp.getFreeHostess(courseData).stream().map(h -> h.getName() + " " + h.getSurname())
								.collect(Collectors.toCollection(FXCollections::observableArrayList)));
				choiceBoxBuses.setDisable(false);
				choiceBoxDrivers.setDisable(false);
				choiceBoxHostess.setDisable(false);
			}
		});

		choiceBoxBuses.getSelectionModel().selectedItemProperty().addListener(new ChangeListener<Object>() {
			@Override
			public void changed(ObservableValue<?> ov, Object t, Object t1) {
				courseData.setBus(busList.get(choiceBoxBuses.getSelectionModel().getSelectedIndex()));
			}
		});

		addDriverBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				ObservableList<Person> observableList = tableWorkers.getItems();
				observableList.addAll(mainApp.getFreeDrivers(courseData)
						.get(choiceBoxDrivers.getSelectionModel().getSelectedIndex()));
				tableWorkers.setItems(observableList);
			}
		});

		addHostessBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				ObservableList<Person> observableList = tableWorkers.getItems();
				observableList.addAll(mainApp.getFreeHostess(courseData)
						.get(choiceBoxHostess.getSelectionModel().getSelectedIndex()));
				tableWorkers.setItems(observableList);
			}
		});

		okBtn.setOnAction(new EventHandler<ActionEvent>() {
			@Override
			public void handle(ActionEvent e) {
				List<Person> observableList = tableWorkers.getItems();
				mainApp.insertCourse(courseData, observableList);
			}
		});

	}

	public void setMainApp(MainApp mainApp) {
		this.mainApp = mainApp;
		driveList = mainApp.getDriveData();
		categoryList = mainApp.getCategoryData();

		choiceBoxDrive.setItems(driveList.stream().map(d -> d.getFrom() + " ->" + d.getTo())
				.collect(Collectors.toCollection(FXCollections::observableArrayList)));
		choiceBoxBusCategory.setItems(categoryList.stream().map(c -> c.getShortcut())
				.collect(Collectors.toCollection(FXCollections::observableArrayList)));

		SimpleDateFormat format = new SimpleDateFormat("HH:mm");
		try {
			timePicker
					.setTextFormatter(new TextFormatter<>(new DateTimeStringConverter(format), format.parse("00:00")));
		} catch (ParseException e) {
			mainApp.exceptionDialog(e);
		}
	}

}
