package br.upe.sap.sistemasapupe.exceptions.handlers.responses;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.Setter;

import java.time.Instant;

@Builder
@AllArgsConstructor
@Getter @Setter
public class ExceptionBody {

    private int HttpStatus;

    private String error;

    private String message;

    private String path;

    private Instant timeStamp;

}
