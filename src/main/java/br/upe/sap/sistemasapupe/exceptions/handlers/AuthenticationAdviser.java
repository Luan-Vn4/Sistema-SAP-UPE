package br.upe.sap.sistemasapupe.exceptions.handlers;

import br.upe.sap.sistemasapupe.exceptions.handlers.responses.ExceptionBody;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import jakarta.servlet.http.HttpServletRequest;
import org.springframework.core.annotation.Order;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.servlet.mvc.method.annotation.ResponseEntityExceptionHandler;

import java.time.Instant;

@ControllerAdvice
@Order(1)
public class AuthenticationAdviser extends ResponseEntityExceptionHandler {

    @ExceptionHandler(TokenExpiredException.class)
    public ResponseEntity<ExceptionBody> handleTokenExpiredException(TokenExpiredException exception,
                                                 HttpServletRequest request) {

        var response = ExceptionBody.builder()
            .HttpStatus(HttpStatus.UNAUTHORIZED.value())
            .error("Token expirado")
            .message(exception.getMessage())
            .path(request.getRequestURL().toString())
            .timeStamp(Instant.now())
            .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(JWTVerificationException.class)
    public ResponseEntity<ExceptionBody> handleJWTVerificationException(JWTVerificationException exception,
                                                                        HttpServletRequest request) {
        var response = ExceptionBody.builder()
            .HttpStatus(HttpStatus.UNAUTHORIZED.value())
            .error("Token inválido")
            .message(exception.getMessage())
            .path(request.getRequestURL().toString())
            .timeStamp(Instant.now())
            .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }

    @ExceptionHandler(BadCredentialsException.class)
    public ResponseEntity<ExceptionBody> handleBadCredentialsException(BadCredentialsException exception,
                                                              HttpServletRequest request) {
        var response = ExceptionBody.builder()
                .HttpStatus(HttpStatus.UNAUTHORIZED.value())
                .error("Credenciais incorretas")
                .message("As credenciais fornecidas estão incorretas")
                .path(request.getRequestURL().toString())
                .timeStamp(Instant.now())
                .build();

        return ResponseEntity.status(HttpStatus.UNAUTHORIZED).body(response);
    }


}
