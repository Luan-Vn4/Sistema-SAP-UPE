package br.upe.sap.sistemasapupe.security.authentication.jwt;

import br.upe.sap.sistemasapupe.security.authentication.dtos.login.TokenDTO;
import br.upe.sap.sistemasapupe.security.authentication.services.UserDetailsServiceImpl;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.web.authentication.WebAuthenticationDetailsSource;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.UUID;

@Component
public class TokenFilter extends OncePerRequestFilter {

    UserDetailsServiceImpl userDetailsService;

    TokenService tokenService;

    public TokenFilter(TokenService authService, UserDetailsServiceImpl userDetailsService) {
        this.tokenService = authService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException {
        String token = extractToken(request);
        if (token != null) {
            TokenDTO tokenDTO = this.tokenService.validateToken(token);
            UserDetails user = this.userDetailsService.loadUserByUID(UUID.fromString(tokenDTO.subject()));

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
