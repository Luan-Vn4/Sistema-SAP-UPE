package br.upe.sap.sistemasapupe.api.services;

import br.upe.sap.sistemasapupe.api.dtos.posts.comentarios.ComentarioDTO;
import br.upe.sap.sistemasapupe.api.dtos.posts.comentarios.CreateComentarioDTO;
import br.upe.sap.sistemasapupe.api.dtos.posts.CreatePostDTO;
import br.upe.sap.sistemasapupe.api.dtos.posts.PostDTO;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.posts.Comentario;
import br.upe.sap.sistemasapupe.data.model.posts.Post;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.PostsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class PostsService {
    PostsRepository postsRepository;
    FuncionarioRepository funcionarioRepository;

    public PostDTO convertToPostDTO(Post post) {
        UUID uuidAutor = getAutorUUID(post.getIdAutor());
        return new PostDTO(post.getId(), uuidAutor, post.getTitulo(), post.getDataPublicacao(), post.getConteudo(), post.getImagemPost());
    }

    private UUID getAutorUUID(Integer id) {
        Funcionario autor = funcionarioRepository.findById(id);

        if (autor == null) {
            throw new EntityNotFoundException("Autor não encontrado para o id: " + id);
        }

        return autor.getUid();
    }

    private Integer getAutorId(UUID uuid) {
        Integer id = funcionarioRepository.findIds(uuid).get(uuid);

        if (id == null) {
            throw new EntityNotFoundException("Autor não encontrado para o UUID: " + uuid);
        }

        return id;
    }

    public Post convertToPost(PostDTO postDTO) {
        Integer idAutor = getAutorId(postDTO.idAutor());

        Post post = new Post(idAutor, postDTO.titulo(), postDTO.dataPublicacao(), postDTO.conteudo(), postDTO.imagemPost());
        post.setId(postDTO.id());
        return post;
    }

    public Post convertToPost(CreatePostDTO postDTO) {
        Integer idAutor = getAutorId(postDTO.idAutor());

        return new Post(idAutor, postDTO.titulo(), postDTO.dataPublicacao(), postDTO.conteudo(), postDTO.imagemPost());
    }

    public ComentarioDTO convertToComentarioDTO(Comentario comentario) {
        UUID uuidAutor = getAutorUUID(comentario.getIdAutor());
        return new ComentarioDTO(comentario.getId(), comentario.getIdPost() , uuidAutor, comentario.getConteudo());
    }

    public Comentario convertToComentario(ComentarioDTO comentarioDTO) {
        Integer idAutor  = getAutorId(comentarioDTO.idAutor());
        Comentario comentario = new Comentario(idAutor, comentarioDTO.idPost(), comentarioDTO.conteudo());
        comentario.setId(comentarioDTO.id());
        return comentario;
    }

    public Comentario convertToComentario(CreateComentarioDTO comentarioDTO) {
        Integer idAutor = getAutorId(comentarioDTO.idAutor());
        return new Comentario(idAutor, comentarioDTO.idPost(), comentarioDTO.conteudo());
    }

    public PostDTO createPost(Post post) {
        post = postsRepository.create(post);
        return convertToPostDTO(post);
    }

    public ComentarioDTO createComentario(CreateComentarioDTO comentarioDTO){
        Comentario comentario = CreateComentarioDTO.toComentario(comentarioDTO, funcionarioRepository);
        Comentario comentarioCriado = postsRepository.createComentario(comentario);
        return convertToComentarioDTO(comentarioCriado);
    }

    public PostDTO update(PostDTO postAtualizado){
        Post post = convertToPost(postAtualizado);
        Post postExistente = postsRepository.findById(post.getId());
        if (postExistente == null) {
            throw new EntityNotFoundException("Post não encontrado para o ID: " + postAtualizado.id());
        }

        postExistente.setIdAutor(post.getIdAutor());
        postExistente.setTitulo(post.getTitulo());
        postExistente.setImagemPost(post.getImagemPost());
        postExistente.setDataPublicacao(post.getDataPublicacao());
        postExistente. setConteudo(post.getConteudo());

        postsRepository.update(postExistente);
        return PostDTO.from(postExistente, funcionarioRepository);
    }

    public boolean deletePost(Integer id) {
        if (postsRepository.findById(id) == null) {
            throw new EntityNotFoundException("O post deve previamente existir no banco de dados");
        }
        return postsRepository.delete(id) > 0;
    }

    public boolean deletePost(List<Integer> posts_ids) {
        boolean allDeleted = true;
        for (Integer id : posts_ids) {
            if (postsRepository.findById(id) == null) {
                throw new EntityNotFoundException("O post com ID " + id + " não existe no banco de dados");
            }
            boolean deleted = postsRepository.delete(id) > 0;
            if (!deleted) {
                allDeleted = false;
            }
        }
        return allDeleted;
    }

    public int deleteComentario(Integer id) {
        Comentario comentario = postsRepository.findComentarioById(id);
        if (comentario == null) {
            throw new EntityNotFoundException("O comentário com ID " + id + " não existe");
        }
        postsRepository.deleteComentario(id);
        return id;
    }

    public List<PostDTO> getAllPosts() {
        return postsRepository.findAll().stream()
                .map(post -> PostDTO.from(post, funcionarioRepository))
                .collect(Collectors.toList());
    }

    public Post getPostById(Integer id) {
        return postsRepository.findById(id);
    }

    public Post getPostByPublishDate(LocalDateTime dataPublicacao) {
        return postsRepository.findByTempo(dataPublicacao);
    }

    public List<ComentarioDTO> getComentariosByPost(Integer idPost) {
        return postsRepository.findComentariosByPost(idPost).stream()
                .map(comentario -> ComentarioDTO.from(comentario, funcionarioRepository))
                .collect(Collectors.toList());
    }

    public ComentarioDTO getComentarioById(Integer id) {
        return convertToComentarioDTO(postsRepository.findComentarioById(id));
    }


}
