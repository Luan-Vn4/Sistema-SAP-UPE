package br.upe.sap.sistemasapupe.api.dtos.posts.comentarios;

import br.upe.sap.sistemasapupe.data.model.posts.Comentario;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;

import java.util.UUID;


public record CreateComentarioDTO(int idPost, UUID idAutor, String conteudo) {
    public static Comentario toComentario(CreateComentarioDTO dto, FuncionarioRepository funcionarioRepository) {
        int idAutor = funcionarioRepository.findIds(dto.idAutor).get(dto.idAutor);
        return new Comentario(dto.idPost, idAutor, dto.conteudo);
    }
}
