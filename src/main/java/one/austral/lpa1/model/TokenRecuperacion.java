package one.austral.lpa1.model;

import java.time.Instant;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;

/**
 * Token de recuperacion de contrasena (RF3). En la BD solo vive el hash
 * SHA-256 del token; el token crudo viaja en el link del correo. Expira en
 * 30 minutos y es de un solo uso.
 */
@Entity
@Table(name = "tokens_recuperacion")
public class TokenRecuperacion {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 64)
	private String tokenHash;

	@ManyToOne(fetch = FetchType.EAGER, optional = false)
	@JoinColumn(name = "usuario_id", nullable = false)
	private Usuario usuario;

	@Column(nullable = false)
	private Instant expiraEn;

	@Column(nullable = false)
	private boolean usado;

	protected TokenRecuperacion() {
		// para JPA
	}

	public TokenRecuperacion(String tokenHash, Usuario usuario, Instant expiraEn) {
		this.tokenHash = tokenHash;
		this.usuario = usuario;
		this.expiraEn = expiraEn;
		this.usado = false;
	}

	public Long getId() {
		return id;
	}

	public String getTokenHash() {
		return tokenHash;
	}

	public Usuario getUsuario() {
		return usuario;
	}

	public Instant getExpiraEn() {
		return expiraEn;
	}

	public boolean isUsado() {
		return usado;
	}

	public void marcarUsado() {
		this.usado = true;
	}
}
