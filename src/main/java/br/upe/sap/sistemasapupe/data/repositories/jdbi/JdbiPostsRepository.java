package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.data.model.posts.Comentario;
import br.upe.sap.sistemasapupe.data.model.posts.Post;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.PostsRepository;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;

import java.time.LocalDateTime;
import java.util.List;
import java.util.Optional;

@Repository
public class JdbiPostsRepository implements PostsRepository {

    Jdbi jdbi;
    @Override
    public List<Post> create(List<Post> posts) {
        return null;
    }

    @Override
    public List<Post> update(List<Post> posts) {
        return null;
    }

    @Override
    public List<Post> findById(List<Integer> ids) {
        return null;
    }

    @Override
    public void delete(List<Integer> integers) {

    }

    @Override
    public Post create(Post post) {
        String CREATE_POST = """
            INSERT INTO posts (id_autor, titulo, imagem_post, data_publicacao, conteudo) VALUES
                (:id_autor, :titulo, :imagem_post, :data_publicacao, :conteudo);
            """;

        return jdbi.withHandle(handle -> handle.createUpdate(CREATE_POST)
                .bindBean(post)
                .executeAndReturnGeneratedKeys()
                .mapToBean(Post.class)
                .findFirst().orElseGet(null));
    }

    @Override
    public Comentario createComentario(Comentario comentario) {
        String CREATE_COMENTARIO = """
            INSERT INTO comentarios (id_post, id_autor, conteudo) VALUES
                (:id_post, :id_autor, :conteudo);
            """;

        return jdbi.withHandle(handle -> handle.createUpdate(CREATE_COMENTARIO)
                .bindBean(comentario)
                .executeAndReturnGeneratedKeys()
                .mapToBean(Comentario.class)
                .findFirst().orElseGet(null));
    }

    @Override
    public List<Post> findAll() {
        String QUERY = """
                SELECT * FROM posts;
                """;

        return jdbi.withHandle(handle -> handle
                .createUpdate(QUERY)
                .executeAndReturnGeneratedKeys()
                .mapToBean(Post.class)
                .collectIntoList());
    }

    @Override
    public Post findById(Integer idPost) {
        String FIND = """
                SELECT id, id_autor, titulo, imagem_post, data_publicacao, conteudo FROM posts
                WHERE id = :id LIMIT 1;
                """;



        return jdbi.withHandle(handle -> handle
                .createUpdate(FIND)
                .executeAndReturnGeneratedKeys()
                .mapToBean(Post.class)
                .findFirst().orElseGet(null));
    }

    @Override
    public Post findByTempo(LocalDateTime data_publicacao) {
        String QUERY = """
                SELECT id, id_autor, titulo, imagem_post, data_publicacao, conteudo FROM posts
                WHERE data_publicacao = :data_publicacao LIMIT 1;
                """;

        return jdbi.withHandle(handle -> handle
                .createUpdate(QUERY)
                .executeAndReturnGeneratedKeys()
                .mapToBean(Post.class)
                .findFirst().orElseGet(null));
    }

    @Override
    public List<Comentario> findComentariosByPost(Integer idPost) {
        return null;
    }

    @Override
    public Post update(Post postAtualizado) {
        String QUERY = """
                UPDATE id_autor = :id_autor, titulo = :titulo, imagem_post = :imagem_post, data_publicacao = :data_publicacao, conteudo = :conteudo
                FROM posts
                WHERE id = :id LIMIT 1;
                """;

        return jdbi.withHandle(handle -> handle
                .createUpdate(QUERY)
                .executeAndReturnGeneratedKeys()
                .mapToBean(Post.class)
                .findFirst().orElseGet(null));
    }

    @Override
    public void delete(Integer idPost) {

    }

    @Override
    public void deleteComentario(Integer idComentario) {

    }
}
