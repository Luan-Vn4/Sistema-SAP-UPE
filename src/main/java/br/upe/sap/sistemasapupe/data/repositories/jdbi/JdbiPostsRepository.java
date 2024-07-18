package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.data.model.posts.Comentario;
import br.upe.sap.sistemasapupe.data.model.posts.Post;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.PostsRepository;
import jakarta.persistence.EntityNotFoundException;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.PreparedBatch;
import org.springframework.stereotype.Repository;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;
import java.util.stream.Collectors;

@Repository
public class JdbiPostsRepository implements PostsRepository {

    Jdbi jdbi;
    public JdbiPostsRepository(Jdbi jdbi) {this.jdbi = jdbi;}

    @Override
    public List<Post> create(List<Post> posts) {
        String sql = """
        INSERT INTO posts (id_autor, titulo, imagem_post, data_publicacao, conteudo)
        VALUES (:idAutor, :titulo, :imagemPost, :dataPublicacao, :conteudo)
        RETURNING *;
    """;

        jdbi.useHandle(handle -> {
            PreparedBatch batch = handle.prepareBatch(sql);
            for (Post post : posts) {
                batch.bindBean(post).add();
            }
            batch.execute();
        });

        return posts;
    }

    @Override
    public List<Post> update(List<Post> posts) {
        List<Post> results = new ArrayList<>();
        for (Post post : posts) results.add(this.update(post));
        return results;
    }

    @Override
    public List<Post> findById(List<Integer> ids) {
        String sql = "SELECT * FROM posts WHERE id IN (<ids>)";

        String joinedIds = ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        String formattedSql = sql.replace("<ids>", joinedIds);

        return jdbi.withHandle(handle ->
                handle.createQuery(formattedSql)
                        .mapToBean(Post.class)
                        .list()
        );
    }

    @Override
    public int delete(List<Integer> posts_ids) {
        String sql = "DELETE FROM posts WHERE id IN (<ids>)";

        String joinedIds = posts_ids.stream()
                .map(String::valueOf)
                .collect(Collectors.joining(", "));

        String formattedSql = sql.replace("<ids>", joinedIds);

        return jdbi.withHandle(handle -> handle.execute(formattedSql));
    }

    @Override
    public Post create(Post post) {
        String CREATE_POST = """
        INSERT INTO posts (id_autor, titulo, imagem_post, data_publicacao, conteudo)
        VALUES (:idAutor, :titulo, :imagemPost, :dataPublicacao, :conteudo)
        RETURNING *;
    """;

        return jdbi.withHandle(handle -> handle.createUpdate(CREATE_POST)
                .bindBean(post)
                .executeAndReturnGeneratedKeys()
                .mapToBean(Post.class)
                .findFirst()
                .orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public Comentario createComentario(Comentario comentario) {
        String CREATE_COMENTARIO = """
            INSERT INTO comentarios (id_post, id_autor, conteudo) VALUES
                (:idPost, :idAutor, :conteudo)
                RETURNING *;
            """;

        return jdbi.withHandle(handle -> handle.createUpdate(CREATE_COMENTARIO)
                .bindBean(comentario)
                .executeAndReturnGeneratedKeys()
                .mapToBean(Comentario.class)
                .findFirst().orElseThrow(EntityNotFoundException::new));
    }

    @Override
    public List<Post> findAll() {
        String QUERY = "SELECT * FROM posts";

        return jdbi.withHandle(handle ->
                handle.createQuery(QUERY)
                        .mapToBean(Post.class)
                        .list()
        );
    }

    @Override
    public Post findById(Integer idPost) {
        String FIND = """
            SELECT id, id_autor, titulo, imagem_post, data_publicacao, conteudo FROM posts
            WHERE id = :id LIMIT 1;
            """;

        return jdbi.withHandle(handle -> handle
                .createQuery(FIND)
                .bind("id", idPost)
                .mapToBean(Post.class)
                .findFirst()
                .orElse(null));
    }

    @Override
    public Post findByTempo(LocalDateTime data_publicacao) {
        String QUERY = """
            SELECT id, id_autor, titulo, imagem_post, data_publicacao, conteudo FROM posts
            WHERE data_publicacao = :dataPublicacao LIMIT 1;
            """;

        return jdbi.withHandle(handle -> handle
                .createQuery(QUERY)
                .bind("dataPublicacao", data_publicacao)
                .mapToBean(Post.class)
                .findFirst()
                .orElse(null));
    }

    @Override
    public List<Comentario> findComentariosByPost(int idPost) {
        String Query = """
                SELECT * FROM comentarios WHERE id_post = :id_post
                """;

        return jdbi.withHandle(handle ->
                handle.createQuery(Query)
                        .bind("id_post", idPost)
                        .mapToBean(Comentario.class)
                        .list()
        );
    }

    @Override
    public Comentario findComentarioById(int id) {
        String Query = "SELECT * FROM comentarios WHERE id = :id";

        return jdbi.withHandle(handle ->
                handle.createQuery(Query)
                        .bind("id", id)
                        .mapToBean(Comentario.class).
                        findFirst().orElse(null));
    }

    @Override
    public Post update(Post postAtualizado) {
        String QUERY = """
            UPDATE posts
            SET id_autor = :idAutor,
                titulo = :titulo,
                imagem_post = :imagemPost,
                data_publicacao = :dataPublicacao,
                conteudo = :conteudo
            WHERE id = :id;
            """;

        jdbi.useHandle(handle -> handle
            .createUpdate(QUERY)
            .bindBean(postAtualizado)
            .execute());

        return findById(postAtualizado.getId());
    }

    @Override
    public int delete(Integer id) {
        deleteComentariosByPostId(id);

        String sql = "DELETE FROM posts WHERE id = :id";

        return jdbi.withHandle(handle -> handle
                .createUpdate(sql)
                .bind("id", id)
                .execute());
    }

    @Override
    public void deleteComentariosByPostId(int postId) {
        String sql = "DELETE FROM comentarios WHERE id_post = :postId";
        jdbi.useHandle(handle ->
                handle.createUpdate(sql)
                        .bind("postId", postId)
                        .execute()
        );
    }

    @Override
    public int deleteComentario(int id) {
        String sql = "DELETE FROM comentarios WHERE id = :id";

        return jdbi.withHandle(handle -> handle
                .createUpdate(sql)
                .bind("id", id)
                .execute());
    }

}
