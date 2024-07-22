package br.upe.sap.sistemasapupe.configuration.security;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Cargo;
import br.upe.sap.sistemasapupe.exceptions.handlers.FilterChainExceptionHandler;
import br.upe.sap.sistemasapupe.security.authentication.jwt.TokenFilter;
import lombok.AllArgsConstructor;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.http.HttpMethod;
import org.springframework.security.access.hierarchicalroles.RoleHierarchy;
import org.springframework.security.access.hierarchicalroles.RoleHierarchyImpl;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.dao.DaoAuthenticationProvider;
import org.springframework.security.config.annotation.authentication.configuration.AuthenticationConfiguration;
import org.springframework.security.config.annotation.web.builders.HttpSecurity;
import org.springframework.security.config.annotation.web.configuration.EnableWebSecurity;
import org.springframework.security.config.annotation.web.configurers.CsrfConfigurer;
import org.springframework.security.config.http.SessionCreationPolicy;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.security.web.SecurityFilterChain;
import org.springframework.security.web.authentication.UsernamePasswordAuthenticationFilter;

@Configuration
@EnableWebSecurity
@AllArgsConstructor
public class SecurityConfiguration {

    // DEPENDÊNCIAS
    UserDetailsService userDetailsService;

    TokenFilter tokenFilter;

    FilterChainExceptionHandler filterExceptionHandler;

    // CONFIGURAÇÕES
    @Bean
    static RoleHierarchy roleHierarchy() {
        return RoleHierarchyImpl
            .withDefaultRolePrefix()
            .role(Cargo.TECNICO.getRole()).implies(Cargo.ESTAGIARIO.getRole())
            .build();
    }

    @Bean
    public SecurityFilterChain securityFilterChain(HttpSecurity http) throws Exception {
        http.authorizeHttpRequests(configurer -> configurer
                // Swagger
                .requestMatchers("/v3/**").permitAll()
                .requestMatchers("/swagger-ui/**").permitAll()
                .requestMatchers("/swagger-ui/index.css").permitAll()
                // Autenticação
                .requestMatchers("/api/v1/authentication/login").permitAll()
                .requestMatchers("/api/v1/authentication/register-tecnico").permitAll()
                .requestMatchers("/api/v1/authentication/register-estagiario").hasRole(Cargo.TECNICO.getRole())
                // Funcionários
                .requestMatchers("/api/v1/funcionarios/one").hasRole(Cargo.ESTAGIARIO.getRole())
                .requestMatchers("/api/v1/funcionarios/**").hasRole(Cargo.TECNICO.getRole())
                // Posts
                .requestMatchers(HttpMethod.POST,"/api/v1/posts/**").hasRole(Cargo.TECNICO.getRole())
                .requestMatchers(HttpMethod.PUT,"/api/v1/posts/**").hasRole(Cargo.TECNICO.getRole())
                .requestMatchers(HttpMethod.DELETE,"/api/v1/posts/**").hasRole(Cargo.TECNICO.getRole())
                    // Comentários - Lógica personalizada no controller de delete
                // Atividades
                .requestMatchers(HttpMethod.PUT,"/api/v1/atividades/**").hasRole(Cargo.TECNICO.getRole())
                .requestMatchers(HttpMethod.DELETE, "/api/v1/atividades/**").hasRole(Cargo.TECNICO.getRole())
                // Salas
                .requestMatchers(HttpMethod.POST, "/api/v1/sala/**").hasRole(Cargo.TECNICO.getRole())
                .requestMatchers(HttpMethod.PUT, "/api/v1/sala/**").hasRole(Cargo.TECNICO.getRole())
                .requestMatchers(HttpMethod.DELETE, "/api/v1/sala/**").hasRole(Cargo.TECNICO.getRole())
                // Erros
                .requestMatchers("/error/**").permitAll()
                // Geral
                .anyRequest().authenticated())
            .csrf(CsrfConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class)
            .addFilterBefore(filterExceptionHandler, TokenFilter.class);

        return http.build();
    }

    @Bean
    public AuthenticationManager authenticationManager(
            AuthenticationConfiguration authConfiguration) throws Exception{
        return authConfiguration.getAuthenticationManager();
    }

    @Bean
    public PasswordEncoder passwordEncoder() {
        return new BCryptPasswordEncoder();
    }

    @Bean
    public AuthenticationProvider authenticationProvider() {
        var authProvider = new DaoAuthenticationProvider();

        authProvider.setUserDetailsService(this.userDetailsService);
        authProvider.setPasswordEncoder(passwordEncoder());

        return authProvider;
    }



}
