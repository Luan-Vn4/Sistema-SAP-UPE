package br.upe.sap.sistemasapupe.exceptions.handlers;

import br.upe.sap.sistemasapupe.exceptions.handlers.responses.ExceptionBody;
import br.upe.sap.sistemasapupe.exceptions.utils.HttpErrorUtils;
import jakarta.annotation.Nonnull;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.Ordered;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.context.request.ServletWebRequest;
import org.springframework.web.context.request.WebRequest;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;
import org.springframework.web.servlet.resource.NoResourceFoundException;
import java.time.Instant;
import java.util.logging.Logger;

@ControllerAdvice
@Order(Ordered.HIGHEST_PRECEDENCE)
public class RequestsExceptionHandler extends ResponseEntityExceptionHandler {

    Logger log = Logger.getLogger(RequestsExceptionHandler.class.getName());

    @Override
    protected ResponseEntity<Object> handleNoResourceFoundException(@Nonnull NoResourceFoundException exception,
                                                                    @Nonnull HttpHeaders headers,
                                                                    @Nonnull HttpStatusCode status,
                                                                    @Nonnull WebRequest request) {

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

    @ExceptionHandler(HttpClientErrorException.NotFound.class)
    public ResponseEntity<ExceptionBody> handleNotFoundException(HttpClientErrorException.NotFound exception,
                                                                 HttpServletRequest request) {
        var response = ExceptionBody.builder()
            .HttpStatus(HttpStatus.NOT_FOUND.value())
            .error("Não foi possível encontrar algum recurso")
            .message(exception.getMessage())
            .path(request.getRequestURL().toString())
            .timeStamp(Instant.now())
            .build();

        return ResponseEntity.status(HttpStatus.NOT_FOUND).body(response);
    }

    @ExceptionHandler(HttpClientErrorException.BadRequest.class)
    public ResponseEntity<?> handleBadRequestException(HttpClientErrorException.BadRequest exception,
                                                       HttpServletRequest request) {
        var response = ExceptionBody.builder()
            .HttpStatus(HttpStatus.BAD_REQUEST.value())
            .error("O dados da requisição são inválidos")
            .message(exception.getMessage())
            .path(request.getRequestURL().toString())
            .timeStamp(Instant.now())
            .build();

        return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(response);
    }

    @ExceptionHandler(HttpClientErrorException.UnprocessableEntity.class)
    public ResponseEntity<?> handleUnprocessableEntity(HttpClientErrorException.UnprocessableEntity exception,
                                                       HttpServletRequest request) {
        var response = ExceptionBody.builder()
            .HttpStatus(HttpStatus.UNPROCESSABLE_ENTITY.value())
            .error("O dados da requisição são semanticamente corretos, porém inválidos")
            .message(exception.getMessage())
            .path(request.getRequestURL().toString())
            .timeStamp(Instant.now())
            .build();

        return ResponseEntity.status(HttpStatus.UNPROCESSABLE_ENTITY).body(response);
    }

    @SuppressWarnings("unchecked")
    @Override
    protected ResponseEntity<Object> handleMethodArgumentNotValid(@Nonnull MethodArgumentNotValidException ex,
                                                                  @Nonnull HttpHeaders headers,
                                                                  @Nonnull HttpStatusCode status,
                                                                  @Nonnull WebRequest request) {
        String message;
        if (ex.getFieldError() != null) {
            message = "Campos inválidos: " + ex.getFieldError().getField();
        } else {
            message = "Os dados fornecidos violam alguma restrição";
        }

        log.info(message);
        if (request instanceof ServletWebRequest servletRequest) {
            return (ResponseEntity<Object>) this.handleBadRequestException(
                HttpErrorUtils.badRequestException(ex.getMessage(), null, null),
                servletRequest.getRequest());
        }
        return super.handleMethodArgumentNotValid(ex, headers, status, request);
    }

}
