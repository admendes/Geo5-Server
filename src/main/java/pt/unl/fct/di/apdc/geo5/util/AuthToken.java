package pt.unl.fct.di.apdc.geo5.util;

import java.util.UUID;

public class AuthToken {
	
	public static final long EXPIRATION_TIME = 1000*60*60*2; //2h
	
	public String username;
	public String tokenID;
	public String role;
	public long creationData;
	public long expirationData;
	
	public AuthToken() {
		
	}
	
	public AuthToken(String username, String role) {
		this.username = username;
		this.tokenID = UUID.randomUUID().toString();
		this.role = role;
		this.creationData = System.currentTimeMillis();
		this.expirationData = this.creationData + AuthToken.EXPIRATION_TIME;
	}
	
    public AuthToken(String user, String id, long creationData, long expirationData, String role) {
        this.username = user;
        this.tokenID = id;
        this.creationData = creationData;
        this.expirationData = expirationData;
        this.role = role;
    }
    
    public boolean validToken() {
        return validField(username) &&
                validField(tokenID) &&
                validData();
    }
    
    private boolean validField(String value) {
        return value != null && !value.equals("");
    }
    
    private boolean validData() {
        return this.creationData < this.expirationData;
    }
}
