package one.austral.lpa1;

import java.util.List;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.webmvc.test.autoconfigure.AutoConfigureMockMvc;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.web.servlet.MockMvc;

import one.austral.lpa1.model.Categoria;
import one.austral.lpa1.model.Producto;
import one.austral.lpa1.repositorio.ProductoRepositorio;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

/**
 * API REST de catalogo (U3): RF4 (lista por categoria), RF5 (detalle),
 * RF6 (busqueda). Corre contra PostgreSQL real (perfil test, schema limpio).
 */
@SpringBootTest
@AutoConfigureMockMvc
@ActiveProfiles("test")
class PruebasCatalogoRestTest {

	@Autowired
	private MockMvc mockMvc;

	@Autowired
	private ProductoRepositorio repositorio;

	@BeforeEach
	void prepararDatos() {
		repositorio.deleteAll();
		repositorio.saveAll(List.of(
			new Producto("hamburguesa-clasica", "Hamburguesa clasica",
					"Carne 150 g, queso cheddar.", 18000, "unidad", 60,
					Categoria.HAMBURGUESAS, "/images/productos/hamburguesa-clasica.jpg"),
			new Producto("bandeja-paisa", "Bandeja paisa",
					"Arroz, frijoles y platano.", 28000, "porcion", 40,
					Categoria.COMIDAS, "/images/productos/bandeja-paisa.jpg"),
			new Producto("gaseosa-colombiana", "Gaseosa colombiana",
					"Botella 500 ml bien fria.", 5000, "botella", 100,
					Categoria.BEBIDAS, "/images/productos/gaseosa-colombiana.svg"),
			new Producto("brownie-chocolate", "Brownie de chocolate",
					"Con nuez y helado de vainilla.", 12000, "unidad", 50,
					Categoria.POSTRES, "/images/productos/brownie-chocolate.svg")));
	}

	@Test
	void listaCompleta_devuelveTodosLosProductos() throws Exception {
		mockMvc.perform(get("/api/productos"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(4))
			.andExpect(jsonPath("$[?(@.slug == 'hamburguesa-clasica')]").exists())
			.andExpect(jsonPath("$[?(@.slug == 'brownie-chocolate')]").exists());
	}

	@Test
	void filtroPorCategoria_devuelveSoloEsaCategoria() throws Exception {
		mockMvc.perform(get("/api/productos").param("categoria", "HAMBURGUESAS"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(1))
			.andExpect(jsonPath("$[0].slug").value("hamburguesa-clasica"))
			.andExpect(jsonPath("$[0].categoria").value("HAMBURGUESAS"));
	}

	@Test
	void categoriaInvalida_responde400() throws Exception {
		mockMvc.perform(get("/api/productos").param("categoria", "NOEXISTE"))
			.andExpect(status().isBadRequest());
	}

	@Test
	void detallePorId_devuelveProductoConImagenYDescripcion() throws Exception {
		Long id = repositorio.findBySlug("bandeja-paisa").orElseThrow().getId();
		mockMvc.perform(get("/api/productos/{id}", id))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.slug").value("bandeja-paisa"))
			.andExpect(jsonPath("$.precioCop").value(28000))
			.andExpect(jsonPath("$.descripcion").isNotEmpty())
			.andExpect(jsonPath("$.imagenUrl").isNotEmpty());
	}

	@Test
	void detalleInexistente_responde404() throws Exception {
		mockMvc.perform(get("/api/productos/999999"))
			.andExpect(status().isNotFound());
	}

	@Test
	void busquedaPorNombre_devuelveCoincidencias() throws Exception {
		mockMvc.perform(get("/api/productos/buscar").param("q", "colombiana"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(1))
			.andExpect(jsonPath("$[0].slug").value("gaseosa-colombiana"));
	}

	@Test
	void busquedaPorDescripcion_devuelveCoincidencias() throws Exception {
		mockMvc.perform(get("/api/productos/buscar").param("q", "helado"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$[0].slug").value("brownie-chocolate"));
	}

	@Test
	void busquedaSinResultados_devuelveListaVacia() throws Exception {
		mockMvc.perform(get("/api/productos/buscar").param("q", "zzzznada"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(0));
	}

	@Test
	void accesoPublico_sinToken_devuelve200() throws Exception {
		mockMvc.perform(get("/api/productos"))
			.andExpect(status().isOk());
	}

	@Test
	void categorias_devuelveLasCuatroCategorias() throws Exception {
		mockMvc.perform(get("/api/categorias"))
			.andExpect(status().isOk())
			.andExpect(jsonPath("$.length()").value(4))
			.andExpect(jsonPath("$[?(@ == 'HAMBURGUESAS')]").exists())
			.andExpect(jsonPath("$[?(@ == 'POSTRES')]").exists());
	}
}
