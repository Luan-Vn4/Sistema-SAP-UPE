package br.upe.sap.sistemasapupe.api.dtos.posts;

import br.upe.sap.sistemasapupe.data.model.posts.Post;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;

import java.time.LocalDateTime;
import java.util.UUID;

public record CreatePostDTO(UUID idAutor, String titulo, LocalDateTime dataPublicacao,
                            String conteudo, String imagemPost) {
    public static Post toPost(CreatePostDTO dto, FuncionarioRepository funcionarioRepository) {
        int idAutor = funcionarioRepository.findIds(dto.idAutor).get(dto.idAutor);
        return new Post(idAutor, dto.titulo(), LocalDateTime.now(), dto.conteudo(), dto.imagemPost());
    }
}