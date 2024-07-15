package br.upe.sap.sistemasapupe.data.model.posts;

import lombok.*;

import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@AllArgsConstructor @NoArgsConstructor
@ToString
public class Comentario {

    private int id;
    private int id_post;
    private int id_autor;
    private String conteudo;

    @Builder(builderMethodName = "comentarioBuilder")
    public Comentario(int id_post,int id_autor, String conteudo) {
        this.id_autor = id_autor;
        this.conteudo = conteudo;
        this.id_post = id_post;
    }

}
