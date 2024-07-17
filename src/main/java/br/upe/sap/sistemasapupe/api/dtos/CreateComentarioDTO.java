package br.upe.sap.sistemasapupe.api.dtos;

import br.upe.sap.sistemasapupe.data.model.posts.Comentario;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;

import java.util.UUID;


public record CreateComentarioDTO(int idPost, UUID idAutor, String conteudo) {
    public static Comentario toComentario(CreateComentarioDTO dto, FuncionarioRepository funcionarioRepository) {
        int idAutor = funcionarioRepository.findById(dto.idAutor).getId();
        return new Comentario(dto.idPost, idAutor, dto.conteudo);
    }
}
