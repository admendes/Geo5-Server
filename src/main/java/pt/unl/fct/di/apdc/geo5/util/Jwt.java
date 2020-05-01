package pt.unl.fct.di.apdc.geo5.util;

import static org.junit.Assert.assertTrue;
import java.util.Date;
import org.junit.Test;
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
				.signWith(SignatureAlgorithm.HS512, "MTIzNDU2Nzg=").compact();
		return token;
	}
	
	public String generateJwtToken(AuthToken data) {
		Date expiration = new Date(data.expirationData);
		String token = Jwts.builder().setSubject(data.username)
				.setExpiration(expiration)
				.setIssuer("geo5solutions")
				.claim("token", new AuthToken[] { data })
				// HMAC using SHA-512  and 12345678 base64 encoded
				.signWith(SignatureAlgorithm.HS512, "MTIzNDU2Nzg=").compact();
		return token;
	}

	private void printStructure(String token) {
		Jws parseClaimsJws = Jwts.parser().setSigningKey("MTIzNDU2Nzg=").parseClaimsJws(token);

		System.out.println("Header     : " + parseClaimsJws.getHeader());
		System.out.println("Body       : " + parseClaimsJws.getBody());
		System.out.println("Signature  : " + parseClaimsJws.getSignature());
	}
	
	private void printBody(String token) {
		Claims body = Jwts.parser().setSigningKey("MTIzNDU2Nzg=").parseClaimsJws(token).getBody();

		System.out.println("Issuer     : " + body.getIssuer());
		System.out.println("Subject    : " + body.getSubject());
		System.out.println("Expiration : " + body.getExpiration());
	}

}