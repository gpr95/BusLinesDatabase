package sample.model;

public class BusModel {
	private int id;
	private String modelName;
	
	public BusModel(int id , String modelName)
	{
		this.id = id;
		this.modelName = modelName;
	}

	public int getId() {
		return id;
	}

	public String getModelName() {
		return modelName;
	}
}
