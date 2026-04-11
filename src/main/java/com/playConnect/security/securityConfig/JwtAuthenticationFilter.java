package com.playConnect.security.securityConfig;

import java.io.IOException;
import java.util.Collections;

import org.springframework.http.MediaType;
import org.springframework.lang.NonNull;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtAuthenticationFilter extends OncePerRequestFilter {

	private final JwtUtil jwtUtil;

	public JwtAuthenticationFilter(JwtUtil jwtUtil) {
		this.jwtUtil = jwtUtil;
	}

	@Override
	protected boolean shouldNotFilter(@NonNull HttpServletRequest request) {
		return !"/profile".equals(request.getServletPath());
	}

	@Override
	protected void doFilterInternal(
			@NonNull HttpServletRequest request,
			@NonNull HttpServletResponse response,
			@NonNull FilterChain filterChain) throws ServletException, IOException {

		String token = jwtUtil.resolveToken(request.getHeader("Authorization"));
		if (token == null || token.isBlank()) {
			unauthorized(response, "Missing authorization token");
			return;
		}
		try {
			Long userId = jwtUtil.extractUserId(token);
			String email = jwtUtil.extractEmail(token);
			JwtUserPrincipal principal = new JwtUserPrincipal(userId, email);
			UsernamePasswordAuthenticationToken auth = new UsernamePasswordAuthenticationToken(
					principal, null, Collections.emptyList());
			SecurityContextHolder.getContext().setAuthentication(auth);
			filterChain.doFilter(request, response);
		} catch (Exception e) {
			SecurityContextHolder.clearContext();
			unauthorized(response, "Invalid or expired token");
		}
	}

	private static void unauthorized(HttpServletResponse response, String message) throws IOException {
		response.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
		response.setContentType(MediaType.APPLICATION_JSON_VALUE);
		response.setCharacterEncoding("UTF-8");
		response.getWriter().write("{\"message\":\"" + escapeJson(message) + "\"}");
	}

	private static String escapeJson(String s) {
		return s.replace("\\", "\\\\").replace("\"", "\\\"");
	}
}
