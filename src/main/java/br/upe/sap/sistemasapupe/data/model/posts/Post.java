package br.upe.sap.sistemasapupe.data.model.posts;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@AllArgsConstructor
@ToString
public class Post {

    private int id;
    private Funcionario autor;
    private String titulo;
    private LocalDateTime dataPublicacao;
    private String conteudo;
    private String imagem;
    private List<Comentario> comentarios;

}