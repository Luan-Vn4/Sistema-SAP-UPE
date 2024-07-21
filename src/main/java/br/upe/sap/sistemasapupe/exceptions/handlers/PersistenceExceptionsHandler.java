package br.upe.sap.sistemasapupe.exceptions.handlers;

import org.apache.coyote.BadRequestException;
import org.postgresql.util.PSQLState;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

import java.sql.SQLException;
import java.util.logging.Logger;

@ControllerAdvice
public class PersistenceExceptionsHandler {

    Logger log = Logger.getLogger(this.getClass().getName());

    @ExceptionHandler(SQLException.class)
    public ResponseEntity<?> handleSQLException(final SQLException ex) throws BadRequestException {
        log.warning("Erro ao executar SQL: " + ex.getMessage());

        if (isPossibleInvalidDataSent(ex)) {
            throw new BadRequestException("Alguns dos dados fornecidos violam restrições");
        }
        throw new InternalError(ex.getMessage(), ex);
    }

    private boolean isPossibleInvalidDataSent(SQLException ex) {
        String state = ex.getSQLState();
        return state.equals(PSQLState.NOT_NULL_VIOLATION.getState())
               || state.equals(PSQLState.UNIQUE_VIOLATION.getState())
               || state.equals(PSQLState.CHECK_VIOLATION.getState());
    }

}
