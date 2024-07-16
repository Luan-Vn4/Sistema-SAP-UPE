package br.upe.sap.sistemasapupe.api.services;

import br.upe.sap.sistemasapupe.data.model.posts.Comentario;
import br.upe.sap.sistemasapupe.data.model.posts.Post;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.PostsRepository;
import br.upe.sap.sistemasapupe.data.repositories.jdbi.JdbiPostsRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.time.LocalDateTime;
import java.util.List;

@Service
@AllArgsConstructor
public class PostsService {
    PostsRepository postsRepository;

    public Post createPost(Post post) {
        return postsRepository.create(post);
    }

    public Comentario createComentario(Comentario comentario){
        return  postsRepository.createComentario(comentario);
    }

    public Post update(Post postAtualizado){
        Post postExistente = postsRepository.findById(postAtualizado.getId());
        if (postExistente == null) {
            throw new EntityNotFoundException("Post não encontrado para o ID: " + postAtualizado.getId());
        }

        postExistente.setId_autor(postAtualizado.getId_autor());
        postExistente.setTitulo(postAtualizado.getTitulo());
        postExistente.setImagem_post(postAtualizado.getImagem_post());
        postExistente.setData_publicacao(postAtualizado.getData_publicacao());
        postExistente. setConteudo(postAtualizado.getConteudo());

        return postsRepository.update(postExistente);
    }

    public List<Post> createPosts(List<Post> posts) {
        return postsRepository.create(posts);
    }

    public boolean deletePost(Integer id) {
        if (postsRepository.findById(id) == null) {
            throw new EntityNotFoundException("O post deve previamente existir no banco de dados");
        }
        return postsRepository.delete(id) > 0;
    }

    public int deleteComentario(Integer id) {
        Comentario comentario = postsRepository.findComentarioById(id);
        if (comentario == null) {
            throw new EntityNotFoundException("O comentário com ID " + id + " não existe");
        }
        postsRepository.deleteComentario(id);
        return id;
    }

    public List<Post> getAllPosts() {
        return postsRepository.findAll();
    }

    public Post getPostById(Integer id) {
        return postsRepository.findById(id);
    }

    public Post getPostByPublishDate(LocalDateTime dataPublicacao) {
        return postsRepository.findByTempo(dataPublicacao);
    }

    public List<Comentario> getComentariosByPost(Integer idPost) {
        return postsRepository.findComentariosByPost(idPost);
    }

    public Comentario getComentarioById(Integer id) {
        return postsRepository.findComentarioById(id);
    }

}
