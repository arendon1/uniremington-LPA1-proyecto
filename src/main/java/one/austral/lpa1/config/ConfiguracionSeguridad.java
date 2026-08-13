package one.austral.lpa1.config;

import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.boot.security.autoconfigure.web.servlet.PathRequest;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.Customizer;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;
import org.springframework.web.cors.CorsConfiguration;
import org.springframework.web.cors.CorsConfigurationSource;
import org.springframework.web.cors.UrlBasedCorsConfigurationSource;

import java.util.List;

@Configuration
public class ConfiguracionSeguridad {

	@Bean
	public SecurityFilterChain cadenaFiltrosSeguridad(HttpSecurity http) throws Exception {
		return http
			.cors(Customizer.withDefaults())
			.authorizeHttpRequests(autorizacion -> autorizacion
				.requestMatchers(PathRequest.toStaticResources().atCommonLocations()).permitAll()
				.requestMatchers("/swagger-ui/**", "/swagger-ui.html", "/v3/api-docs/**").permitAll()
				.requestMatchers("/error").permitAll()
				.requestMatchers("/", "/public", "/login", "/access-denied").permitAll()
				.requestMatchers(HttpMethod.GET, "/api/productos/**", "/api/categorias").permitAll()
				.requestMatchers("/api/auth/**").permitAll()
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

	/** BCrypt: hashing de contrasenas (U1 se conserva). */
	@Bean
	public org.springframework.security.crypto.password.PasswordEncoder passwordEncoder() {
		return new org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder();
	}

	/** CORS para la SPA (Svelte en :5173) contra la API en :8080. */
	@Bean
	public CorsConfigurationSource corsConfigurationSource() {
		CorsConfiguration configuracion = new CorsConfiguration();
		configuracion.setAllowedOrigins(List.of("http://localhost:5173", "http://127.0.0.1:5173"));
		configuracion.setAllowedMethods(List.of("GET", "POST", "PUT", "DELETE", "OPTIONS"));
		configuracion.setAllowedHeaders(List.of("*"));
		configuracion.setExposedHeaders(List.of("Authorization"));
		configuracion.setAllowCredentials(true);
		UrlBasedCorsConfigurationSource fuente = new UrlBasedCorsConfigurationSource();
		fuente.registerCorsConfiguration("/api/**", configuracion);
		return fuente;
	}
}
