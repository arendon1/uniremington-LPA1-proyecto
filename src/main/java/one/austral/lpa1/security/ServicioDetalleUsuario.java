package one.austral.lpa1.security;

import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import one.austral.lpa1.model.Usuario;
import one.austral.lpa1.repositorio.UsuarioRepositorio;

/**
 * UserDetailsService unico: lee de PostgreSQL (U3). El login por formulario
 * de las vistas Thymeleaf (U1/U2) usa este mismo servicio; los usuarios
 * admin/user del seed mantienen las mismas credenciales de siempre.
 */
@Service
public class ServicioDetalleUsuario implements UserDetailsService {

	private final UsuarioRepositorio usuarios;

	public ServicioDetalleUsuario(UsuarioRepositorio usuarios) {
		this.usuarios = usuarios;
	}

	@Override
	public UserDetails loadUserByUsername(String email) throws UsernameNotFoundException {
		Usuario u = usuarios.findByEmail(email)
				.orElseThrow(() -> new UsernameNotFoundException("Usuario no encontrado: " + email));
		return User.withUsername(u.getEmail())
				.password(u.getPasswordHash())
				.roles(u.getRol().name())
				.build();
	}
}
