package pt.unl.fct.di.apdc.geo5.util;

public class GeoSpotData {
	
	public Pointer location;
	public String username;
	public String name;
	public String description;
	
	public GeoSpotData() {
		
	}
	
	public GeoSpotData(Pointer location, String username, String name, String description) {
		this.location = location;
		this.username = username;
		this.name = name;
		this.description = description;
	}
	
	private boolean validField(String value) {
		return value != null && !value.equals("");
	}
	
	public boolean validRegistration() {
		return validField(username) &&
			   validField(name);	
	}
}
