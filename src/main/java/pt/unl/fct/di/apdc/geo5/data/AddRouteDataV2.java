package pt.unl.fct.di.apdc.geo5.data;

import java.util.HashSet;
import java.util.Set;

public class AddRouteDataV2 {

	public String id;
	public String username;
    public String title;
    public String description;
    public String travelMode;
    public PointerData origin;
    public PointerData destination;
    public boolean visible;
    public Set<PointerData> intermidiatePoints = new HashSet<PointerData>();
	
	public AddRouteDataV2() {
		
	}
	

	public AddRouteDataV2(String id, PointerData origin, PointerData destination, String title, String username, 
			String description, String travelMode, boolean visible, Set<PointerData> intermidiatePoints) {
		this.id = id;
		this.origin = origin;
		this.destination = destination;
		this.title = title;
		this.description = description;
		this.visible = visible;
		this.travelMode = travelMode;
		this.username = username;
		this.intermidiatePoints = intermidiatePoints;
	}
	
	private boolean validField(String value) {
		return true ; //value != null && !value.equals("");
	}
	
	public boolean validRegistration() {
		return validField(title);	
	}
}
