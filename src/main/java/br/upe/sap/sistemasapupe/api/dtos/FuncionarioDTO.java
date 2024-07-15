package br.upe.sap.sistemasapupe.api.dtos;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Cargo;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import lombok.Builder;

import java.util.UUID;

@Builder
public record FuncionarioDTO (UUID uid, String nome, String sobrenome, String email, Cargo cargo,
                              Boolean isAtivo) {

    public static FuncionarioDTO from(Funcionario funcionario) {
        return new FuncionarioDTO(funcionario.getUid(), funcionario.getNome(),
            funcionario.getSobrenome(), funcionario.getEmail(),
            funcionario.getCargo(), funcionario.isAtivo());
    }

}
