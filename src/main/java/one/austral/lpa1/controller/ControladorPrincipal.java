package one.austral.lpa1.controller;

import java.security.Principal;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ResponseBody;

@Controller
public class ControladorPrincipal {

	@GetMapping("/public")
	public String paginaPublica(Model modelo) {
		modelo.addAttribute("titulo", "Pagina publica");
		modelo.addAttribute("subtitulo", "/public");
		modelo.addAttribute("descripcion",
			"Endpoint de demostracion accesible sin autenticacion. "
			+ "Util para probar el deny-by-default de Spring Security: "
			+ "esta ruta esta explicitamente en permitAll() en "
			+ "ConfiguracionSeguridad.");
		return "ruta";
	}

	@GetMapping("/user")
	public String paginaUsuario(Principal principal, Model modelo) {
		modelo.addAttribute("titulo", "Zona de usuario");
		modelo.addAttribute("subtitulo", "/user");
		modelo.addAttribute("descripcion",
			"Visible para usuarios con rol USER o ADMIN. "
			+ "Aqui se muestra informacion personalizada del usuario "
			+ "autenticado. Un anonimo que intente entrar sera "
			+ "redirigido a /login por Spring Security.");
		modelo.addAttribute("infoUsuario", principal.getName());
		return "ruta";
	}

	@GetMapping("/admin")
	public String paginaAdmin(Principal principal, Model modelo) {
		modelo.addAttribute("titulo", "Panel de administracion");
		modelo.addAttribute("subtitulo", "/admin");
		modelo.addAttribute("descripcion",
			"Acceso restringido. Solo usuarios con rol ADMIN pueden ver "
			+ "esta pagina. Un USER autenticado que intente entrar sera "
			+ "redirigido a /access-denied (T8).");
		modelo.addAttribute("infoUsuario", principal.getName());
		return "ruta";
	}

	@GetMapping("/login")
	public String paginaLogin() {
		return "login";
	}

	@GetMapping("/access-denied")
	public String paginaAccessDenied() {
		return "access-denied";
	}

	@GetMapping("/")
	public String paginaIndex() {
		return "index";
	}
}
