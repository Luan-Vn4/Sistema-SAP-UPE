package br.upe.sap.sistemasapupe.data.model.funcionarios;

import lombok.*;
import java.util.UUID;

@Getter @Setter
@AllArgsConstructor
@ToString
public class Funcionario {

    private int id;
    private UUID uid;
    private String urlImagem;
    private String nome;
    private String sobrenome;
    private String senha;
    private String email;
    private boolean isTecnico;
    private boolean ativo;

    public String getNomeCompleto() {
        return this.nome + " " + this.sobrenome;
    }

}
