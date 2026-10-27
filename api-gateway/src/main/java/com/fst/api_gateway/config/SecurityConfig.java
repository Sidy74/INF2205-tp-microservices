package com.fst.api_gateway.config;

import jakarta.servlet.http.HttpServletResponse;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationConverter;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.access.AccessDeniedHandler;

import java.util.Collection;
import java.util.List;
import java.util.Map;

@Configuration
@EnableWebSecurity
public class SecurityConfig {

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {

        http
                .csrf(csrf -> csrf.disable())
                .authorizeHttpRequests(auth -> auth

                        .requestMatchers("/actuator/**").permitAll()

                        // Produits
                        .requestMatchers(HttpMethod.GET, "/api/produits/**")
                        .hasAnyRole("CLIENT", "GESTIONNAIRE", "ADMIN")

                        .requestMatchers(HttpMethod.POST, "/api/produits/**")
                        .hasAnyRole("GESTIONNAIRE", "ADMIN")

                        .requestMatchers(HttpMethod.PUT, "/api/produits/**")
                        .hasAnyRole("GESTIONNAIRE", "ADMIN")

                        .requestMatchers(HttpMethod.DELETE, "/api/produits/**")
                        .hasRole("ADMIN")

                        // Commandes
                        .requestMatchers(HttpMethod.POST, "/api/commandes/**")
                        .hasRole("CLIENT")

                        .requestMatchers(HttpMethod.GET, "/api/commandes/**")
                        .hasAnyRole("GESTIONNAIRE", "ADMIN")

                        .requestMatchers(HttpMethod.PATCH, "/api/commandes/**")
                        .hasAnyRole("GESTIONNAIRE", "ADMIN")

                        .anyRequest().authenticated()
                )
                .exceptionHandling(exception -> exception
                        .accessDeniedHandler(accessDeniedHandler())
                )
                .oauth2ResourceServer(oauth2 -> oauth2
                        .jwt(jwt -> jwt.jwtAuthenticationConverter(jwtAuthenticationConverter()))
                );

        return http.build();
    }

    @Bean
    public JwtAuthenticationConverter jwtAuthenticationConverter() {

        JwtAuthenticationConverter converter = new JwtAuthenticationConverter();

        converter.setJwtGrantedAuthoritiesConverter(jwt -> {

            Map<String, Object> realmAccess = jwt.getClaim("realm_access");

            if (realmAccess == null) {
                return List.of();
            }

            Object rolesObject = realmAccess.get("roles");

            if (!(rolesObject instanceof Collection<?> roles)) {
                return List.of();
            }

            return roles.stream()
                    .map(Object::toString)
                    .map(role -> (GrantedAuthority) new SimpleGrantedAuthority("ROLE_" + role))
                    .toList();
        });

        return converter;
    }

    @Bean
    public AccessDeniedHandler accessDeniedHandler() {
        return (request, response, accessDeniedException) -> {
            response.setStatus(HttpServletResponse.SC_FORBIDDEN);
            response.setContentType("application/json");
            response.setCharacterEncoding("UTF-8");

            response.getWriter().write("""
        {
          "error": "Accès refusé",
          "message": "Vous n'avez pas les droits nécessaires pour effectuer cette action."
        }
        """);
        };
    }
}