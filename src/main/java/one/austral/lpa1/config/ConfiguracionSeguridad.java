package one.austral.lpa1.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

@Configuration
public class ConfiguracionSeguridad {

	@Bean
	public SecurityFilterChain cadenaFiltrosSeguridad(HttpSecurity http) throws Exception {
		return http
			.authorizeHttpRequests(autorizacion -> autorizacion
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
				.requestMatchers("/", "/public", "/login", "/access-denied").permitAll()
				.requestMatchers("/user").hasAnyRole("USER", "ADMIN")
				.requestMatchers("/admin").hasRole("ADMIN")
				.anyRequest().authenticated()
			)
			.formLogin(formulario -> formulario
				.loginPage("/login")
				.failureUrl("/login?error")
				.permitAll()
			)
			.logout(salida -> salida
				.logoutSuccessUrl("/login?logout")
				.permitAll()
			)
			.exceptionHandling(manejo -> manejo
				.accessDeniedHandler(manejadorAccessDenied())
			)
			.build();
	}


	@Bean
	public AccessDeniedHandler manejadorAccessDenied() {
		return (HttpServletRequest request, HttpServletResponse response, AccessDeniedException ex) -> {
			try {
				request.getRequestDispatcher("/access-denied").forward(request, response);
			} catch (ServletException | java.io.IOException e) {
				throw new RuntimeException("No se pudo hacer forward a /access-denied", e);
			}
		};
	}
}
