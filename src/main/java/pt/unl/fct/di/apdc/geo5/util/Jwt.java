package pt.unl.fct.di.apdc.geo5.util;

import java.util.Base64;
import java.util.Date;
import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import pt.unl.fct.di.apdc.geo5.data.AuthToken;
import pt.unl.fct.di.apdc.geo5.data.JwtData;

public class Jwt {

	public static final String SECRET = "1nc4jRjdO5enfUc4loN3q7gEb8fhr9O";
	
	public Jwt() {
		
	}
	
	public String generateJwtToken(AuthToken data) {
		Date expiration = new Date(data.expirationData);
		byte[] decodedSecret = getDecoded(SECRET);
		String token = Jwts.builder()
				.setSubject(data.username)
				.setExpiration(expiration)
				.setIssuer("geo5solutions")
				.claim("token", data)
				// HMAC using SHA-512  and 12345678 base64 encoded
				.signWith(SignatureAlgorithm.HS512, decodedSecret).compact();
		return token;
	}
	
	public AuthToken getAuthToken(JwtData jData) {
		byte[] decodedSecret = getDecoded(SECRET);
		Claims parseClaimsJws = Jwts.parser().setSigningKey(decodedSecret).parseClaimsJws(jData.token).getBody();
		final ObjectMapper mapper = new ObjectMapper();
		return mapper.convertValue(parseClaimsJws.get("token"), AuthToken.class);
	}
	
	public byte[] getDecoded(String secret) {
		byte[] decodedSecret = Base64.getDecoder().decode(secret);
		return decodedSecret;
	}
	
	public boolean validToken(JwtData jData) {
		AuthToken data = getAuthToken(jData);
		String verification = generateJwtToken(data);
		return jData.token.equals(verification) && data.validToken();
	}
}