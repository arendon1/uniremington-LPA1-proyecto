package one.austral.lpa1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** RF1: registro de nuevo usuario por email y contrasena. */
public record RegistroRequest(
		@NotBlank(message = "El email es obligatorio")
		@Email(message = "El email no es valido")
		String email,

		@NotBlank(message = "La contrasena es obligatoria")
		@Size(min = 6, message = "La contrasena debe tener minimo 6 caracteres")
		String password,

		@NotBlank(message = "El nombre es obligatorio")
		String nombre) {
}
