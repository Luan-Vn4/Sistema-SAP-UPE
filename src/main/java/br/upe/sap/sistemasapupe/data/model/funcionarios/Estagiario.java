package br.upe.sap.sistemasapupe.data.model.funcionarios;

import lombok.*;
import java.util.UUID;

@Getter @Setter
@NoArgsConstructor
@EqualsAndHashCode(callSuper = true)
public class Estagiario extends Funcionario{

    private Tecnico supervisor;

    @Builder(builderMethodName = "estagiarioBuilder")
    public Estagiario(Integer id, UUID uid, String nome, String sobrenome, String email, String senha,
                      String urlImagem, boolean isAtivo, Tecnico supervisor) {
        super(id, uid, nome, sobrenome, email, senha, urlImagem, isAtivo);
        this.supervisor = supervisor;
    }

    @Override
    public Cargo getCargo() {
        return Cargo.ESTAGIARIO;
    }

}
