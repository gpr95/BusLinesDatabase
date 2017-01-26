package sample.model;

import javafx.beans.property.SimpleStringProperty;
import javafx.beans.property.StringProperty;

public class Person {

	private int id;
    private final StringProperty name;
    private final StringProperty surname;

    public Person()
    {
        this(0, null,null);
    }

    public Person(int id, String name, String surname)
    {
    	this.id = id;
        this.name = new SimpleStringProperty(name);
        this.surname = new SimpleStringProperty(surname);
    }

	public int getId() {
		return id;
	}

	public void setId(int id) {
		this.id = id;
	}

	public StringProperty nameProperty() {
		return name;
	}
	
	public String getName() {
		return name.get();
	}

	public StringProperty surnameProperty() {
		return surname;
	}
	
	public String getSurname() {
		return surname.get();
	}
}
