package com.ducnv.wsschat.config;

import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.config.Customizer;
import org.springframework.security.config.annotation.method.configuration.EnableMethodSecurity;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.oauth2.server.resource.web.access.BearerTokenAccessDeniedHandler;
import org.springframework.security.web.SecurityFilterChain;

@Configuration
@EnableMethodSecurity(securedEnabled = true)
public class SecurityConfiguration {
    @Bean
    public SecurityFilterChain filterChain(HttpSecurity httpSecurity) throws Exception {
        httpSecurity
        .cors(Customizer.withDefaults())
        .csrf(csrf -> csrf.disable())
        .authorizeHttpRequests(auth -> auth
            // .requestMatchers(HttpMethod.OPTIONS, "/**").permitAll()
            // .requestMatchers("/error").permitAll()
            .requestMatchers("/hello/**").permitAll()
            .anyRequest().permitAll()
        )
        // .oauth2ResourceServer(oauth2 -> oauth2
        //     .jwt(Customizer.withDefaults())
        //     .authenticationEntryPoint(cPoint)
        // )
        // .exceptionHandling(exception -> exception.accessDeniedHandler(new BearerTokenAccessDeniedHandler()))
        .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
        .formLogin(form -> form.disable());
        return httpSecurity.build();
    }
    
}
