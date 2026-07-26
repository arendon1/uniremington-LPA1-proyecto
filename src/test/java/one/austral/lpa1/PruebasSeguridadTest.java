package one.austral.lpa1;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.web.servlet.MockMvc;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;

@SpringBootTest
@AutoConfigureMockMvc
class PruebasSeguridadTest {

	@Autowired
	private MockMvc mockMvc;

	@Test
	void getRaiz_sinAutenticacion_retorna200LandingPublico() throws Exception {
		mockMvc.perform(get("/"))
			.andExpect(status().isOk());
	}

	@Test
	void getPublic_sinAutenticacion_retorna200() throws Exception {
		mockMvc.perform(get("/public"))
			.andExpect(status().isOk());
	}

	@Test
	void getPublic_templateRuta_conTituloYCalloutAnonimo() throws Exception {
		mockMvc.perform(get("/public"))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Pagina publica")))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("/public")))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Sesion anonima")));
	}

	@Test
	void postLogin_adminValido_retorna302() throws Exception {
		mockMvc.perform(SecurityMockMvcRequestBuilders.formLogin()
				.user("admin").password("admin123"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	void postLogin_userValido_retorna302() throws Exception {
		mockMvc.perform(SecurityMockMvcRequestBuilders.formLogin()
				.user("user").password("user123"))
			.andExpect(status().is3xxRedirection());
	}

	@Test
	void postLogin_credencialesInvalidas_redirigeALoginError() throws Exception {
		mockMvc.perform(SecurityMockMvcRequestBuilders.formLogin()
				.user("admin").password("contrasena-mal"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/login?error"));
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	void getUser_conUser_retorna200() throws Exception {
		mockMvc.perform(get("/user"))
			.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	void getUser_conUser_templateRuta_conNombreUsuario() throws Exception {
		mockMvc.perform(get("/user"))
			.andExpect(status().isOk())
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Zona de usuario")))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Hola, <strong>user</strong>")));
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void getUser_conAdmin_retorna200() throws Exception {
		mockMvc.perform(get("/user"))
			.andExpect(status().isOk());
	}

	@Test
	void getUser_sinAuth_redirigeALogin() throws Exception {
		mockMvc.perform(get("/user"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/login"));
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void getAdmin_conAdmin_retorna200() throws Exception {
		mockMvc.perform(get("/admin"))
			.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void getAdmin_conAdmin_templateRuta_conNombreAdmin() throws Exception {
		mockMvc.perform(get("/admin"))
			.andExpect(status().isOk())
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Panel de administracion")))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Hola, <strong>admin</strong>")))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Solo usuarios con rol ADMIN")));
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	void getAdmin_conUser_redirigeAAccessDenied_viaT6() throws Exception {
		mockMvc.perform(get("/admin"))
			.andExpect(status().isOk())
			.andExpect(forwardedUrl("/access-denied"));
	}

	@Test
	void getAdmin_sinAuth_redirigeALogin() throws Exception {
		mockMvc.perform(get("/admin"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/login"));
	}

	@Test
	void getLogin_sinAuth_retorna200() throws Exception {
		mockMvc.perform(get("/login"))
			.andExpect(status().isOk());
	}

	@Test
	void getLogin_conError_retorna200() throws Exception {
		mockMvc.perform(get("/login?error"))
			.andExpect(status().isOk());
	}

	@Test
	void getAccessDenied_retorna200() throws Exception {
		mockMvc.perform(get("/access-denied"))
			.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	void getAdmin_conUser_redirigeAAccessDenied() throws Exception {
		mockMvc.perform(get("/admin"))
			.andExpect(status().isOk())
			.andExpect(forwardedUrl("/access-denied"));
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	void getRaiz_conUser_retorna200() throws Exception {
		mockMvc.perform(get("/"))
			.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void getRaiz_conAdmin_retorna200() throws Exception {
		mockMvc.perform(get("/"))
			.andExpect(status().isOk());
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void postLogout_adminAutenticado_retorna302() throws Exception {
		mockMvc.perform(SecurityMockMvcRequestBuilders.logout())
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/login?logout"));
	}

	@Test
	void getLogin_conLogout_retorna200() throws Exception {
		mockMvc.perform(get("/login?logout"))
			.andExpect(status().isOk());
	}

	@SuppressWarnings("unused")
	private void placeholderParaImportsDeSeguridad() {
		WithMockUser.class.getName();
	}
}
