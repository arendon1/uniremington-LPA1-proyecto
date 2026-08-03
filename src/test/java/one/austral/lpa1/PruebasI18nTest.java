package one.austral.lpa1;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import jakarta.servlet.http.Cookie;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.http.MediaType;
import org.springframework.security.test.context.support.WithMockUser;
import org.springframework.security.test.web.servlet.request.SecurityMockMvcRequestBuilders;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.MvcResult;

import static org.hamcrest.Matchers.containsString;
import static org.hamcrest.Matchers.not;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.content;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.forwardedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.redirectedUrl;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * Suite i18n + l10n + seguridad de U2.
 *
 * Aserciones RESILIENTES al rediseño: no afirman strings de marca ni de nombres
 * de producto (que cambian), sino estructura, conteos, comportamiento de switch
 * de locale, formato numerico de precios y comportamiento de seguridad. Si el
 * rediseño cambia la marca o los items, estos tests siguen verde sin tocarlos.
 */
@SpringBootTest
@AutoConfigureMockMvc
class PruebasI18nTest {

	@Autowired
	private MockMvc mockMvc;

	private static final String[] IDIOMAS = { "es", "en", "pt" };
	private static final String[] RUTAS_PUBLICAS = { "/", "/public", "/login", "/access-denied" };

	private static final Pattern SIN_CLAVES_RESUELTAS = Pattern.compile("\\?\\?[a-zA-Z._]+\\?\\?");

	/** 1. Matriz 3 idiomas x 4 rutas publicas: 200, text/html, sin artefactos ??clave??. */
	@Test
	void i18nLocaleMatrix_todaRutaPublica_resuelveClavesEnCadaIdioma() throws Exception {
		for (String lang : IDIOMAS) {
			for (String ruta : RUTAS_PUBLICAS) {
				MvcResult r = mockMvc.perform(get(ruta).param("lang", lang))
					.andExpect(status().isOk())
					.andExpect(content().contentTypeCompatibleWith(MediaType.TEXT_HTML))
					.andReturn();
				String cuerpo = r.getResponse().getContentAsString();
				Matcher m = SIN_CLAVES_RESUELTAS.matcher(cuerpo);
				if (m.find()) {
					throw new AssertionError(
						"Ruta " + ruta + " con lang=" + lang
						+ " contiene clave no resuelta: " + m.group());
				}
			}
		}
	}

	/** 2. El switcher aparece con los 3 idiomas en tienda, login y denied. */
	@Test
	void switcherPresente_tresIdiomasEnCadaVistaPublica() throws Exception {
		for (String ruta : new String[] { "/", "/login", "/access-denied" }) {
			mockMvc.perform(get(ruta))
				.andExpect(content().string(containsString("lang=es")))
				.andExpect(content().string(containsString("lang=en")))
				.andExpect(content().string(containsString("lang=pt")));
		}
	}

	/** 3. Cambiar de idioma cambia el contenido renderizado. */
	@Test
	void cambioLocaleCambiaContenido_esDiferenteDeEnYdePt() throws Exception {
		String es = cuerpo("/", "es");
		String en = cuerpo("/", "en");
		String pt = cuerpo("/", "pt");
		if (es.equals(en)) {
			throw new AssertionError("El contenido de /?lang=es es identico a /?lang=en: el switch no cambio el render.");
		}
		if (es.equals(pt)) {
			throw new AssertionError("El contenido de /?lang=es es identico a /?lang=pt.");
		}
	}

	/** 4. El catalogo renderiza exactamente 10 items (cuenta la clase del boton Agregar). */
	@Test
	void catalogoRenderiza10Items_enTienda() throws Exception {
		String cuerpo = cuerpo("/", "es");
		long cuenta = contarOcurrencias(cuerpo, "producto-agregar");
		if (cuenta != 10) {
			throw new AssertionError(
				"Se esperaban 10 items en el catalogo, se encontraron " + cuenta
				+ " (marcador 'producto-agregar'). Si el rediseño cambio la clase del boton, "
				+ "actualizar este marcador.");
		}
	}

