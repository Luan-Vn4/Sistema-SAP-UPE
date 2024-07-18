package br.upe.sap.sistemasapupe.api.dtos.ficha;

import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.api.dtos.grupoterapeutico.GrupoTerapeuticoDTO;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import lombok.Builder;

import java.util.UUID;

@Builder
public record FichaDTO (UUID uid, FuncionarioDTO responsavel, GrupoTerapeuticoDTO grupoTerapeutico){
    public static FichaDTO from(Ficha ficha) {
        FuncionarioDTO responsavel = FuncionarioDTO.from(ficha.getResponsavel());
        GrupoTerapeuticoDTO grupoTerapeutico = GrupoTerapeuticoDTO.from(ficha.getGrupoTerapeutico());
        return new FichaDTO(ficha.getUid(), responsavel, grupoTerapeutico);
    }
}
