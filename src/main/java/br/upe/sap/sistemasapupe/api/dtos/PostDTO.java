package br.upe.sap.sistemasapupe.api.dtos;

import br.upe.sap.sistemasapupe.data.model.posts.Post;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import lombok.Builder;

import java.time.LocalDateTime;
import java.util.UUID;

@Builder
public record PostDTO(Integer id, UUID idAutor, String titulo, LocalDateTime dataPublicacao,
                      String conteudo, String imagemPost) {

    public static PostDTO from(Post post, FuncionarioRepository funcionarioRepository) {
        UUID idAutor = funcionarioRepository.findByIdInteger(post.getIdAutor()).getUid();
        return new PostDTO(post.getId(), idAutor, post.getTitulo(), post.getDataPublicacao(),
                post.getConteudo(), post.getImagemPost());
    }
}
