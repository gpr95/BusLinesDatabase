package sample.model;

import javafx.beans.property.*;

import java.sql.Date;

/**
 * Created by Marek on 2016-12-25.
 */
public class Bus {

    private final int busId;  
    private final IntegerProperty busModelId;
    private Date dateOfBuy;
    private final StringProperty licensePlate;
    private final StringProperty sereialNumber;
    private final IntegerProperty seats;
    private final IntegerProperty mileage;
    private final FloatProperty classRate;
    private final StringProperty busModelName;
    
    public Bus(){this(null);}

    public Bus(String licensePlate){
        this.licensePlate = new SimpleStringProperty(licensePlate);
        this.busModelId = new SimpleIntegerProperty(-1);
        this.busId = -1;
        this.dateOfBuy = new Date(0); 
        this.sereialNumber = new SimpleStringProperty("null");
        this.seats = new SimpleIntegerProperty(0);
        this.mileage = new SimpleIntegerProperty(0);
        this.classRate = new SimpleFloatProperty(0f);
        this.busModelName = new SimpleStringProperty("null");
    }

    public Bus(int id, int busModelId, Date date, String licensePlate, String serialNumber, int seats,
    		int mileage, float classRate,String modelName){

        this.busId = id;
        this.busModelId = new SimpleIntegerProperty(busModelId);
        this.dateOfBuy = new Date(date.getTime());
        this.licensePlate = new SimpleStringProperty(licensePlate);
        this.sereialNumber = new SimpleStringProperty(serialNumber);
        this.seats = new SimpleIntegerProperty(seats);
        this.mileage = new SimpleIntegerProperty(mileage);
        this.classRate = new SimpleFloatProperty(classRate);
        this.busModelName = new SimpleStringProperty(modelName);
    }

    @Override
    public String toString() {return licensePlate.get(); }

    public int getMileage() {
        return mileage.get();
    }

    public IntegerProperty mileageProperty() {
        return mileage;
    }

    public void setMileage(int mileage) {
        this.mileage.set(mileage);
    }

    public int getBusId() {
        return busId;
    }

    public float getClassRate() {
        return classRate.get();
    }

    public FloatProperty classRateProperty() {
        return classRate;
    }

    public void setClassRate(float category) {
        this.classRate.set(category);
    }

    public int getBusModelId() {
        return busModelId.get();
    }
    
    public void setBusModelId(int busModelId) {
    	this.busModelId.set(busModelId);
    }

    public Date getDateOfBuy() {
        return dateOfBuy;
    }

    public String getLicensePlate() {
        return licensePlate.get();
    }

    public StringProperty licensePlateProperty() {
        return licensePlate;
    }

    public void setLicensePlate(String licensePlate) {
        this.licensePlate.set(licensePlate);
    }

    public String getSereialNumber() {
        return sereialNumber.get();
    }

    public StringProperty sereialNumberProperty() {
        return sereialNumber;
    }

    public void setSereialNumber(String sereialNumber) {
        this.sereialNumber.set(sereialNumber);
    }

    public int getSeats() {
        return seats.get();
    }

    public IntegerProperty seatsProperty() {
        return seats;
    }

    public void setSeats(int seats) {
        this.seats.set(seats);
    }

	public StringProperty busModelNameProperty() {
		return busModelName;
	}
	
	public void setModelName(String modelName) {
		this.busModelName.set(modelName);
	}
	
	public String getModelName() {
		return busModelName.get();
	}

    public void setDateOfBuy(Date date) {
        dateOfBuy = date;
    }
}
