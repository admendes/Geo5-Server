package pt.unl.fct.di.apdc.geo5.util;

public class RegisterData {

	public String username;
	public String name;
	public String email;	
	public String password;
	public String confirmation;
	public String street;
	public String place;
	public String country;
	public String role;
	public boolean isActive;
	
	public RegisterData() {
		
	}
	
	public RegisterData(String username, String name, String email, String password, String confirmation, String role, String street, String place, String country) {
		
			this.username = username;
			this.name = name;
			this.email = email;
			this.password = password;
			this.confirmation = confirmation;
			this.role = role;
			this.street = street;
			this.place = place;
			this.country = country;
			this.isActive = true;
	}
	
	private boolean validField(String value) {
		return value != null && !value.equals("");
	}
	
	public boolean validRegistration() {
		return validField(username) &&
			   validField(name) &&
			   validField(email) &&
			   validField(password) &&
			   validField(confirmation) &&
			   password.equals(confirmation) &&
			   email.contains("@");		
	}

}
