package one.austral.lpa1.controller;

import java.util.Locale;

import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;

import one.austral.lpa1.dto.RestablecerPasswordRequest;
import one.austral.lpa1.dto.SolicitudRecuperacionRequest;
import one.austral.lpa1.service.RecuperacionContrasenaServicio;

/** RF3: recuperacion de contrasena por correo (forgot + reset). */
@RestController
@RequestMapping("/api/auth")
public class ControladorRecuperacionApi {

	private final RecuperacionContrasenaServicio servicio;

	public ControladorRecuperacionApi(RecuperacionContrasenaServicio servicio) {
		this.servicio = servicio;
	}

	@PostMapping("/forgot")
	public void solicitar(@Valid @RequestBody SolicitudRecuperacionRequest solicitud,
			Locale locale) {
		servicio.solicitar(solicitud.email(), locale);
	}

	@PostMapping("/reset")
	public void restablecer(@Valid @RequestBody RestablecerPasswordRequest solicitud) {
		servicio.restablecer(solicitud.token(), solicitud.nuevaPassword());
	}
}
