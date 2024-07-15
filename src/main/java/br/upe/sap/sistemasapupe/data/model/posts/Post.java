package br.upe.sap.sistemasapupe.data.model.posts;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import lombok.*;

import java.rmi.server.UID;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@ToString
public class Post {

    private int id;
    private int id_autor;
    private String titulo;
    private LocalDateTime data_publicacao;
    private String conteudo;
    private String imagem_post;
    private List<Comentario> comentarios;

    @Builder(builderMethodName = "postBuilder")
    public Post(int id_autor, String titulo, LocalDateTime data_publicacao,
                String conteudo, String imagem_post, List<Comentario> comentarios) {
        this.id_autor = id_autor;
        this.titulo = titulo;
        this.data_publicacao = data_publicacao;
        this.conteudo = conteudo;
        this.imagem_post = imagem_post;
        this.comentarios = comentarios;
    }

    @Builder(builderMethodName = "postBuilderWithId")
    public Post(int id, int id_autor, String titulo, LocalDateTime data_publicacao,
                String conteudo, String imagem_post, List<Comentario> comentarios) {
        this.id = id;
        this.id_autor = id_autor;
        this.titulo = titulo;
        this.data_publicacao = data_publicacao;
        this.conteudo = conteudo;
        this.imagem_post = imagem_post;
        this.comentarios = comentarios;
    }
}