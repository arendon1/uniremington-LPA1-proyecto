package one.austral.lpa1.controller;

import java.security.Principal;
import java.util.List;
import java.util.Locale;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import one.austral.lpa1.service.Catalogo;

@Controller
public class ControladorPrincipal {

	private final Catalogo catalogo;

	public ControladorPrincipal(Catalogo catalogo) {
		this.catalogo = catalogo;
	}

	@GetMapping("/")
	public String tienda(Model modelo) {
		modelo.addAttribute("productos", catalogo.listar());
		return "index";
	}

	@GetMapping("/public")
	public String tiendaPublica(Model modelo) {
		return tienda(modelo);
	}

	@GetMapping("/user")
	public String cuenta(Principal principal, Model modelo) {
		modelo.addAttribute("nombre", principal.getName());
		return "cuenta";
	}

	@GetMapping("/admin")
	public String panel(Principal principal, Model modelo) {
		modelo.addAttribute("nombre", principal.getName());
		modelo.addAttribute("productos", catalogo.listar());
		modelo.addAttribute("total", catalogo.total());
		return "panel";
	}

	@GetMapping("/login")
	public String login() {
		return "login";
	}

	@GetMapping("/access-denied")
	public String accessDenied() {
		return "access-denied";
	}
}