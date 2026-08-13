package one.austral.lpa1.config;

import java.io.IOException;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.AuthenticationEntryPoint;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

import one.austral.lpa1.repositorio.UsuarioRepositorio;
import one.austral.lpa1.security.FiltroJwt;
import one.austral.lpa1.security.JwtServicio;

/**
 * Cadena de seguridad de la API REST (U3): stateless con JWT. Convive con la
 * cadena MVC (formLogin + sesion) que sirve las vistas Thymeleaf de U1/U2:
 * /api/** se evalua primero (Order 1) y el resto cae en la cadena MVC.
 */
@Configuration
public class ConfiguracionSeguridadApi {

	@Bean
	@Order(1)
	public SecurityFilterChain cadenaApi(HttpSecurity http,
			JwtServicio jwtServicio, UsuarioRepositorio usuarios) throws Exception {
		return http
			.securityMatcher("/api/**")
			.cors(Customizer.withDefaults())
			.csrf(csrf -> csrf.disable())
			.sessionManagement(sesion -> sesion
				.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
			.authorizeHttpRequests(autorizacion -> autorizacion
				.requestMatchers(HttpMethod.GET, "/api/productos/**", "/api/categorias").permitAll()
				.requestMatchers("/api/auth/**").permitAll()
				.requestMatchers("/api/admin/**").hasRole("ADMIN")
				.anyRequest().authenticated())
			.exceptionHandling(manejo -> manejo
				.authenticationEntryPoint(entryPointApiJson())
				.accessDeniedHandler(handlerAccesoDenegadoJson()))
			.addFilterBefore(new FiltroJwt(jwtServicio, usuarios),
				UsernamePasswordAuthenticationFilter.class)
			.build();
	}

	/** 403 en JSON: sin forward a /error (que caeria en la cadena MVC y redirigiria a /login). */
	@Bean
	public org.springframework.security.web.access.AccessDeniedHandler handlerAccesoDenegadoJson() {
		return (jakarta.servlet.http.HttpServletRequest request,
				jakarta.servlet.http.HttpServletResponse response,
				org.springframework.security.access.AccessDeniedException ex)
				-> {
			response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_FORBIDDEN);
			response.setContentType("application/json;charset=UTF-8");
			try {
				response.getWriter().write("{\"error\":\"Acceso denegado\"}");
			} catch (java.io.IOException e) {
				// no hay nada mas que hacer
			}
		};
	}

	/** 401 en JSON para clientes REST (la SPA y Postman lo leen limpio). */
	@Bean
	public AuthenticationEntryPoint entryPointApiJson() {
		return (jakarta.servlet.http.HttpServletRequest request,
				jakarta.servlet.http.HttpServletResponse response,
				org.springframework.security.core.AuthenticationException ex)
				-> {
			response.setStatus(jakarta.servlet.http.HttpServletResponse.SC_UNAUTHORIZED);
			response.setContentType("application/json;charset=UTF-8");
			try {
				response.getWriter().write("{\"error\":\"No autorizado\"}");
			} catch (IOException e) {
				// no hay nada mas que hacer
			}
		};
	}
}
