package one.austral.lpa1.dto;

import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotBlank;

/** RF3: solicitud de recuperacion de contrasena. */
public record SolicitudRecuperacionRequest(
		@NotBlank(message = "El email es obligatorio")
		@Email(message = "El email no es valido")
		String email) {
}
