package br.upe.sap.sistemasapupe.data.model.posts;

import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
@ToString
public class Post {
    private int id;
    private String titulo;
    private LocalDateTime dataPublicacao;
    private String conteudo;
    private String imagem;
    private List<Comentario> comentarios;


}