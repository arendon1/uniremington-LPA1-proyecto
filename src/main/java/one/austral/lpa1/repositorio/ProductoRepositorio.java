package one.austral.lpa1.repositorio;

import java.util.List;
import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import one.austral.lpa1.model.Categoria;
import one.austral.lpa1.model.Producto;

public interface ProductoRepositorio extends JpaRepository<Producto, Long> {

	List<Producto> findAllByOrderByNombreAsc();

	List<Producto> findByCategoriaOrderByNombre(Categoria categoria);

	Optional<Producto> findBySlug(String slug);

	boolean existsBySlug(String slug);

	List<Producto> findByNombreContainingIgnoreCaseOrDescripcionContainingIgnoreCaseOrderByNombreAsc(
			String nombre, String descripcion);
}
