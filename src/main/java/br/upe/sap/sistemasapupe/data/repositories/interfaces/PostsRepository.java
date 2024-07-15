package br.upe.sap.sistemasapupe.data.repositories.interfaces;

import br.upe.sap.sistemasapupe.data.model.posts.Comentario;
import br.upe.sap.sistemasapupe.data.model.posts.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;

public interface PostsRepository extends BasicRepository<Post, UUID> {

    Post create(Post novoPosto);

    Comentario createComentario(Comentario novoComentario);

    List<Post> findAll();

    Post findById(UUID idPost);

    Post findByTempo(LocalDateTime tempoInicio, LocalDateTime tempoFim);

    List<Comentario> findComentariosByPost(UUID idPost);

    Post update(Post postAtualizado);

    void delete(UUID idPost);

    void deleteComentario(UUID idComentario);

}
