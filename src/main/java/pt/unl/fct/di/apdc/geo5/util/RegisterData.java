package pt.unl.fct.di.apdc.geo5.util;

public class RegisterData {

	public String username;
	public String name;
	public String email;	
	public String password;

	
	public RegisterData() {
		
	}
	
	public RegisterData(String username, String name, String email, String password) {
		
			this.username = username;
			this.name = name;
			this.email = email;
			this.password = password;
	}
	
	private boolean validField(String value) {
		return value != null && !value.equals("");
	}
	
	public boolean validRegistration() {
		return validField(username) &&
			   validField(name) &&
			   validField(email) &&
			   validField(password) &&
			   email.contains("@");		
	}

}
