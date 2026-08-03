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
	void getPublic_muestraTienda_conCatalogoEnEspanol() throws Exception {
		mockMvc.perform(get("/public"))
			.andExpect(status().isOk())
			.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Lo m\u00E1s pedido")))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Bandeja paisa")))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("/public")));
	}

	@Test
	void getImagenProducto_sinAutenticacion_retorna200() throws Exception {
		// Regression: en Boot 4, PathRequest.toStaticResources().atCommonLocations()
		// solo cubre /images/** (StaticResourceLocation.IMAGES), no /img/. Los assets
		// de imagenes deben servirse a usuarios anonimos o la tienda queda sin fotos.
		// Tras el cambio de SVG a JPG+WebP, la imagen canonica es .jpg con fallback
		// .webp via <picture>; el test verifica que el recurso se sirve y devuelve
		// el content-type correcto a usuarios anonimos.
		mockMvc.perform(get("/images/productos/hamburguesa-clasica.jpg"))
			.andExpect(status().isOk())
			.andExpect(content().contentType("image/jpeg"));
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
	void getUser_muestraCuenta_conSaludoEnEspanol() throws Exception {
		mockMvc.perform(get("/user"))
			.andExpect(status().isOk())
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Mi cuenta")))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Hola, user")));
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
	void getAdmin_muestraPanel_conSaludoEnEspanol() throws Exception {
		mockMvc.perform(get("/admin"))
			.andExpect(status().isOk())
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Panel de administraci\u00F3n")))
			.andExpect(content().string(org.hamcrest.Matchers.containsString("Conectado como admin")));
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
}
