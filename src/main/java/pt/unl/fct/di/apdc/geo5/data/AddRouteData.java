package pt.unl.fct.di.apdc.geo5.data;

public class AddRouteData {
<<<<<<< HEAD

	public PointerData start;
	public PointerData end;
	public String routeName;
	public String description;
=======
	public String username;
    public String title;
    public String description;
    public String travelMode;
    public PointerData origin;
    public PointerData destination;
    public boolean visible;
>>>>>>> e7ddf119c0ae4ea19d5a0b5badb95a05c20555ca
	
	public AddRouteData() {
		
	}
	
<<<<<<< HEAD
	public AddRouteData(PointerData start, PointerData end, String name, String username, String description) {
		this.start = start;
		this.end = end;
		this.routeName = name;
		this.description = description;
=======
	public AddRouteData(PointerData origin, PointerData destination, String title, String username, String description, boolean visible) {
		this.origin = origin;
		this.destination = destination;
		this.title = title;
		this.description = description;
		this.visible = visible;
>>>>>>> e7ddf119c0ae4ea19d5a0b5badb95a05c20555ca
	}
	
	private boolean validField(String value) {
		return true ; //value != null && !value.equals("");
	}
	
	public boolean validRegistration() {
		return validField(title);	
	}
}
