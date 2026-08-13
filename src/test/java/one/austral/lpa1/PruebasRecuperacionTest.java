package one.austral.lpa1;

import java.time.Instant;
import java.time.temporal.ChronoUnit;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.ArgumentCaptor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.mail.SimpleMailMessage;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.test.web.servlet.MockMvc;

import one.austral.lpa1.model.Rol;
import one.austral.lpa1.model.TokenRecuperacion;
import one.austral.lpa1.model.Usuario;
import one.austral.lpa1.repositorio.TokenRecuperacionRepositorio;
import one.austral.lpa1.repositorio.UsuarioRepositorio;
import one.austral.lpa1.service.RecuperacionContrasenaServicio;

import static org.junit.jupiter.api.Assertions.assertTrue;
import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.never;
import static org.mockito.Mockito.verify;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * RF3: recuperacion de contrasena por correo. El JavaMailSender esta mockeado
 * (en el demo real el correo cae en Mailpit); el test captura el mensaje,
 * extrae el token del enlace y completa el flujo reset + login.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PruebasRecuperacionTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UsuarioRepositorio usuarios;

	@Autowired
	private TokenRecuperacionRepositorio tokens;

	@Autowired
	private PasswordEncoder passwordEncoder;

	@MockitoBean
	private JavaMailSender mailSender;

	private static final String EMAIL = "ana@test.com";
	private static final String CLAVE_ORIGINAL = "clave123";

	@BeforeEach
	void prepararDatos() {
		tokens.deleteAll();
		usuarios.deleteAll();
		usuarios.save(new Usuario(EMAIL, passwordEncoder.encode(CLAVE_ORIGINAL), "Ana", Rol.USER));
	}

	@Test
	void forgot_emailExistente_enviaCorreoConEnlaceDeRecuperacion() throws Exception {
		mockMvc.perform(post("/api/auth/forgot")
				.header("Accept-Language", "es")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"email\": \"" + EMAIL + "\"}"))
			.andExpect(status().isOk());

		ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mailSender).send(captor.capture());
		SimpleMailMessage mensaje = captor.getValue();

		assertTrue(mensaje.getTo()[0].equalsIgnoreCase(EMAIL), "El correo debe ir al usuario");
		assertTrue(mensaje.getSubject().contains("Recuperacion"), "Asunto en es");
		assertTrue(mensaje.getText().contains("http://localhost:5173/recuperar?token="),
				"El enlace debe apuntar a la SPA con el token. Texto real: [" + mensaje.getText() + "]");
	}

	@Test
	void forgot_emailInexistente_responde200SinEnviarCorreo() throws Exception {
		mockMvc.perform(post("/api/auth/forgot")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"email\": \"nadie@test.com\"}"))
			.andExpect(status().isOk());
		verify(mailSender, never()).send(any(SimpleMailMessage.class));
	}

	@Test
	void reset_conTokenValido_cambiaPasswordYLoginFuncionaConLaNueva() throws Exception {
		String token = solicitarYExtraerToken();

		mockMvc.perform(post("/api/auth/reset")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"token\": \"" + token + "\", \"nuevaPassword\": \"nueva123\"}"))
			.andExpect(status().isOk());

		// login con la nueva contrasena funciona
		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"email\": \"" + EMAIL + "\", \"password\": \"nueva123\"}"))
			.andExpect(status().isOk());
		// la anterior dejo de servir
		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"email\": \"" + EMAIL + "\", \"password\": \"" + CLAVE_ORIGINAL + "\"}"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void reset_tokenReutilizado_responde400() throws Exception {
		String token = solicitarYExtraerToken();

		mockMvc.perform(post("/api/auth/reset")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"token\": \"" + token + "\", \"nuevaPassword\": \"nueva123\"}"))
			.andExpect(status().isOk());

		mockMvc.perform(post("/api/auth/reset")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"token\": \"" + token + "\", \"nuevaPassword\": \"otra123\"}"))
			.andExpect(status().isBadRequest());
	}

	@Test
	void reset_tokenInvalido_responde400() throws Exception {
		mockMvc.perform(post("/api/auth/reset")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"token\": \"token-inventado\", \"nuevaPassword\": \"nueva123\"}"))
			.andExpect(status().isBadRequest());
	}

	@Test
	void reset_tokenExpirado_responde400() throws Exception {
		Usuario ana = usuarios.findByEmail(EMAIL).orElseThrow();
		String token = "token-expirado-xyz";
		tokens.save(new TokenRecuperacion(RecuperacionContrasenaServicio.hash(token), ana,
				Instant.now().minus(1, ChronoUnit.MINUTES)));

		mockMvc.perform(post("/api/auth/reset")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"token\": \"" + token + "\", \"nuevaPassword\": \"nueva123\"}"))
			.andExpect(status().isBadRequest());
	}

	private String solicitarYExtraerToken() throws Exception {
		mockMvc.perform(post("/api/auth/forgot")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"email\": \"" + EMAIL + "\"}"))
			.andExpect(status().isOk());

		ArgumentCaptor<SimpleMailMessage> captor = ArgumentCaptor.forClass(SimpleMailMessage.class);
		verify(mailSender).send(captor.capture());
		String texto = captor.getValue().getText();
		int inicio = texto.indexOf("token=") + "token=".length();
		int fin = texto.indexOf('\n', inicio);
		String token = fin < 0 ? texto.substring(inicio) : texto.substring(inicio, fin);
		return token.trim();
	}
}
