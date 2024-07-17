package br.upe.sap.sistemasapupe.configuration;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Cargo;
import br.upe.sap.sistemasapupe.security.authentication.jwt.TokenFilter;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
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
public class SecurityConfiguration {

    // DEPENDÊNCIAS
    UserDetailsService userDetailsService;

    TokenFilter tokenFilter;

    public SecurityConfiguration(UserDetailsService userDetailsService, TokenFilter tokenFilter) {
        this.userDetailsService = userDetailsService;
        this.tokenFilter = tokenFilter;
    }


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
                .requestMatchers("/api/v1/authentication/login").permitAll()
                .requestMatchers("/api/v1/authentication/register-tecnico").permitAll()
                .requestMatchers("/api/v1/authentication/register-estagiario").hasRole(Cargo.TECNICO.getRole())
                .requestMatchers("/api/v1/funcionarios/**").hasRole(Cargo.TECNICO.getRole())
                .requestMatchers("/api/v1/funcionarios/**").hasRole(Cargo.TECNICO.getRole())
                .requestMatchers("/error/**").permitAll()
                .requestMatchers("/api/v1/posts").hasRole(Cargo.TECNICO.getRole())
                .requestMatchers("/api/v1/posts/**").permitAll()
                .requestMatchers("/api/v1/comentarios").permitAll()
                .requestMatchers("/api/v1/comentarios/delete/**").hasRole(Cargo.TECNICO.getRole())
                .requestMatchers("/api/v1/comentarios/**").permitAll()
                .anyRequest().authenticated())
            .csrf(CsrfConfigurer::disable)
            .sessionManagement(session -> session.sessionCreationPolicy(SessionCreationPolicy.STATELESS))
            .addFilterBefore(tokenFilter, UsernamePasswordAuthenticationFilter.class);

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
