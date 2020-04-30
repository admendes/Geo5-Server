package pt.unl.fct.di.apdc.geo5.util;

public class GeoSpotData {
	
	public Pointer location;
	public String username;
	public String geoSpotName;
	public String description;
	public boolean isActive;
	
	public GeoSpotData() {
		
	}
	
	public GeoSpotData(Pointer location, String username, String geoSpotName, String description, boolean isActive) {
		this.location = location;
		this.username = username;
		this.geoSpotName = geoSpotName;
		this.description = description;
		this.isActive = isActive;
	}
	
	private boolean validField(String value) {
		return value != null && !value.equals("");
	}
	
	public boolean validRegistration() {
		return validField(username) &&
			   validField(geoSpotName);	
	}
}
