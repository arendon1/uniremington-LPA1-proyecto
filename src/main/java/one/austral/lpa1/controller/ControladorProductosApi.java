package one.austral.lpa1.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import one.austral.lpa1.dto.ProductoDto;
import one.austral.lpa1.service.ProductoServicio;

/**
 * API REST de catalogo (U3). RF4/RF5/RF6. Acceso publico (GET).
 */
@RestController
@RequestMapping("/api/productos")
public class ControladorProductosApi {

	private final ProductoServicio servicio;

	public ControladorProductosApi(ProductoServicio servicio) {
		this.servicio = servicio;
	}

	@GetMapping
	public List<ProductoDto> listar(@RequestParam(required = false) String categoria) {
		return servicio.listar(categoria);
	}

	@GetMapping("/{id}")
	public ProductoDto detalle(@PathVariable Long id) {
		return servicio.detalle(id);
	}

	@GetMapping("/buscar")
	public List<ProductoDto> buscar(@RequestParam String q) {
		return servicio.buscar(q);
	}
}
