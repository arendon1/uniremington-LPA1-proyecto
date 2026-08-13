package one.austral.lpa1.service;

import java.util.Arrays;
import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.stereotype.Service;
import org.springframework.web.server.ResponseStatusException;

import one.austral.lpa1.dto.ProductoDto;
import one.austral.lpa1.model.Categoria;
import one.austral.lpa1.model.Producto;
import one.austral.lpa1.repositorio.ProductoRepositorio;

/**
 * Catalogo servido por la API REST (U3). RF4 (lista por categoria),
 * RF5 (detalle) y RF6 (busqueda por nombre o palabra clave).
 */
@Service
public class ProductoServicio {

	private final ProductoRepositorio repositorio;

	public ProductoServicio(ProductoRepositorio repositorio) {
		this.repositorio = repositorio;
	}

	/** RF4: lista completa o filtrada por categoria (HAMBURGUESAS, COMIDAS, BEBIDAS, POSTRES). */
	public List<ProductoDto> listar(String categoria) {
		if (categoria == null || categoria.isBlank()) {
			return repositorio.findAllByOrderByNombreAsc().stream()
					.map(ProductoDto::desde)
					.toList();
		}
		Categoria cat;
		try {
			cat = Categoria.valueOf(categoria.trim().toUpperCase());
		} catch (IllegalArgumentException e) {
			throw new ResponseStatusException(HttpStatus.BAD_REQUEST,
					"Categoria invalida: " + categoria);
		}
		return repositorio.findByCategoriaOrderByNombre(cat).stream()
				.map(ProductoDto::desde)
				.toList();
	}

	/** RF5: detalle de un producto por id. */
	public ProductoDto detalle(Long id) {
		Producto p = repositorio.findById(id)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.NOT_FOUND,
						"Producto no encontrado: " + id));
		return ProductoDto.desde(p);
	}

	/** RF6: busqueda por nombre o descripcion (palabra clave, sin distinguir mayusculas). */
	public List<ProductoDto> buscar(String q) {
		if (q == null || q.isBlank()) {
			return List.of();
		}
		String clave = q.trim();
		return repositorio
				.findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCaseOrderByNombreAsc(
						clave, clave)
				.stream()
				.map(ProductoDto::desde)
				.toList();
	}

	public List<String> categorias() {
		return Arrays.stream(Categoria.values()).map(Enum::name).toList();
	}
}
