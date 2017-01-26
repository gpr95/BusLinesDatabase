package sample.model;

import java.util.Calendar;

public class GraphicPosition {
	private int ID;
	private int hostessId;
	private int driverId;
	private int courseId;
	private Calendar timeFrom;
	private Calendar timeTo;
	private String status;
	private float hourlyBid;
	public GraphicPosition(int iD, int hostessId, int driverId, int courseId, Calendar timeFrom, Calendar timeTo,
			String status, float hourlyBid) {
		super();
		ID = iD;
		this.hostessId = hostessId;
		this.driverId = driverId;
		this.courseId = courseId;
		this.timeFrom = timeFrom;
		this.timeTo = timeTo;
		this.status = status;
		this.hourlyBid = hourlyBid;
	}
	public int getId() {
		return ID;
	}
	public void setID(int iD) {
		ID = iD;
	}
	public int getHostessId() {
		return hostessId;
	}
	public void setHostessId(int hostessId) {
		this.hostessId = hostessId;
	}
	public int getDriverId() {
		return driverId;
	}
	public void setDriverId(int driverId) {
		this.driverId = driverId;
	}
	public int getCourseId() {
		return courseId;
	}
	public void setCourseId(int courseId) {
		this.courseId = courseId;
	}
	public Calendar getTimeFrom() {
		return timeFrom;
	}
	public void setTimeFrom(Calendar timeFrom) {
		this.timeFrom = timeFrom;
	}
	public Calendar getTimeTo() {
		return timeTo;
	}
	public void setTimeTo(Calendar timeTo) {
		this.timeTo = timeTo;
	}
	public String getStatus() {
		return status;
	}
	public void setStatus(String status) {
		this.status = status;
	}
	public float getHourlyBid() {
		return hourlyBid;
	}
	public void setHourlyBid(float hourlyBid) {
		this.hourlyBid = hourlyBid;
	}
	
}
