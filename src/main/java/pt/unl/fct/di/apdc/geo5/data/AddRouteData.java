package pt.unl.fct.di.apdc.geo5.data;

public class AddRouteData {
	public String username;
    public String title;
    public String description;
    public String travelMode;
    public PointerData origin;
    public PointerData destination;
    public boolean visible;
	
	public AddRouteData() {
		
	}
	
	public AddRouteData(PointerData origin, PointerData destination, String title, String username, String description, boolean visible) {
		this.origin = origin;
		this.destination = destination;
		this.title = title;
		this.description = description;
		this.visible = visible;
	}
	
	private boolean validField(String value) {
		return true ; //value != null && !value.equals("");
	}
	
	public boolean validRegistration() {
		return validField(title);	
	}
}
