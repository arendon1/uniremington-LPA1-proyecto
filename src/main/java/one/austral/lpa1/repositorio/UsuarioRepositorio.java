package one.austral.lpa1.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import one.austral.lpa1.model.Usuario;

public interface UsuarioRepositorio extends JpaRepository<Usuario, Long> {

	Optional<Usuario> findByEmail(String email);

	boolean existsByEmail(String email);
}
