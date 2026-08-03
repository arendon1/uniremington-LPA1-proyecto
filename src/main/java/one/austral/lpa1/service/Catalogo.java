package one.austral.lpa1.service;

import java.util.List;

import org.springframework.stereotype.Component;

import one.austral.lpa1.model.Producto;

@Component
public class Catalogo {

	private static final List<Producto> PRODUCTOS = List.of(
		new Producto("bandeja-paisa", 28000, "porcion", 40),
		new Producto("sushi-rolls", 32000, "set", 25),
		new Producto("hamburguesa-clasica", 18000, "unidad", 60),
		new Producto("pizza-pepperoni", 35000, "mediana", 30),
		new Producto("arepa-carne", 12000, "unidad", 50),
		new Producto("tacos-carnitas", 22000, "orden", 45),
		new Producto("pollo-asado", 24000, "porcion", 35),
		new Producto("poke-bowl", 30000, "bowl", 20),
		new Producto("pasta-alfredo", 26000, "porcion", 28),
		new Producto("sandwich-cubano", 15000, "unidad", 40)
	);

	public List<Producto> listar() {
		return PRODUCTOS;
	}

	public int total() {
		return PRODUCTOS.size();
	}
}