package pt.unl.fct.di.apdc.geo5.data;

public class AddRouteData {

	public PointerData start;
	public PointerData end;
	public String routeName;
	public String description;
	public boolean isActive;
	public String token;
	
	public AddRouteData() {
		
	}
	
	public AddRouteData(PointerData start, PointerData end, String name, String username, String description, boolean isActive, String token) {
		this.start = start;
		this.end = end;
		this.routeName = name;
		this.description = description;
		this.isActive = isActive;
		this.token = token;
	}
	
	private boolean validField(String value) {
		return true ; //value != null && !value.equals("");
	}
	
	public boolean validRegistration() {
		return validField(routeName);	
	}
}
