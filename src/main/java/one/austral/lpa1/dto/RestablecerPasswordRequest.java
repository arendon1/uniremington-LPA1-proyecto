package one.austral.lpa1.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;

/** RF3: restablecimiento con el token recibido por correo. */
public record RestablecerPasswordRequest(
		@NotBlank(message = "El token es obligatorio")
		String token,

		@NotBlank(message = "La contrasena es obligatoria")
		@Size(min = 6, message = "La contrasena debe tener minimo 6 caracteres")
		String nuevaPassword) {
}
