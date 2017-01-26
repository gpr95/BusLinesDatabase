package sample.model;

import java.util.Calendar;

public class Course {
	private int id;

	
	private Drive courseDrive;
	private TimeTablePosition timeTablePosition;
	private Bus bus;
	private Calendar date;
	
	
	public void setDate(Calendar cal) {
		this.date = cal;
	}
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public Drive getCourseDrive() {
		return courseDrive;
	}
	public void setCourseDrive(Drive courseDrive) {
		this.courseDrive = courseDrive;
	}
	public TimeTablePosition getTimeTablePosition() {
		return timeTablePosition;
	}
	public void setTimeTablePosition(TimeTablePosition timeTablePosition) {
		this.timeTablePosition = timeTablePosition;
	}
	public Bus getBus() {
		return bus;
	}
	public void setBus(Bus bus) {
		this.bus = bus;
	}
	public Calendar getDate() {
		return date;
	}
	
	
	
}
