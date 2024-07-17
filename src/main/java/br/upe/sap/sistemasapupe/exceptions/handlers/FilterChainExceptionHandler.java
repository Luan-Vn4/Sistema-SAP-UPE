package br.upe.sap.sistemasapupe.exceptions.handlers;

import jakarta.annotation.Nonnull;
import jakarta.servlet.FilterChain;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.OncePerRequestFilter;
import org.springframework.web.servlet.HandlerExceptionResolver;

@Component
public class FilterChainExceptionHandler extends OncePerRequestFilter {

    private final Logger log = LoggerFactory.getLogger(getClass());

    HandlerExceptionResolver exceptionResolver;

    public FilterChainExceptionHandler(@Qualifier("handlerExceptionResolver")
                                       HandlerExceptionResolver exceptionResolver) {
        this.exceptionResolver = exceptionResolver;
    }

    @Override
    protected void doFilterInternal(@Nonnull HttpServletRequest request, @Nonnull HttpServletResponse response,
                                    @Nonnull FilterChain filterChain) {
        try {
            filterChain.doFilter(request, response);
        } catch (Exception e) {
            log.debug("Erro na FilterChain do Spring", e);
            exceptionResolver.resolveException(request, response, null, e);
        }

    }
}
