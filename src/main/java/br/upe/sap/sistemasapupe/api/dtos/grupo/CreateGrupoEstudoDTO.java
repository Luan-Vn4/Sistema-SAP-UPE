package br.upe.sap.sistemasapupe.api.dtos.grupo;

import br.upe.sap.sistemasapupe.data.model.grupos.GrupoEstudo;

public record CreateGrupoEstudoDTO(String tema, String descricao, int dono) {
    public static CreateGrupoEstudoDTO from(GrupoEstudo grupoEstudo) {
        return new CreateGrupoEstudoDTO(grupoEstudo.getTema(), grupoEstudo.getDescricao(), grupoEstudo.getDono());
    }
    public static GrupoEstudo to(CreateGrupoEstudoDTO createGrupoEstudoDTO) {
        return GrupoEstudo.grupoEstudoBuilder().descricao(createGrupoEstudoDTO.descricao)
                .dono(createGrupoEstudoDTO.dono()).tema(createGrupoEstudoDTO.tema()).build();
    }
}
