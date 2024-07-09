package br.upe.sap.sistemasapupe.data.repositories.interfaces;

import br.upe.sap.sistemasapupe.data.model.posts.Comentario;
import br.upe.sap.sistemasapupe.data.model.posts.Post;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

public interface PostsRepository extends BasicRepository<Post, Integer> {

    Post create(Post novoPosto);

    Comentario createComentario(Comentario novoComentario);

    List<Post> findAll();

    Post findById(Integer idPost);

    Post findByTempo(LocalDateTime data_publicacao);

    List<Comentario> findComentariosByPost(Integer idPost);

    Post update(Post postAtualizado);

    void delete(Integer idPost);

    void deleteComentario(Integer idComentario);

}
