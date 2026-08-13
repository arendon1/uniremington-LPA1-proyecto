package one.austral.lpa1.dto;

/** Respuesta de login: el token JWT que la SPA guarda y envia en cada llamada. */
public record RespuestaLogin(String token, String email, String nombre, String rol) {
}
