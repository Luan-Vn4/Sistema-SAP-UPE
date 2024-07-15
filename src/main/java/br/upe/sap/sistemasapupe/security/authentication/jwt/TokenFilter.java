package br.upe.sap.sistemasapupe.security.authentication.jwt;

import br.upe.sap.sistemasapupe.data.repositories.jdbi.JdbiFuncionariosRepository;
import br.upe.sap.sistemasapupe.security.authentication.services.UserDetailsServiceImpl;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class TokenFilter extends OncePerRequestFilter {

    TokenService tokenService;

    UserDetailsService userDetailsService;

    JdbiFuncionariosRepository funcionariosRepository;

    public TokenFilter(TokenService tokenService, UserDetailsServiceImpl userDetailsService, JdbiFuncionariosRepository funcionariosRepository) {
        this.tokenService = tokenService;
        this.userDetailsService = userDetailsService;
        this.funcionariosRepository = funcionariosRepository;
    }

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        if (token != null) {
            String subject = this.tokenService.validateToken(token).subject();
            String email = funcionariosRepository.findById(UUID.fromString(subject)).getEmail();
            UserDetails user = this.userDetailsService.loadUserByUsername(email);

            var authentication = new UsernamePasswordAuthenticationToken(
                    user, null, user.getAuthorities());
            authentication.setDetails(new WebAuthenticationDetailsSource().buildDetails(request));

            SecurityContextHolder.getContext().setAuthentication(authentication);
        }
        filterChain.doFilter(request, response);
    }

    private String extractToken(@Nonnull HttpServletRequest request) {
        String authorization = request.getHeader("Authorization");
        if (authorization == null) return null;
        return authorization.replace("Bearer ", "");
    }

}
