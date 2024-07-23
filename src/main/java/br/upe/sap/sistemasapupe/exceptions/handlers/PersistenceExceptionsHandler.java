package br.upe.sap.sistemasapupe.exceptions.handlers;

import br.upe.sap.sistemasapupe.exceptions.utils.HttpErrorUtils;
import jakarta.servlet.http.HttpServletRequest;
import org.apache.coyote.BadRequestException;
import org.postgresql.util.PSQLState;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;
import java.sql.SQLException;
import java.util.logging.Logger;

@ControllerAdvice
public class PersistenceExceptionsHandler {

    private final Logger log = Logger.getLogger(this.getClass().getName());

    RequestsExceptionHandler requestsExceptionHandler;

    public PersistenceExceptionsHandler(RequestsExceptionHandler requestsExceptionHandler) {
        this.requestsExceptionHandler = requestsExceptionHandler;
    }

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<?> handleSQLException(final SQLException ex, HttpServletRequest request) throws BadRequestException {
        log.warning("Erro ao executar SQL: " + ex.getMessage());

        if (isPossibleInvalidDataSent(ex)) {
            return requestsExceptionHandler.handleBadRequestException(
                HttpErrorUtils.badRequestException("Alguns dos dados fornecidos violam restrições", null, null),
                request);
        }
        throw new InternalError(ex.getMessage(), ex);
    }

    private boolean isPossibleInvalidDataSent(SQLException ex) {
        String state = ex.getSQLState();
        if (state == null || state.isEmpty()) {
            return false;
        }
        return state.equals(PSQLState.NOT_NULL_VIOLATION.getState())
           || state.equals(PSQLState.UNIQUE_VIOLATION.getState())
           || state.equals(PSQLState.CHECK_VIOLATION.getState());
    }

}
