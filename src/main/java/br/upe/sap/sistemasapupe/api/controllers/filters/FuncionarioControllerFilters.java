package br.upe.sap.sistemasapupe.api.controllers.filters;

import br.upe.sap.sistemasapupe.api.controllers.FuncionarioController;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Cargo;
import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.springframework.security.access.AccessDeniedException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.filter.OncePerRequestFilter;

import java.io.IOException;
import java.util.List;
import java.util.Map;

public class FuncionarioControllerFilters extends OncePerRequestFilter {

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) throws ServletException, IOException{

        String contextPath = request.getContextPath();

        switch (contextPath) {
            case "/api/v1/funcionarios/many" -> filterGetMany(request, response, filterChain);
        }

    }

    private void filterGetMany(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response,
                               @Nonnull FilterChain filterChain) throws ServletException, IOException {
        Map<String, String[]> params = request.getParameterMap();
        List<String> authorities = SecurityContextHolder.getContext()
                .getAuthentication()
                .getAuthorities().stream()
                .map(GrantedAuthority::getAuthority).toList();

        if (!params.containsKey("by") && !authorities.contains(Cargo.TECNICO.getRole()))
            throw new AccessDeniedException("Apenas técnicos podem acessar esse recurso");

        var type = FuncionarioController.Search.from(params.get("by")[0]);
        if (type == FuncionarioController.Search.ATIVOS) {
            if (!authorities.contains(Cargo.TECNICO.getRole()));
        }

    }

}
