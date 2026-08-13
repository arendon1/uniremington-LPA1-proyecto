package one.austral.lpa1.security;

import java.nio.charset.StandardCharsets;
import java.util.Date;

import javax.crypto.SecretKey;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.security.Keys;

import one.austral.lpa1.model.Rol;

/**
 * Emision y validacion de JWT (HS256). El token lleva el email como subject
 * y el rol como claim; la SPA lo envia como "Authorization: Bearer <token>".
 */
@Component
public class JwtServicio {

	private final SecretKey clave;
	private final long horasValidez;

	public JwtServicio(@Value("${app.jwt.secret}") String secreto,
			@Value("${app.jwt.horas:24}") long horasValidez) {
		this.clave = Keys.hmacShaKeyFor(secreto.getBytes(StandardCharsets.UTF_8));
		this.horasValidez = horasValidez;
	}

	public String generar(String email, Rol rol) {
		Date ahora = new Date();
		return Jwts.builder()
				.subject(email)
				.claim("rol", rol.name())
				.issuedAt(ahora)
				.expiration(new Date(ahora.getTime() + horasValidez * 3600_000L))
				.signWith(clave)
				.compact();
	}

	/** Valida firma y expiracion; devuelve el email (subject). */
	public String validar(String token) {
		return Jwts.parser()
				.verifyWith(clave)
				.build()
				.parseSignedClaims(token)
				.getPayload()
				.getSubject();
	}
}
