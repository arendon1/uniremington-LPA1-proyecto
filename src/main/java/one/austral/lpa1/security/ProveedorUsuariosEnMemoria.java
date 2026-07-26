package one.austral.lpa1.security;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.provisioning.InMemoryUserDetailsManager;

@Configuration
@SuppressWarnings("deprecation")
public class ProveedorUsuariosEnMemoria {

	@Bean
	public InMemoryUserDetailsManager usuariosEnMemoria() {
		UserDetails admin = User
			.withDefaultPasswordEncoder()
			.username("admin")
			.password("admin123")
			.roles("ADMIN")
			.build();

		UserDetails usuario = User
			.withDefaultPasswordEncoder()
			.username("user")
			.password("user123")
			.roles("USER")
			.build();

		return new InMemoryUserDetailsManager(admin, usuario);
	}
}
