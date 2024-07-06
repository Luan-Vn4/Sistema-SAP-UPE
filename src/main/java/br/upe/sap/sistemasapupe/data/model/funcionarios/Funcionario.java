package br.upe.sap.sistemasapupe.data.model.funcionarios;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import lombok.*;
import java.util.UUID;

@ToString
@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@Table(name = "funcionarios")
@Entity
public abstract class Funcionario {

    @Id
    @Column(name = "id")
    protected Integer id;

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

    @Column(name="is_ativo")
    private boolean isAtivo = true;

    public String getNomeCompleto() {
        return this.nome + " " + this.sobrenome;
    }

    public abstract Cargo getCargo();

}
