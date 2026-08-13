package one.austral.lpa1.service;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.security.SecureRandom;
import java.time.Instant;
import java.time.temporal.ChronoUnit;
import java.util.Base64;
import java.util.Locale;

import org.springframework.context.MessageSource;
import org.springframework.http.HttpStatus;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import one.austral.lpa1.model.TokenRecuperacion;
import one.austral.lpa1.model.Usuario;
import one.austral.lpa1.repositorio.TokenRecuperacionRepositorio;
import one.austral.lpa1.repositorio.UsuarioRepositorio;

/**
 * RF3: recuperacion de contrasena por correo.
 *
 * Seguridad: token de 32 bytes (SecureRandom) que viaja solo en el correo;
 * en la BD se guarda su hash SHA-256; expira en 30 minutos y es de un solo
 * uso. La respuesta de /forgot es 200 siempre, exista o no el email, para no
 * revelar que emails estan registrados (anti-enumeracion).
 */
@Service
public class RecuperacionContrasenaServicio {

	static final long MINUTOS_VALIDEZ = 30;

	private final UsuarioRepositorio usuarios;
	private final TokenRecuperacionRepositorio tokens;
	private final PasswordEncoder passwordEncoder;
	private final JavaMailSender mailSender;
	private final MessageSource messageSource;
	private final SecureRandom aleatorio = new SecureRandom();

	public RecuperacionContrasenaServicio(UsuarioRepositorio usuarios,
			TokenRecuperacionRepositorio tokens, PasswordEncoder passwordEncoder,
			JavaMailSender mailSender, MessageSource messageSource) {
		this.usuarios = usuarios;
		this.tokens = tokens;
		this.passwordEncoder = passwordEncoder;
		this.mailSender = mailSender;
		this.messageSource = messageSource;
	}

	/** Genera el token, guarda su hash y envia el correo. 200 siempre. */
	public void solicitar(String email, Locale locale) {
		usuarios.findByEmail(email.trim().toLowerCase()).ifPresent(usuario -> {
			String token = nuevoToken();
			tokens.save(new TokenRecuperacion(hash(token), usuario,
					Instant.now().plus(MINUTOS_VALIDEZ, ChronoUnit.MINUTES)));
			enviarCorreo(usuario, token, locale);
		});
	}

	/** Valida token (existe, vigente, sin usar) y cambia la contrasena. */
	public void restablecer(String token, String nuevaPassword) {
		TokenRecuperacion registro = tokens.findByTokenHash(hash(token))
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.BAD_REQUEST,
						"Token invalido o expirado"));
		if (registro.isUsado() || registro.getExpiraEn().isBefore(Instant.now())) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Token invalido o expirado");
		}
		Usuario usuario = registro.getUsuario();
		usuario.cambiarPassword(passwordEncoder.encode(nuevaPassword));
		usuarios.save(usuario);
		registro.marcarUsado();
		tokens.save(registro);
	}

	private String nuevoToken() {
		byte[] bytes = new byte[32];
		aleatorio.nextBytes(bytes);
		return Base64.getUrlEncoder().withoutPadding().encodeToString(bytes);
	}

	public static String hash(String token) {
		try {
			MessageDigest digest = MessageDigest.getInstance("SHA-256");
			return Base64.getUrlEncoder().withoutPadding()
					.encodeToString(digest.digest(token.getBytes(StandardCharsets.UTF_8)));
		} catch (NoSuchAlgorithmException e) {
			throw new IllegalStateException("SHA-256 no disponible", e);
		}
	}

	private void enviarCorreo(Usuario usuario, String token, Locale locale) {
		String enlace = "http://localhost:5173/recuperar?token=" + token;
		String intro = messageSource.getMessage("mail.recuperacion.cuerpo.intro", null, locale);
		String pie = messageSource.getMessage("mail.recuperacion.cuerpo.pie", null, locale);
		SimpleMailMessage mensaje = new SimpleMailMessage();
		mensaje.setTo(usuario.getEmail());
		mensaje.setSubject(messageSource.getMessage("mail.recuperacion.asunto", null, locale));
		mensaje.setText(intro + "\n" + enlace + "\n\n" + pie);
		mailSender.send(mensaje);
	}
}
