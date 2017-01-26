package sample.model;

public class Category {
	private int id;
	private String shortcut;
	private String description;
	private float priceRate;
	public Category(int id, String shortcut, String description, float priceRate) {
		this.id = id;
		this.shortcut = shortcut;
		this.description = description;
		this.priceRate = priceRate;
	}
	
	public int getId() {
		return id;
	}
	public void setId(int id) {
		this.id = id;
	}
	public String getShortcut() {
		return shortcut;
	}
	public void setShortcut(String shortcut) {
		this.shortcut = shortcut;
	}
	public String getDescription() {
		return description;
	}
	public void setDescription(String description) {
		this.description = description;
	}
	public float getPriceRate() {
		return priceRate;
	}
	public void setPriceRate(float priceRate) {
		this.priceRate = priceRate;
	}
}
