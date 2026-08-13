package one.austral.lpa1.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;

import one.austral.lpa1.service.ProductoServicio;

/** Categorias disponibles para el menu de la SPA. */
@RestController
public class ControladorCategoriasApi {

	private final ProductoServicio servicio;

	public ControladorCategoriasApi(ProductoServicio servicio) {
		this.servicio = servicio;
	}

	@GetMapping("/api/categorias")
	public List<String> categorias() {
		return servicio.categorias();
	}
}
