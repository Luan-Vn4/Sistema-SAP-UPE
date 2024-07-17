package br.upe.sap.sistemasapupe.exceptions.handlers;

import br.upe.sap.sistemasapupe.exceptions.handlers.responses.ExceptionBody;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@ControllerAdvice
@Order(Ordered.LOWEST_PRECEDENCE)
@SuppressWarnings("DefaultAnnotationParam")
public class UnhandledExceptionHandler extends ResponseEntityExceptionHandler {

    private static final Logger log = LoggerFactory.getLogger(UnhandledExceptionHandler.class);

    @ExceptionHandler(Exception.class)
    public ResponseEntity<ExceptionBody> exception(Exception exception, HttpServletRequest request) {
        log.error(exception.getMessage(), exception);

        var response = ExceptionBody.builder()
            .HttpStatus(HttpStatus.INTERNAL_SERVER_ERROR.value())
            .error("Erro Interno do Servidor")
            .message("Ocorreu um erro inesperado no servidor, tente novamente mais tarde")
            .path(request.getRequestURL().toString())
            .timeStamp(Instant.now())
            .build();

        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(response);
    }




}
