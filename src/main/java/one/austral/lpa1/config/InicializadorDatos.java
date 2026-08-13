package one.austral.lpa1.config;

import java.util.List;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.CommandLineRunner;
import org.springframework.stereotype.Component;

import one.austral.lpa1.model.Categoria;
import one.austral.lpa1.model.Producto;
import one.austral.lpa1.repositorio.ProductoRepositorio;

/**
 * Seed del catalogo en PostgreSQL (U3). Idempotente: solo inserta si la tabla
 * esta vacia. Los 10 platos de U1/U2 se conservan y se reclasifican, y se
 * suman hamburguesas, bebidas y postres para cubrir las categorias del RF4.
 */
@Component
public class InicializadorDatos implements CommandLineRunner {

	private static final Logger LOG = LoggerFactory.getLogger(InicializadorDatos.class);

	private final ProductoRepositorio repositorio;

	@Value("${app.seed.enabled:true}")
	private boolean habilitado;

	public InicializadorDatos(ProductoRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	@Override
	public void run(String... args) {
		if (!habilitado) {
			return;
		}
		if (repositorio.count() > 0) {
			return;
		}
		repositorio.saveAll(PRODUCTOS);
		LOG.info("Seed U3: {} productos insertados en PostgreSQL", PRODUCTOS.size());
	}

	private static String img(String slug) {
		return "/images/productos/" + slug + ".jpg";
	}

	private static final List<Producto> PRODUCTOS = List.of(
			// HAMBURGUESAS
			new Producto("hamburguesa-clasica", "Hamburguesa clasica",
					"Carne 150 g, queso cheddar, lechuga y salsa especial.",
					18000, "unidad", 60, Categoria.HAMBURGUESAS, img("hamburguesa-clasica")),
			new Producto("hamburguesa-doble", "Hamburguesa doble",
					"Doble carne 200 g, doble queso, tocineta y cebolla caramelizada.",
					26000, "unidad", 40, Categoria.HAMBURGUESAS, img("hamburguesa-doble")),
			new Producto("hamburguesa-pollo-crispy", "Hamburguesa pollo crispy",
					"Pollo apanado crujiente, coleslaw y mayonesa de chipotle.",
					22000, "unidad", 45, Categoria.HAMBURGUESAS, img("hamburguesa-pollo-crispy")),

			// COMIDAS (los 10 platos de U1/U2, reclasificados)
			new Producto("bandeja-paisa", "Bandeja paisa",
					"Arroz, frijoles, platano, carne, huevo y aguacate. Clasico colombiano.",
					28000, "porcion", 40, Categoria.COMIDAS, img("bandeja-paisa")),
			new Producto("sushi-rolls", "Sushi California",
					"8 rollos de salmon, aguacate y queso crema.",
					32000, "set", 25, Categoria.COMIDAS, img("sushi-rolls")),
			new Producto("pizza-pepperoni", "Pizza pepperoni",
					"Mediana, masa artesanal y pepperoni importado.",
					35000, "mediana", 30, Categoria.COMIDAS, img("pizza-pepperoni")),
			new Producto("arepa-carne", "Arepa con carne",
					"Arepa boyacense con carne desmechada en salsa BBQ.",
					12000, "unidad", 50, Categoria.COMIDAS, img("arepa-carne")),
			new Producto("tacos-carnitas", "Tacos de carnitas",
					"3 tacos de cerdo adobado con cebolla y cilantro.",
					22000, "orden", 45, Categoria.COMIDAS, img("tacos-carnitas")),
			new Producto("pollo-asado", "Pollo asado",
					"1/4 de pollo con papas a la francesa y arepa.",
					24000, "porcion", 35, Categoria.COMIDAS, img("pollo-asado")),
			new Producto("poke-bowl", "Poke bowl de salmon",
					"Arroz, salmon fresco, aguacate y aderezo sesamo.",
					30000, "bowl", 20, Categoria.COMIDAS, img("poke-bowl")),
			new Producto("pasta-alfredo", "Pasta Alfredo",
					"Pasta linguini con pollo grillado y salsa Alfredo.",
					26000, "porcion", 28, Categoria.COMIDAS, img("pasta-alfredo")),
			new Producto("sandwich-cubano", "Sandwich cubano",
					"Jamon, cerdo, queso, pepinillos y mostaza.",
					15000, "unidad", 40, Categoria.COMIDAS, img("sandwich-cubano")),

			// BEBIDAS
			new Producto("gaseosa-colombiana", "Gaseosa colombiana",
					"Botella 500 ml bien fria.",
					5000, "botella", 100, Categoria.BEBIDAS, img("gaseosa-colombiana")),
			new Producto("jugo-natural", "Jugo natural",
					"Naranja, mango o mora recien exprimido.",
					8000, "vaso", 80, Categoria.BEBIDAS, img("jugo-natural")),
			new Producto("limonada-cerezada", "Limonada cerezada",
					"Limon, cereza y hielo.",
					7000, "vaso", 80, Categoria.BEBIDAS, img("limonada-cerezada")),
			new Producto("cafe-colombiano", "Cafe colombiano",
					"Tinto o capuchino, grano de la region.",
					4000, "taza", 120, Categoria.BEBIDAS, img("cafe-colombiano")),

			// POSTRES
			new Producto("brownie-chocolate", "Brownie de chocolate",
					"Con nuez y helado de vainilla.",
					12000, "unidad", 50, Categoria.POSTRES, img("brownie-chocolate")),
			new Producto("cheesecake-frutas", "Cheesecake de frutos rojos",
					"Base de galleta y salsa de mora.",
					15000, "porcion", 35, Categoria.POSTRES, img("cheesecake-frutas")),
			new Producto("helado-artesanal", "Helado artesanal",
					"Tres bolas: vainilla, chocolate y arequipe.",
					9000, "copa", 60, Categoria.POSTRES, img("helado-artesanal")));
}
