package one.austral.lpa1.config;

import jakarta.servlet.http.HttpServletRequest;

import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ModelAttribute;

@ControllerAdvice
public class ContextoWeb {

	@ModelAttribute("rutaActual")
	public String rutaActual(HttpServletRequest peticion) {
		return peticion.getRequestURI();
	}
}