	/** 5. l10n de precios: separador de miles por locale (es/pt punto, en coma). */
	@Test
	void precioL10nSeparador_puntoParaEsYPtComaParaEn() throws Exception {
		String es = cuerpo("/", "es");
		String en = cuerpo("/", "en");
		String pt = cuerpo("/", "pt");
		if (!Pattern.compile("COP \\d{1,3}\\.\\d{3}").matcher(es).find()) {
			throw new AssertionError("Precio en es no usa punto como separador de miles: " + extraerPrecios(es));
		}
		if (!Pattern.compile("COP \\d{1,3},\\d{3}").matcher(en).find()) {
			throw new AssertionError("Precio en en no usa coma como separador de miles: " + extraerPrecios(en));
		}
		if (!Pattern.compile("COP \\d{1,3}\\.\\d{3}").matcher(pt).find()) {
			throw new AssertionError("Precio en pt no usa punto como separador de miles: " + extraerPrecios(pt));
		}
	}

	/** 6. El locale en cookie sobrevive al ciclo login/logout (CookieLocaleResolver vs Session). */
	@Test
	void cookieLocalePersisteTrasLogout() throws Exception {
		MvcResult ptInicial = mockMvc.perform(get("/").param("lang", "pt"))
			.andExpect(status().isOk())
			.andReturn();
		Cookie cookie = cookieLang(ptInicial);
		if (cookie == null || !"pt".equals(cookie.getValue())) {
			throw new AssertionError(
				"GET /?lang=pt no seteo cookie de locale a pt. Cookie: "
				+ (cookie == null ? "null" : cookie.getName() + "=" + cookie.getValue()));
		}

		mockMvc.perform(SecurityMockMvcRequestBuilders.formLogin()
				.user("admin").password("admin123"))
			.andExpect(status().is3xxRedirection());

		mockMvc.perform(SecurityMockMvcRequestBuilders.logout())
			.andExpect(status().is3xxRedirection());

		MvcResult trasLogout = mockMvc.perform(get("/").cookie(cookie))
			.andExpect(status().isOk())
			.andReturn();
		String cuerpo = trasLogout.getResponse().getContentAsString();
		if (SIN_CLAVES_RESUELTAS.matcher(cuerpo).find()) {
			throw new AssertionError("Tras logout con cookie pt, la pagina tiene claves no resueltas.");
		}
		String es = cuerpo("/", "es");
		if (cuerpo.equals(es)) {
			throw new AssertionError(
				"Tras logout, la pagina con cookie pt es identica a /?lang=es: "
				+ "el locale no persistio en la cookie.");
		}
	}

	/** 7. Seguridad intacta: /user exige auth, /admin exige ADMIN, USER -> denied. */
	@Test
	void seguridadIntacta_rutasProtegidas() throws Exception {
		mockMvc.perform(get("/user"))
			.andExpect(status().is3xxRedirection())
			.andExpect(redirectedUrl("/login"));
	}

	@Test
	@WithMockUser(username = "user", roles = "USER")
	void seguridadIntacta_userEnAdmin_vaAAccessDenied() throws Exception {
		mockMvc.perform(get("/admin"))
			.andExpect(status().isOk())
			.andExpect(forwardedUrl("/access-denied"));
	}

	@Test
	@WithMockUser(username = "admin", roles = "ADMIN")
	void seguridadIntacta_adminEnAdmin_retorna200() throws Exception {
		mockMvc.perform(get("/admin"))
			.andExpect(status().isOk());
	}

	// ---- helpers ----

	private String cuerpo(String ruta, String lang) throws Exception {
		return mockMvc.perform(get(ruta).param("lang", lang))
			.andExpect(status().isOk())
			.andReturn()
			.getResponse()
			.getContentAsString();
	}

	private static long contarOcurrencias(String texto, String sub) {
		long cuenta = 0;
		int desde = 0;
		while (true) {
			int i = texto.indexOf(sub, desde);
			if (i < 0) break;
			cuenta++;
			desde = i + sub.length();
		}
		return cuenta;
	}

	private static final String NOMBRE_COOKIE_LOCALE =
		"org.springframework.web.servlet.i18n.CookieLocaleResolver.LOCALE";

	private static Cookie cookieLang(MvcResult r) {
		for (Cookie c : r.getResponse().getCookies()) {
			if (NOMBRE_COOKIE_LOCALE.equals(c.getName())) return c;
		}
		return null;
	}

	private static String extraerPrecios(String cuerpo) {
		StringBuilder sb = new StringBuilder();
		Matcher m = Pattern.compile("COP [0-9.,]+").matcher(cuerpo);
		while (m.find()) sb.append(m.group()).append(' ');
		return sb.toString().trim();
	}
}
