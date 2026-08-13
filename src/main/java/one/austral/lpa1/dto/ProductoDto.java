package one.austral.lpa1.dto;

import one.austral.lpa1.model.Producto;

/**
 * Producto tal como viaja por la API REST (JSON). Nunca se expone la entidad
 * JPA directamente (invariante V6).
 */
public record ProductoDto(
		Long id,
		String slug,
		String nombre,
		String descripcion,
		int precioCop,
		String unidad,
		int stock,
		String categoria,
		String imagenUrl) {

	public static ProductoDto desde(Producto p) {
		return new ProductoDto(
				p.getId(),
				p.getSlug(),
				p.getNombre(),
				p.getDescripcion(),
				p.getPrecioCop(),
				p.getUnidad(),
				p.getStock(),
				p.getCategoria().name(),
				p.getImagenUrl());
	}
}
