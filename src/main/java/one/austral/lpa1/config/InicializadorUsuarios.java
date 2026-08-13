package one.austral.lpa1.config;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.boot.CommandLineRunner;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import one.austral.lpa1.model.Rol;
import one.austral.lpa1.model.Usuario;
import one.austral.lpa1.repositorio.UsuarioRepositorio;

/**
 * Usuarios base (U1/U2): admin/admin123 y user/user123, ahora persistidos en
 * PostgreSQL. Idempotente: solo crea los que falten. Corre tambien en el
 * perfil de test para que las suites de U1/U2 (formLogin) sigan verdes.
 */
@Component
public class InicializadorUsuarios implements CommandLineRunner {

	private static final Logger LOG = LoggerFactory.getLogger(InicializadorUsuarios.class);

	private final UsuarioRepositorio usuarios;
	private final PasswordEncoder passwordEncoder;

	public InicializadorUsuarios(UsuarioRepositorio usuarios, PasswordEncoder passwordEncoder) {
		this.usuarios = usuarios;
		this.passwordEncoder = passwordEncoder;
	}

	@Override
	public void run(String... args) {
		crearSiFalta("admin", "admin123", "Administrador", Rol.ADMIN);
		crearSiFalta("user", "user123", "Usuario Demo", Rol.USER);
	}

	private void crearSiFalta(String email, String password, String nombre, Rol rol) {
		if (!usuarios.existsByEmail(email)) {
			usuarios.save(new Usuario(email, passwordEncoder.encode(password), nombre, rol));
			LOG.info("Seed usuarios: creado {} con rol {}", email, rol);
		}
	}
}
