package br.upe.sap.sistemasapupe.data.model.posts;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.*;

import java.rmi.server.UID;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@ToString
public class Post {
    @Id
    @Column(name = "id")
    private int id;

    @Column(name = "id_autor")
    private int idAutor;

    @Column(name = "titulo")
    private String titulo;

    @Column(name = "data_publicacao")
    private LocalDateTime dataPublicacao;

    @Column(name = "conteudo")
    private String conteudo;

    @Column(name = "imagemPost")
    private String imagemPost;

    @Builder(builderMethodName = "postBuilder")
    public Post(int idAutor, String titulo, LocalDateTime dataPublicacao,
                String conteudo, String imagemPost) {
        this.idAutor = idAutor;
        this.titulo = titulo;
        this.dataPublicacao = dataPublicacao;
        this.conteudo = conteudo;
        this.imagemPost = imagemPost;
    }

}