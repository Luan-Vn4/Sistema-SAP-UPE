package br.upe.sap.sistemasapupe.api.dtos;

import br.upe.sap.sistemasapupe.data.model.posts.Comentario;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import lombok.Builder;

import java.util.UUID;

@Builder
public record ComentarioDTO(Integer id, Integer idPost, UUID idAutor, String conteudo) {
    public static ComentarioDTO from(Comentario comentario, FuncionarioRepository funcionarioRepository){
        UUID idAutor = funcionarioRepository.findByIdInteger(comentario.getIdAutor()).getUid();
        return new ComentarioDTO(comentario.getId(), comentario.getIdPost(), idAutor, comentario.getConteudo());
    }
}
