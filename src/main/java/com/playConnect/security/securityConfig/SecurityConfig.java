package com.playConnect.security.securityConfig;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
public class SecurityConfig {

	@Bean
	public SecurityFilterChain securityFilterChain(HttpSecurity http, JwtAuthenticationFilter jwtAuthenticationFilter)
			throws Exception {

		http.cors()
				.and()
				.csrf(csrf -> csrf.disable())
				.sessionManagement(s -> s.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
				.addFilterBefore(jwtAuthenticationFilter, UsernamePasswordAuthenticationFilter.class)
				.authorizeHttpRequests(auth -> auth
						.requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
						.requestMatchers(
								"/api/auth/login",
								"/api/auth/forgot-password",
								"/api/auth/reset-password",
								"/api/user/register")
						.permitAll()
						.requestMatchers(HttpMethod.GET, "/profile").authenticated()
						.requestMatchers(HttpMethod.PUT, "/profile").authenticated()
						.anyRequest()
						.permitAll())
				.httpBasic(basic -> basic.disable())
				.formLogin(form -> form.disable());

		return http.build();
	}
}
