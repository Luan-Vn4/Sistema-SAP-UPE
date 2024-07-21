package br.upe.sap.sistemasapupe.api.dtos.funcionarios;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Cargo;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Estagiario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Tecnico;
import lombok.Builder;

import java.util.UUID;

@Builder
public record FuncionarioDTO (UUID uid, String nome, String sobrenome, String email, Cargo cargo,
                              String urlImagem, Boolean isAtivo) {

    public static FuncionarioDTO from(Funcionario funcionario) {
        return new FuncionarioDTO(funcionario.getUid(), funcionario.getNome(),
            funcionario.getSobrenome(), funcionario.getEmail(),
            funcionario.getCargo(), funcionario.getUrlImagem(), funcionario.isAtivo());
    }

    public Funcionario toFuncionario() {
        if (cargo.equals(Cargo.TECNICO)) {
            return Tecnico.tecnicoBuilder()
                .uid(uid())
                .nome(nome())
                .sobrenome(sobrenome())
                .email(email())
                .urlImagem(urlImagem())
                .isAtivo(isAtivo()).build();
        }
        return Estagiario.estagiarioBuilder()
            .uid(uid())
            .nome(nome())
            .sobrenome(sobrenome())
            .email(email())
            .urlImagem(urlImagem())
            .isAtivo(isAtivo())
            .supervisor(null)
            .build();
    }

}
