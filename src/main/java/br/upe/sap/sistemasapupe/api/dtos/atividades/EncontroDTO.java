package br.upe.sap.sistemasapupe.api.dtos.atividades;

import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.api.dtos.grupo.GrupoEstudoDTO;
import lombok.Builder;

import java.util.List;
import java.util.UUID;

@Builder
public record EncontroDTO (AtividadesDTO atividadesDTO, UUID idGrupoEstudo, List<UUID> idsPresentes) {}
