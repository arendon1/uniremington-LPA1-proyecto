package one.austral.lpa1.dto;

import one.austral.lpa1.model.Usuario;

/** Usuario tal como se expone por la API: nunca el hash de la contrasena. */
public record UsuarioDto(Long id, String email, String nombre, String rol) {

	public static UsuarioDto desde(Usuario u) {
		return new UsuarioDto(u.getId(), u.getEmail(), u.getNombre(), u.getRol().name());
	}
}
