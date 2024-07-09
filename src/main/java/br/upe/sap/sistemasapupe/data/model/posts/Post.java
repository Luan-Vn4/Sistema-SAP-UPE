package br.upe.sap.sistemasapupe.data.model.posts;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import lombok.*;
import java.time.LocalDateTime;
import java.util.List;

@Getter @Setter
@NoArgsConstructor
@ToString
public class Post {

    private int id;
    private Funcionario autor;
    private String titulo;
    private LocalDateTime dataPublicacao;
    private String conteudo;
    private String imagem;
    private List<Comentario> comentarios;

    @Builder(builderMethodName = "postBuilder")
    public Post(Funcionario autor, String titulo, LocalDateTime dataPublicacao,
                String conteudo, String imagem, List<Comentario> comentarios) {
        this.autor = autor;
        this.titulo = titulo;
        this.dataPublicacao = dataPublicacao;
        this.conteudo = conteudo;
        this.imagem = imagem;
        this.comentarios = comentarios;
    }
}