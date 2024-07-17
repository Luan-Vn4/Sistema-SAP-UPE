package br.upe.sap.sistemasapupe.api.dtos;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.posts.Comentario;
import br.upe.sap.sistemasapupe.data.model.posts.Post;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FichaRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import lombok.AllArgsConstructor;
import lombok.Builder;
import org.springframework.beans.factory.annotation.Autowired;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

@Builder
public record PostDTO(Integer idPost, UUID idAutor, String titulo, LocalDateTime dataPublicacao,
                      String conteudo, String imagemPost) {

    public static PostDTO from(Post post, FuncionarioRepository funcionarioRepository) {
        UUID idAutor = funcionarioRepository.findByIdInteger(post.getIdAutor()).getUid();
        return new PostDTO(post.getId(), idAutor, post.getTitulo(), post.getDataPublicacao(),
                post.getConteudo(), post.getImagemPost());
    }
}
