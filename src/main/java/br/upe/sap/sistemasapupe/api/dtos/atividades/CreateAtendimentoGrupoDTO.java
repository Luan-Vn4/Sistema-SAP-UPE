package br.upe.sap.sistemasapupe.api.dtos.atividades;

import br.upe.sap.sistemasapupe.data.model.atividades.AtendimentoGrupo;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

public record CreateAtendimentoGrupoDTO(AtividadesDTO atividade, UUID idGrupoTerapeutico,
                                        List<UUID> idsParticipantes, List<UUID> idsMinistrantes) {

    public static AtendimentoGrupo to(CreateAtendimentoGrupoDTO createAtendimentoGrupoDTO) {
        return AtendimentoGrupo.builder()
                .idGrupoTerapeutico(createAtendimentoGrupoDTO.idGrupoTerapeutico() != null ?
                         : null)
                .idsParticipantes(createAtendimentoGrupoDTO.idsParticipantes()
                        .stream().map(UUID::toString).map(Integer::valueOf).toList())
                .idsMinistrantes(createAtendimentoGrupoDTO.idsMinistrantes()
                        .stream().map(UUID::toString).map(Integer::valueOf).toList())
                .build();
    }
}
