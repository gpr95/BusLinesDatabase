package sample.model;

import javafx.beans.property.FloatProperty;
import javafx.beans.property.IntegerProperty;
import javafx.beans.property.SimpleFloatProperty;
import javafx.beans.property.SimpleIntegerProperty;
import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

import java.util.*;

/**
 * Created by Marek on 2016-12-25.
 */
public class Drive {

	private int id;
    private final StringProperty from;
    private final StringProperty to;
    private final IntegerProperty time;
    private final IntegerProperty distance;
    private final FloatProperty price;
    private List<IntermediateDrive> listOfIntermediateDrive;

    public Drive()
    {
        this(-1, null,null);
    }

    public Drive(int id, String from, String to)
    {
    	this.id = id;
        this.from = new SimpleStringProperty(from);
        this.to = new SimpleStringProperty(to);
        this.time = new SimpleIntegerProperty(0);
        this.distance = new SimpleIntegerProperty(0);
        this.price = new SimpleFloatProperty(0f);
        this.listOfIntermediateDrive = null;
    }

    public Drive(int id, String from, String to, int time, int distance, float price)
    {
    	this.id = id;
        this.from = new SimpleStringProperty(from);
        this.to = new SimpleStringProperty(to);
        this.time = new SimpleIntegerProperty(time);
        this.distance = new SimpleIntegerProperty(distance);
        this.price = new SimpleFloatProperty(price);
        this.listOfIntermediateDrive = null;
    }

    public Drive(int id, String from, String to, int time, int distance, float price, List<IntermediateDrive> listOfIntermediateDrive)
    {
    	this.id = id;
        this.from = new SimpleStringProperty(from);
        this.to = new SimpleStringProperty(to);
        this.time = new SimpleIntegerProperty(time);
        this.distance = new SimpleIntegerProperty(distance);
        this.price = new SimpleFloatProperty(price);
        this.listOfIntermediateDrive = new ArrayList<>(listOfIntermediateDrive);
    }

    @Override
    public String toString()
    {
     return "Departure from: " + getFrom() + "; \t To: " + getTo();
    }

    public String getFrom() {
        return from.get();
    }

    public StringProperty fromProperty() {
        return from;
    }

    public void setFrom(String from) {
        this.from.set(from);
    }

    public String getTo() {
        return to.get();
    }

    public StringProperty toProperty() {
        return to;
    }

    public void setTo(String to) {
        this.to.set(to);
    }

    public int getTime() {
        return time.get();
    }

    public IntegerProperty timeProperty() {
        return time;
    }

    public void setTime(int time) {
        this.time.set(time);
    }

    public int getDistance() {
        return distance.get();
    }

    public IntegerProperty distanceProperty() {
        return distance;
    }

    public void setDistance(int distance) {
        this.distance.set(distance);
    }

    public float getPrice() {
        return price.get();
    }

    public FloatProperty priceProperty() {
        return price;
    }

    public void setPrice(float price) {
        this.price.set(price);
    }

    public List<IntermediateDrive> getListOfIntermediateDrive() {
        return listOfIntermediateDrive;
    }

    public void setListOfIntermediateDrive(List<IntermediateDrive> intermediateDrive) {
        this.listOfIntermediateDrive = new ArrayList<>(intermediateDrive);
    }

	public int getId() {
		return id;
	}

}
