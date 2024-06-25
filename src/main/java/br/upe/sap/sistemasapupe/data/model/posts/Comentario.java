package br.upe.sap.sistemasapupe.data.model.posts;

import lombok.*;

@Getter @Setter
@AllArgsConstructor
@ToString
public class Comentario {
    private int id;
    private int post;
    private int autor;
    private String conteudo;



}
