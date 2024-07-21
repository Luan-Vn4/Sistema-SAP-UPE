package br.upe.sap.sistemasapupe.api.dtos.grupo;

import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import lombok.Builder;
import java.util.List;
import java.util.UUID;

@Builder
<<<<<<< HEAD
public record GrupoEstudoDTO (UUID uid, String temaEstudo, String descricao, FuncionarioDTO dono, List<FuncionarioDTO> participantes) {
    public static GrupoEstudoDTO from(GrupoEstudo grupoEstudo) {
        return null;
    }
=======
public record GrupoEstudoDTO (UUID uid, String temaEstudo, List<FuncionarioDTO> participantes) {

>>>>>>> 2396ebe1d0ee01bfc3741538ac72be5f231d471f
}
