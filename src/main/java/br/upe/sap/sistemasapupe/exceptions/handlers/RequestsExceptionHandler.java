package br.upe.sap.sistemasapupe.exceptions.handlers;

import br.upe.sap.sistemasapupe.exceptions.handlers.responses.ExceptionBody;
import jakarta.annotation.Nonnull;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;

import java.time.Instant;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestsExceptionHandler extends ResponseEntityExceptionHandler {

    @Override

    protected ResponseEntity<Object> handleNoResourceFoundException(@Nonnull NoResourceFoundException exception,
                                                                    @Nonnull HttpHeaders headers, @Nonnull HttpStatusCode status, @Nonnull WebRequest request) {

        if (request instanceof ServletWebRequest servletRequest) {
            var response = ExceptionBody.builder()
                    .HttpStatus(HttpStatus.NOT_FOUND.value())
                    .error("Nenhum recurso encontrado")
                    .message("Nenhum recurso foi encontrado para o caminho especificado")
                    .path(servletRequest.getRequest().getRequestURL().toString())
                    .timeStamp(Instant.now())
                    .build();

            return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
        }
        return super.handleNoResourceFoundException(exception, headers, status, request);
    }

}
