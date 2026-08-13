package one.austral.lpa1.security;

import java.io.IOException;
import java.util.List;

import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import io.jsonwebtoken.JwtException;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

import one.austral.lpa1.repositorio.UsuarioRepositorio;

/**
 * Filtro JWT de la cadena /api/** (stateless). Si llega un Bearer token
 * valido, carga al usuario y lo deja autenticado en el contexto; si el token
 * falta o es invalido, la peticion sigue sin autenticar y el entry point
 * responde 401 cuando la ruta lo exige.
 */
public class FiltroJwt extends OncePerRequestFilter {

	private final JwtServicio jwtServicio;
	private final UsuarioRepositorio usuarios;

	public FiltroJwt(JwtServicio jwtServicio, UsuarioRepositorio usuarios) {
		this.jwtServicio = jwtServicio;
		this.usuarios = usuarios;
	}

	@Override
	protected void doFilterInternal(HttpServletRequest request,
			HttpServletResponse response, FilterChain cadena)
			throws ServletException, IOException {
		String cabecera = request.getHeader("Authorization");
		if (cabecera != null && cabecera.startsWith("Bearer ")) {
			String token = cabecera.substring(7);
			try {
				String email = jwtServicio.validar(token);
				usuarios.findByEmail(email).ifPresent(usuario -> {
					var autenticacion = new UsernamePasswordAuthenticationToken(
							usuario.getEmail(), null,
							List.of(new SimpleGrantedAuthority("ROLE_" + usuario.getRol().name())));
					SecurityContextHolder.getContext().setAuthentication(autenticacion);
				});
			} catch (JwtException | IllegalArgumentException e) {
				// token invalido o expirado: se sigue sin autenticar (401 por entry point)
				SecurityContextHolder.clearContext();
			}
		}
		cadena.doFilter(request, response);
	}
}
