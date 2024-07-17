package br.upe.sap.sistemasapupe.data.repositories.interfaces;

import br.upe.sap.sistemasapupe.data.model.posts.Comentario;
import br.upe.sap.sistemasapupe.data.model.posts.Post;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostsRepository extends BasicRepository<Post, Integer> {

    Post create(Post novoPosto);

    Comentario createComentario(Comentario novoComentario);

    List<Post> findAll();

    Post findById(Integer idPost);

    Post findByTempo(LocalDateTime data_publicacao);

    List<Comentario> findComentariosByPost(Integer idPost);

    Comentario findComentarioById(Integer id);

    Post update(Post postAtualizado);

    int delete(Integer idPost);

    void deleteComentariosByPostId(Integer postId);

    int deleteComentario(Integer idComentario);

}
