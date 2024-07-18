package br.upe.sap.sistemasapupe.data.repositories.interfaces;

import br.upe.sap.sistemasapupe.data.model.posts.Comentario;
import br.upe.sap.sistemasapupe.data.model.posts.Post;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;

@Repository
public interface PostsRepository extends BasicRepository<Post, Integer> {

    Comentario createComentario(Comentario novoComentario);

    Post findByTempo(LocalDateTime data_publicacao);

    List<Comentario> findComentariosByPost(int idPost);

    Comentario findComentarioById(int id);

    void deleteComentariosByPostId(int postId);

    int deleteComentario(int idComentario);

}
