package br.upe.sap.sistemasapupe.data.model.funcionarios;

import jakarta.persistence.Column;
import jakarta.persistence.Table;
import lombok.*;
import java.util.UUID;

@ToString
@Getter @Setter @Builder
@AllArgsConstructor @NoArgsConstructor
@Table(name = "funcionarios")
public class Funcionario {

    @Column(name = "id")
    private Integer id;

    @Column(name = "uid")
    private UUID uid;

    @Column(name = "nome")
    private String nome;

    @Column(name = "sobrenome")
    private String sobrenome;

    @Column(name = "email")
    private String email;

    @Column(name = "senha")
    private String senha;

    @Column(name = "imagem")
    private String urlImagem;

    @Column(name="is_tecnico")
    private boolean isTecnico = false;

    @Column(name="is_ativo")
    private boolean isAtivo = true;

    public String getNomeCompleto() {
        return this.nome + " " + this.sobrenome;
    }

}
