package pt.unl.fct.di.apdc.geo5.util;

public class Pointer {
	
	public long lat;
	public long lon;
	public String name;
	
	public Pointer() {
		
	}
	
	public Pointer(long lat, long lon, String name) {
		this.lat = lat;
		this.lon = lon;
		this.name = name;
	}

}
