package one.austral.lpa1.model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;

/**
 * Usuario registrado (RF1/RF2). La contrasena viaja hasheada con BCrypt;
 * nunca se expone por la API.
 */
@Entity
@Table(name = "usuarios")
public class Usuario {

	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	private Long id;

	@Column(nullable = false, unique = true, length = 254)
	private String email;

	@Column(nullable = false)
	private String passwordHash;

	@Column(nullable = false)
	private String nombre;

	@Enumerated(EnumType.STRING)
	@Column(nullable = false, length = 10)
	private Rol rol;

	protected Usuario() {
		// para JPA
	}

	public Usuario(String email, String passwordHash, String nombre, Rol rol) {
		this.email = email;
		this.passwordHash = passwordHash;
		this.nombre = nombre;
		this.rol = rol;
	}

	public Long getId() {
		return id;
	}

	public String getEmail() {
		return email;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public String getNombre() {
		return nombre;
	}

	public Rol getRol() {
		return rol;
	}

	/** Solo el flujo de recuperacion cambia la contrasena (RF3). */
	public void cambiarPassword(String nuevoHash) {
		this.passwordHash = nuevoHash;
	}
}
