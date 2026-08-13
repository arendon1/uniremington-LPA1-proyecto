package one.austral.lpa1.service;

import java.util.List;

import org.springframework.stereotype.Component;

import one.austral.lpa1.model.ProductoVista;

@Component
public class Catalogo {

	private static final List<ProductoVista> PRODUCTOS = List.of(
		new ProductoVista("bandeja-paisa", 28000, "porcion", 40),
		new ProductoVista("sushi-rolls", 32000, "set", 25),
		new ProductoVista("hamburguesa-clasica", 18000, "unidad", 60),
		new ProductoVista("pizza-pepperoni", 35000, "mediana", 30),
		new ProductoVista("arepa-carne", 12000, "unidad", 50),
		new ProductoVista("tacos-carnitas", 22000, "orden", 45),
		new ProductoVista("pollo-asado", 24000, "porcion", 35),
		new ProductoVista("poke-bowl", 30000, "bowl", 20),
		new ProductoVista("pasta-alfredo", 26000, "porcion", 28),
		new ProductoVista("sandwich-cubano", 15000, "unidad", 40)
	);

	public List<ProductoVista> listar() {
		return PRODUCTOS;
	}

	public int total() {
		return PRODUCTOS.size();
	}
}