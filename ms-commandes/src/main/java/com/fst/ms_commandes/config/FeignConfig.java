package com.fst.ms_commandes.config;

import feign.FeignException;
import feign.RequestInterceptor;
import feign.codec.ErrorDecoder;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.oauth2.server.resource.authentication.JwtAuthenticationToken;

@Configuration
public class FeignConfig {

    @Bean
    public RequestInterceptor bearerTokenInterceptor() {
        return requestTemplate -> {

            var authentication = SecurityContextHolder
                    .getContext()
                    .getAuthentication();

            if (authentication instanceof JwtAuthenticationToken jwtAuthentication) {

                String token = jwtAuthentication.getToken().getTokenValue();

                requestTemplate.header(
                        "Authorization",
                        "Bearer " + token
                );
            }
        };
    }

    @Bean
    public ErrorDecoder feignErrorDecoder() {
        return (methodKey, response) ->
                FeignException.errorStatus(methodKey, response);
    }
}