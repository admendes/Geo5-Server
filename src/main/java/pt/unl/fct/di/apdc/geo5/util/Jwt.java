package pt.unl.fct.di.apdc.geo5.util;

import static org.junit.Assert.assertTrue;
import java.util.Date;
import org.junit.Test;

import com.fasterxml.jackson.databind.ObjectMapper;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class Jwt {

	
	public Jwt() {
		
	}
	
	@Test
	public void testJWT() {
		String token = generateJwtTokenTest();
		assertTrue(token != null);
		System.out.println(token);
		printStructure(token);
		printBody(token);
	}
	
	@SuppressWarnings("deprecation")
	private String generateJwtTokenTest() {
		String token = Jwts.builder().setSubject("adam")
				.setExpiration(new Date(2018, 1, 1))
				.setIssuer("info@wstutorial.com")
				.claim("groups", new String[] { "user", "admin" })
				// HMAC using SHA-512  and 12345678 base64 encoded
				.signWith(SignatureAlgorithm.HS512, "1nc4jRjdO5enfUc4loN3q7gEb8fhr9O").compact();
		return token;
	}
	
	public String generateJwtToken(AuthToken data) {
		Date expiration = new Date(data.expirationData);
		String token = Jwts.builder().setSubject(data.username)
				.setExpiration(expiration)
				.setIssuer("geo5solutions")
				.claim("token", data)
				// HMAC using SHA-512  and 12345678 base64 encoded
				.signWith(SignatureAlgorithm.HS512, "1nc4jRjdO5enfUc4loN3q7gEb8fhr9O").compact();
		return token;
	}
	
	public void printStructure(String token) {
		Jws<Claims> parseClaimsJws = Jwts.parser().setSigningKey("1nc4jRjdO5enfUc4loN3q7gEb8fhr9O").parseClaimsJws(token);
				
		System.out.println("Header     : " + parseClaimsJws.getHeader());
		System.out.println("Body       : " + parseClaimsJws.getBody());
		System.out.println("Signature  : " + parseClaimsJws.getSignature());
	}
	
	
	public void printBody(String token) {
		Claims body = Jwts.parser().setSigningKey("MTIzNDU2Nzg=").parseClaimsJws(token).getBody();

		System.out.println("Issuer     : " + body.getIssuer());
		System.out.println("Subject    : " + body.getSubject());
		System.out.println("Expiration : " + body.getExpiration());
	}
	
	public AuthToken getAuthToken(String token) {
		Claims parseClaimsJws = Jwts.parser().setSigningKey("1nc4jRjdO5enfUc4loN3q7gEb8fhr9O").parseClaimsJws(token).getBody();
		final ObjectMapper mapper = new ObjectMapper();
		return mapper.convertValue(parseClaimsJws.get("token"), AuthToken.class);
	}
}