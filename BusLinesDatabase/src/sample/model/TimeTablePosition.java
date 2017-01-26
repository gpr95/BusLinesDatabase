package sample.model;

public class TimeTablePosition {
	private int id;
	private int driveId;
	private String weekDay;
	private String leavingHour;
	private String registerPhone;
	private float timeCategoryRate;

	public TimeTablePosition(int id, int driveId, String weekDay, String leavingHour, String registerPhone,
			float timeCategoryRate) {
		this.id = id;
		this.driveId = driveId;
		this.weekDay = weekDay;
		this.leavingHour = leavingHour;
		this.registerPhone = registerPhone;
		this.timeCategoryRate = timeCategoryRate;
	}

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public int getDriveId() {
		return driveId;
	}

	public void setDriveId(int driveId) {
		this.driveId = driveId;
	}

	public String getWeekDay() {
		return weekDay;
	}

	public void setWeekDay(String weekDay) {
		this.weekDay = weekDay;
	}

	public String getLeavingHour() {
		return leavingHour;
	}

	public void setLeavingHour(String leavingHour) {
		this.leavingHour = leavingHour;
	}

	public String getRegisterPhone() {
		return registerPhone;
	}

	public void setRegisterPhone(String registerPhone) {
		this.registerPhone = registerPhone;
	}

	public float getTimeCategoryRate() {
		return timeCategoryRate;
	}

	public void setTimeCategoryRate(float timeCategoryRate) {
		this.timeCategoryRate = timeCategoryRate;
	}
}
