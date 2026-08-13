package one.austral.lpa1.controller;

import java.util.List;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import one.austral.lpa1.dto.UsuarioDto;
import one.austral.lpa1.repositorio.UsuarioRepositorio;

/** Demo de rol ADMIN en la API REST (Postman: token ADMIN vs token USER). */
@RestController
@RequestMapping("/api/admin")
public class ControladorAdminApi {

	private final UsuarioRepositorio usuarios;

	public ControladorAdminApi(UsuarioRepositorio usuarios) {
		this.usuarios = usuarios;
	}

	@GetMapping("/usuarios")
	public List<UsuarioDto> listarUsuarios() {
		return usuarios.findAll().stream().map(UsuarioDto::desde).toList();
	}
}
