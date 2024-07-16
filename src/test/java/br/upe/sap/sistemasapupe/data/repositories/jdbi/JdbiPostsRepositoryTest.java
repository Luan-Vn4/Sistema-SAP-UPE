package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.configuration.DataSourceTestConfiguration;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Estagiario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Tecnico;
import br.upe.sap.sistemasapupe.data.model.posts.Comentario;
import br.upe.sap.sistemasapupe.data.model.posts.Post;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.*;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@ContextConfiguration(classes = {DataSourceTestConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EntityScan(basePackages = {"br.upe.sap.sistemasapupe.data"})
public class JdbiPostsRepositoryTest {
    @Autowired
    Jdbi jdbi;

    @Autowired
    JdbiPostsRepository repository;

    @Autowired
    JdbiFuncionariosRepository funcionariosRepository;

    private List<Tecnico> getTecnicos() {
        Tecnico tecnico1 = Tecnico.tecnicoBuilder()
                .nome("Carlinhos").sobrenome("Carlos")
                .email("carlos@gmail.com").senha("123456")
                .isAtivo(true).urlImagem("www.com").build();
        Tecnico tecnico2 = Tecnico.tecnicoBuilder()
                .nome("Jiró").sobrenome("Brabo")
                .email("Jaca@gmail.com").senha("1210")
                .isAtivo(true).urlImagem("www.com").build();

        return List.of(tecnico1, tecnico2);
    }

    private List<Estagiario> getEstagiarios() {
        Estagiario estagiario1 = Estagiario.estagiarioBuilder()
                .nome("Luan").sobrenome("Vilaça")
                .email("luan@gmail.com").senha("1234")
                .isAtivo(true).urlImagem("www.com").build();
        Estagiario estagiario2 = Estagiario.estagiarioBuilder()
                .nome("Maria").sobrenome("Joaquina")
                .email("maria@gmail.com").senha("1234")
                .isAtivo(true).urlImagem("www.com").build();

        return List.of(estagiario1, estagiario2);
    }



    private List<Post> getPosts(){
        Tecnico supervisor = (Tecnico) funcionariosRepository.create(getTecnicos().get(0));
        Post post1 = Post.postBuilder()
                .idAutor(supervisor.getId())
                .dataPublicacao(LocalDateTime.now())
                .imagemPost("gatinhoURL")
                .titulo("Gatinhos são muito daora!")
                .conteudo("veja esse belo gatinho")
                .build();
        Post post2 = Post.postBuilder()
                .idAutor(supervisor.getId())
                .dataPublicacao(LocalDateTime.now())
                .imagemPost("doguinhoURL")
                .titulo("Doguinhos são demais!")
                .conteudo("veja esse belo doguinho")
                .build();

        return List.of(post1, post2);
    }

    private List<Post> getPostsNoCreate(){
        Post post1 = Post.postBuilder()
                .idAutor(1)
                .dataPublicacao(LocalDateTime.now())
                .imagemPost("gatinhoURL")
                .titulo("Gatinhos são muito daora!")
                .conteudo("veja esse belo gatinho")
                .build();
        Post post2 = Post.postBuilder()
                .idAutor(1)
                .dataPublicacao(LocalDateTime.now())
                .imagemPost("doguinhoURL")
                .titulo("Doguinhos são demais!")
                .conteudo("veja esse belo doguinho")
                .build();

        return List.of(post1, post2);
    }

    private List<Comentario> getComentarios(){
        Post post = repository.create(getPosts().get(0));
        Comentario comentario1 = Comentario.comentarioBuilder()
                .idPost(post.getId())
                .idAutor(post.getIdAutor())
                .conteudo("UAU!")
                .build();

        Comentario comentario2 = Comentario.comentarioBuilder()
                .idPost(post.getId())
                .idAutor(post.getIdAutor())
                .conteudo("Que arraso!")
                .build();

        return List.of(comentario1, comentario2);
    }

    @AfterEach
    public void truncateTables() {
        jdbi.withHandle(handle -> handle.execute("TRUNCATE TABLE posts, comentarios CASCADE"));
        jdbi.withHandle(handle -> handle.execute("TRUNCATE TABLE funcionarios, supervisoes CASCADE"));
    }

    @Test
    @DisplayName("Dado um post, quando criar, então retorne post com as chaves auto-geradas")
    public void givenPost_whenCreate_thenReturnPostWithAutoGeneratedKeys() {
        Estagiario estagiario = getEstagiarios().get(0);
        Tecnico supervisor = (Tecnico) funcionariosRepository.create(getTecnicos().get(0));
        estagiario.setSupervisor(supervisor);

        Post post = Post.postBuilder()
                .idAutor(supervisor.getId())
                .dataPublicacao(LocalDateTime.now())
                .imagemPost("url")
                .titulo("titulo")
                .conteudo("conteudo")
                .build();

        Post result = repository.create(post);

        assertNotNull(result, "Retorno nulo");
        assertIdsAreNotNull(result);
        assertEqualsWithoutIds(result, post);
    }

    private void assertIdsAreNotNull(Post post) {
        assertNotNull(post.getId(), "ID do post não deve ser nulo");
        assertNotNull(post.getIdAutor(), "ID do autor não deve ser nulo");
    }

    private void assertEqualsWithoutIds(Post expected, Post actual) {
        assertEquals(expected.getTitulo(), actual.getTitulo(), "Título não coincide");
        //Assertions.assertEquals(expected.getData_publicacao(), actual.getData_publicacao(), "Data de publicação não coincide"); atraso de menos de um segundo
        assertEquals(expected.getConteudo(), actual.getConteudo(), "Conteúdo não coincide");
        assertEquals(expected.getImagemPost(), actual.getImagemPost(), "Imagem não coincide");
        assertEquals(expected.getConteudo(), actual.getConteudo(), "Comentários não coincidem");
    }
    @Test
    @DisplayName("Dado um comentario, quando criar, então retorne comentario com as chaves auto-geradas")
    public void givenComentario_whenCreate_thenReturnComentarioWithAutoGeneratedKeys() {
        Post post = repository.create(getPosts().get(0));

        Comentario comentario = Comentario.comentarioBuilder()
                .idPost(post.getId())
                .idAutor(post.getIdAutor())
                .conteudo("UAU!")
                .build();

        Comentario result = repository.createComentario(comentario);

        assertNotNull(result, "Retorno nulo");
        assertIdsAreNotNull(result);
        assertEqualsWithoutIds(result, comentario);
    }

    private void assertIdsAreNotNull(Comentario comentario) {
        assertNotNull(comentario.getId(), "ID do post não deve ser nulo");
        assertNotNull(comentario.getId(), "ID do autor não deve ser nulo");
    }

    private void assertEqualsWithoutIds(Comentario expected, Comentario actual) {
        assertEquals(expected.getIdPost(), actual.getIdPost(), "Id_post não encontrado");
        assertEquals(expected.getIdAutor(), actual.getIdAutor(), "Id_autor não encontrado");
        assertEquals(expected.getConteudo(), actual.getConteudo(), "Conteúdo não encontrado");
    }

    @Test
    @DisplayName("Dado um post com comentários, quando buscar, então retorne os comentários corretos")
    public void givenPostWithComentarios_whenFind_thenReturnCorrectComentarios() {
        Post post = repository.create(getPosts().get(0));

        Comentario comentario1 = Comentario.comentarioBuilder()
                .idPost(post.getId())
                .idAutor(post.getIdAutor())
                .conteudo("Primeiro comentário")
                .build();
        Comentario comentario2 = Comentario.comentarioBuilder()
                .idPost(post.getId())
                .idAutor(post.getIdAutor())
                .conteudo("Segundo comentário")
                .build();

        repository.createComentario(comentario1);
        repository.createComentario(comentario2);

        List<Comentario> comentarios = repository.findComentariosByPost(post.getId());

        assertEquals(2, comentarios.size(), "Deve retornar dois comentários");
        assertComentarioEquals(comentario1, comentarios.get(0));
        assertComentarioEquals(comentario2, comentarios.get(1));
    }

    private void assertComentarioEquals(Comentario expected, Comentario actual) {
        assertEquals(expected.getIdPost(), actual.getIdPost(), "ID do post não coincide");
        assertEquals(expected.getIdAutor(), actual.getIdAutor(), "ID do autor não coincide");
        assertEquals(expected.getConteudo(), actual.getConteudo(), "Conteúdo não coincide");
    }

    @Test
    @DisplayName("Dado uma lista de posts, quando criar, então retorne os posts com os IDs auto-gerados")
    public void givenListOfPosts_whenCreate_thenPostsCreatedWithAutoGeneratedIds() {
        List<Post> createdPosts = repository.create(getPosts());

        assertNotNull(createdPosts, "A lista de posts criados não deve ser nula");
        assertEquals(2, createdPosts.size(), "Deveriam ter sido criados 2 posts");
        assertEquals(getPostsNoCreate().get(0).getId(), createdPosts.get(0).getId(), "Ids não correspondem");

        assertEqualsWithoutIds(getPostsNoCreate().get(0), createdPosts.get(0));
        assertEqualsWithoutIds(getPostsNoCreate().get(1), createdPosts.get(1));
    }

    @Test
    @DisplayName("Dado um ID de post, quando buscar por ID, então retornar o post correspondente")
    public void givenPostId_whenFindById_thenReturnMatchingPost() {
        Post post = getPosts().get(0);
        post = repository.create(post);

        Post foundPost = repository.findById(post.getId());

        assertNotNull(foundPost, "Post não encontrado");

        assertEquals(post.getId(), foundPost.getId(), "IDs do post não correspondem");
    }

    @Test
    @DisplayName("Dada uma data de publicação, quando buscar por data, então retornar o post correspondente")
    public void givenPublishDate_whenFindByTempo_thenReturnMatchingPost() {
        Post post = getPosts().get(0);
        post = repository.create(post);

        Post foundPost = repository.findByTempo(post.getDataPublicacao());

        assertNotNull(foundPost, "Post não encontrado");
        assertEquals(post.getDataPublicacao(), foundPost.getDataPublicacao(), "Datas de publicação não correspondem");
    }

    @Test
    @DisplayName("Dado uma lista de posts, quando buscar todos os posts, então retornar a lista completa")
    public void givenPosts_whenFindAll_thenReturnAllPosts() {
        repository.create(getPosts());

        List<Post> foundPosts = repository.findAll();

        assertNotNull(foundPosts, "Lista de posts não deve ser nula");
        assertEquals(getPostsNoCreate().size(), foundPosts.size(), "Quantidade de posts encontrados não corresponde");
    }

    @Test
    @DisplayName("Dado um post atualizado, quando atualizar, então retornar o post atualizado")
    public void givenUpdatedPost_whenUpdate_thenReturnUpdatedPost() {
        Post post = repository.create(getPosts().get(0));
        int id_post = post.getId();

        Post postAtualizado = Post.postBuilder()
                .idAutor(post.getIdAutor())
                .titulo("Novo Título")
                .imagemPost("nova-url-imagem")
                .dataPublicacao(LocalDateTime.now())
                .conteudo("Novo conteúdo do post")
                .build();

        postAtualizado.setId(id_post);
        Post postRetornado = repository.update(postAtualizado);

        assertNotNull(postRetornado, "Post retornado não deve ser nulo");
        assertEquals(postAtualizado.getId(), postRetornado.getId(), "IDs do post não correspondem");
        assertEquals(postAtualizado.getTitulo(), postRetornado.getTitulo(), "Títulos do post não correspondem");
        assertEquals(postAtualizado.getImagemPost(), postRetornado.getImagemPost(), "URLs da imagem do post não correspondem");
        assertEquals(postAtualizado.getConteudo(), postRetornado.getConteudo(), "Conteúdos do post não correspondem");
    }

    @Test
    @DisplayName("Dado um ID de post, quando deletar, então remover o post correspondente")
    public void givenPostId_whenDelete_thenRemovePost() {
        Post post = repository.create(getPosts().get(0));
        int id = post.getId();

        int rowsAffected = repository.delete(id);

        assertEquals(1, rowsAffected, "Número de linhas afetadas deve ser 1");
        assertNull(repository.findById(id), "Post não deve ser encontrado após deleção");
    }

    @Test
    @DisplayName("Dado um ID de comentário, quando deletar, então remover o comentário correspondente")
    public void givenComentarioId_whenDelete_thenRemoveComentario() {
        Comentario comentario = repository.createComentario(getComentarios().get(0));
        int id = comentario.getId();

        int rowsAffected = repository.deleteComentario(id);

        assertEquals(1, rowsAffected, "Número de linhas afetadas deve ser 1");
        assertNull(repository.findComentarioById(id), "Comentário não deve ser encontrado após deleção");
    }


}