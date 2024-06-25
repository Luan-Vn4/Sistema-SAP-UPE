package br.upe.sap.sistemasapupe.exceptions;

import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

public class ScheduleException extends RuntimeException {

    private static final DateTimeFormatter formater = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    public ScheduleException(LocalDateTime tempoInicio, LocalDateTime tempoFim) {
        super("Agendamento inválido para tempo de início " +
                formater.format(tempoInicio) +
                " e tempo de finalização " +
                formater.format(tempoFim));
    }
}
