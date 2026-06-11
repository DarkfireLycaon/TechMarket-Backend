package com.galvan.inventarios.config;

import java.io.IOException;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

@Component
public class JwtFilter extends OncePerRequestFilter {

    @Autowired
    private UserDetailsService userDetailsService;

    @Autowired
    private JwtUtils jwtUtils;

    @Override
    protected void doFilterInternal(HttpServletRequest request,
                                    HttpServletResponse response,
                                    FilterChain chain) throws ServletException, IOException {

        final String authorizationHeader = request.getHeader("Authorization");
        String path = request.getRequestURI();
        String username = null;
        String method = request.getMethod();
        String jwt = null;

        System.out.println("🔍 Procesando petición: " + method + " " + path);

        // Salida rápida para rutas públicas
        if (path.startsWith("/api/public/") || (method.equals("GET") && path.startsWith("/api/productos/"))) {
            System.out.println("✅ Salida rápida aplicada para: " + path);
            chain.doFilter(request, response);
            return;
        }

        if (authorizationHeader != null && authorizationHeader.startsWith("Bearer ")) {
            jwt = authorizationHeader.substring(7);
            try {
                boolean tokenValido = jwtUtils.validarToken(jwt);
                System.out.println("📌 Token válido: " + tokenValido);

                if (tokenValido) {
                    username = jwtUtils.getEmailFromToken(jwt);
                    System.out.println("📌 Email extraído del token: " + username);
                } else {
                    System.out.println("⚠️ EL TOKEN ES INVÁLIDO Y SE ESTÁ RECHAZANDO");
                    username = null; // 🌟 CORRECCIÓN: Si el token no es válido, no hay usuario.
                }
            } catch (Exception e) {
                System.out.println("❌ Error procesando token: " + e.getMessage());
                username = null;
            }
        }

        // Si tenemos username válido y no está autenticado previamente en el contexto
        if (username != null && SecurityContextHolder.getContext().getAuthentication() == null) {
            UserDetails userDetails = this.userDetailsService.loadUserByUsername(username);

            // 🌟 LÍNEA DE DIAGNÓSTICO: Ver qué roles le está asignando la BD a este usuario
            System.out.println("🔍 Roles/Autoridades reales del usuario en BD: " + userDetails.getAuthorities());

            UsernamePasswordAuthenticationToken authToken =
                    new UsernamePasswordAuthenticationToken(userDetails, null, userDetails.getAuthorities());

            authToken.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));
            SecurityContextHolder.getContext().setAuthentication(authToken);

            System.out.println("✅ Usuario autenticado en el contexto: " + username);
        }

        chain.doFilter(request, response);
    }
}