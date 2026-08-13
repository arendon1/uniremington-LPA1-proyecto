package one.austral.lpa1.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.server.ResponseStatusException;

import jakarta.validation.Valid;

import one.austral.lpa1.dto.LoginRequest;
import one.austral.lpa1.dto.RegistroRequest;
import one.austral.lpa1.dto.RespuestaLogin;
import one.austral.lpa1.dto.UsuarioDto;
import one.austral.lpa1.model.Rol;
import one.austral.lpa1.model.Usuario;
import one.austral.lpa1.repositorio.UsuarioRepositorio;
import one.austral.lpa1.security.JwtServicio;

/**
 * Autenticacion (U3): RF1 registro, RF2 login con JWT. El registro crea
 * usuarios con rol USER; el rol ADMIN existe en el seed (panel Thymeleaf).
 */
@RestController
@RequestMapping("/api/auth")
public class ControladorAuthApi {

	private final UsuarioRepositorio usuarios;
	private final PasswordEncoder passwordEncoder;
	private final JwtServicio jwtServicio;

	public ControladorAuthApi(UsuarioRepositorio usuarios,
			PasswordEncoder passwordEncoder, JwtServicio jwtServicio) {
		this.usuarios = usuarios;
		this.passwordEncoder = passwordEncoder;
		this.jwtServicio = jwtServicio;
	}

	@PostMapping("/register")
	public ResponseEntity<UsuarioDto> registrar(@Valid @RequestBody RegistroRequest solicitud) {
		String email = solicitud.email().trim().toLowerCase();
		if (usuarios.existsByEmail(email)) {
			throw new ResponseStatusException(HttpStatus.CONFLICT,
					"Ya existe un usuario con ese email");
		}
		Usuario nuevo = new Usuario(email,
				passwordEncoder.encode(solicitud.password()),
				solicitud.nombre().trim(),
				Rol.USER);
		usuarios.save(nuevo);
		return ResponseEntity.status(HttpStatus.CREATED).body(UsuarioDto.desde(nuevo));
	}

	@PostMapping("/login")
	public RespuestaLogin iniciarSesion(@Valid @RequestBody LoginRequest solicitud) {
		String email = solicitud.email().trim().toLowerCase();
		Usuario usuario = usuarios.findByEmail(email)
				.orElseThrow(() -> new ResponseStatusException(HttpStatus.UNAUTHORIZED,
						"Credenciales invalidas"));
		if (!passwordEncoder.matches(solicitud.password(), usuario.getPasswordHash())) {
			throw new ResponseStatusException(HttpStatus.UNAUTHORIZED,
					"Credenciales invalidas");
		}
		String token = jwtServicio.generar(usuario.getEmail(), usuario.getRol());
		return new RespuestaLogin(token, usuario.getEmail(), usuario.getNombre(),
				usuario.getRol().name());
	}
}
