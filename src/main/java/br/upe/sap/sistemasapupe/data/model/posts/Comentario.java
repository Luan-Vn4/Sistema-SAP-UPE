package br.upe.sap.sistemasapupe.data.model.posts;

import jakarta.persistence.Column;
import jakarta.persistence.Id;
import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString
public class Comentario {

    @Id
    @Column(name = "id")
    private int id;
    @Column(name = "id_post")
    private int idPost;
    @Column(name = "id_autor")
    private int idAutor;
    @Column(name = "conteudo")
    private String conteudo;

    @Builder(builderMethodName = "comentarioBuilder")
    public Comentario(int idPost,int idAutor, String conteudo) {
        this.idAutor = idAutor;
        this.conteudo = conteudo;
        this.idPost = idPost;
    }

}
