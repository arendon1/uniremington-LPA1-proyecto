package one.austral.lpa1;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import com.fasterxml.jackson.databind.JsonNode;
import com.fasterxml.jackson.databind.ObjectMapper;

import one.austral.lpa1.model.Rol;
import one.austral.lpa1.model.Usuario;
import one.austral.lpa1.repositorio.UsuarioRepositorio;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Auth API (U3): RF1 registro, RF2 login con JWT, y proteccion por rol
 * (USER vs ADMIN) en la API REST.
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PruebasAuthRestTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private UsuarioRepositorio usuarios;

	@Autowired
	private PasswordEncoder passwordEncoder;

	private final ObjectMapper objectMapper = new ObjectMapper();

	private static final String ANA = "ana@test.com";
	private static final String CLAVE = "clave123";

	@BeforeEach
	void prepararDatos() {
		usuarios.deleteAll();
		usuarios.save(new Usuario(ANA, passwordEncoder.encode(CLAVE), "Ana", Rol.USER));
		usuarios.save(new Usuario("boss@test.com", passwordEncoder.encode(CLAVE), "Boss", Rol.ADMIN));
	}

	@Test
	void registrar_usuarioNuevo_devuelve201SinHash() throws Exception {
		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"email": "nuevo@test.com", "password": "secreto1", "nombre": "Nuevo"}
						"""))
			.andExpect(status().isCreated())
			.andExpect(jsonPath("$.email").value("nuevo@test.com"))
			.andExpect(jsonPath("$.rol").value("USER"))
			.andExpect(jsonPath("$.passwordHash").doesNotExist());
	}

	@Test
	void registrar_emailDuplicado_responde409() throws Exception {
		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"email": "ana@test.com", "password": "secreto1", "nombre": "Otra"}
						"""))
			.andExpect(status().isConflict());
	}

	@Test
	void registrar_emailInvalido_responde400() throws Exception {
		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"email": "no-es-email", "password": "secreto1", "nombre": "X"}
						"""))
			.andExpect(status().isBadRequest());
	}

	@Test
	void registrar_passwordCorta_responde400() throws Exception {
		mockMvc.perform(post("/api/auth/register")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"email": "x@test.com", "password": "123", "nombre": "X"}
						"""))
			.andExpect(status().isBadRequest());
	}

	@Test
	void login_conCredencialesCorrectas_devuelveToken() throws Exception {
		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"email": "ana@test.com", "password": "clave123"}
						"""))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.token").isNotEmpty())
			.andExpect(jsonPath("$.rol").value("USER"));
	}

	@Test
	void login_conPasswordIncorrecta_responde401() throws Exception {
		mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("""
						{"email": "ana@test.com", "password": "equivocada"}
						"""))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void endpointProtegido_sinToken_responde401() throws Exception {
		mockMvc.perform(get("/api/admin/usuarios"))
			.andExpect(status().isUnauthorized());
	}

	@Test
	void endpointAdmin_conTokenUser_responde403() throws Exception {
		String token = login(ANA);
		mockMvc.perform(get("/api/admin/usuarios")
				.header("Authorization", "Bearer " + token))
			.andExpect(status().isForbidden())
			.andExpect(jsonPath("$.error").value("Acceso denegado"));
	}

	@Test
	void endpointAdmin_conTokenAdmin_responde200() throws Exception {
		String token = login("boss@test.com");
		mockMvc.perform(get("/api/admin/usuarios")
				.header("Authorization", "Bearer " + token))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(2));
	}

	@Test
	void tokenInvalido_responde401() throws Exception {
		mockMvc.perform(get("/api/admin/usuarios")
				.header("Authorization", "Bearer token-falso"))
			.andExpect(status().isUnauthorized());
	}

	private String login(String email) throws Exception {
		MvcResult resultado = mockMvc.perform(post("/api/auth/login")
				.contentType(MediaType.APPLICATION_JSON)
				.content("{\"email\": \"" + email + "\", \"password\": \"" + CLAVE + "\"}"))
			.andExpect(status().isOk())
			.andReturn();
		JsonNode cuerpo = objectMapper.readTree(resultado.getResponse().getContentAsString());
		return cuerpo.get("token").asText();
	}
}
