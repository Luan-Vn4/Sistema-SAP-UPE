package br.upe.sap.sistemasapupe.data.model.funcionarios;


import lombok.*;

import java.util.List;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Tecnico extends Funcionario {

    private List<Estagiario> supervisionados = List.of();

    @Builder(builderMethodName = "tecnicoBuilder")
    public Tecnico(Integer id, UUID uid, String nome, String sobrenome, String email, String senha,
                   String urlImagem, boolean isAtivo, List<Estagiario> supervisionados) {
        super(id, uid, nome, sobrenome, email, senha, urlImagem, isAtivo);
        this.supervisionados = supervisionados;
    }



    @Override
    public Cargo getCargo() {
        return Cargo.TECNICO;
    }

}
