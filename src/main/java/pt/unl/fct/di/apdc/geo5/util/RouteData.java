package pt.unl.fct.di.apdc.geo5.util;

public class RouteData {

	public Pointer start;
	public Pointer end;
	public String name;
	public String username;
	public String description;
	public boolean isActive;
	
	public RouteData() {
		
	}
	
	public RouteData(Pointer start, Pointer end, String name, String username, String description) {
		this.start = start;
		this.end = end;
		this.name = name;
		this.username = username;
		this.description = description;
		this.isActive = true;
	}
	
	private boolean validField(String value) {
		return value != null && !value.equals("");
	}
	
	public boolean validRegistration() {
		return validField(username) &&
			   validField(name);	
	}
}
