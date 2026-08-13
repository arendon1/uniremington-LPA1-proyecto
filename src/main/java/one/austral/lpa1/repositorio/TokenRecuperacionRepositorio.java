package one.austral.lpa1.repositorio;

import java.util.Optional;

import org.springframework.data.jpa.repository.JpaRepository;

import one.austral.lpa1.model.TokenRecuperacion;

public interface TokenRecuperacionRepositorio extends JpaRepository<TokenRecuperacion, Long> {

	Optional<TokenRecuperacion> findByTokenHash(String tokenHash);
}
