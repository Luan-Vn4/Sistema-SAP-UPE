package br.upe.sap.sistemasapupe.api.dtos.paciente;

import br.upe.sap.sistemasapupe.api.dtos.grupo.GrupoTerapeuticoDTO;

import java.util.UUID;

public record UpdateFichaDTO (UUID uid, int idResponsavel, String nome, GrupoTerapeuticoDTO grupoTerapeutico){

}
