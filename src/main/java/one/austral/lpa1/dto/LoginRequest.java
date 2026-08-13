package one.austral.lpa1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** RF2: inicio de sesion con credenciales registradas. El login NO exige
 *  formato email: los usuarios seed de U1 usan "admin"/"user" como
 *  credenciales; el registro (RF1) si valida email. */
public record LoginRequest(
		@NotBlank(message = "El email es obligatorio")
		String email,

		@NotBlank(message = "La contrasena es obligatoria")
		String password) {
}
