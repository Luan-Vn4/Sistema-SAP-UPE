package br.upe.sap.sistemasapupe.api.controllers;

import br.upe.sap.sistemasapupe.api.dtos.ComentarioDTO;
import br.upe.sap.sistemasapupe.api.dtos.CreateComentarioDTO;
import br.upe.sap.sistemasapupe.api.dtos.CreatePostDTO;
import br.upe.sap.sistemasapupe.api.dtos.PostDTO;
import br.upe.sap.sistemasapupe.api.services.PostsService;
import br.upe.sap.sistemasapupe.data.model.posts.Comentario;
import br.upe.sap.sistemasapupe.data.model.posts.Post;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/posts")
public class PostsController {
    PostsService postsService;

    @PostMapping
    public ResponseEntity<PostDTO> createPost(@RequestBody CreatePostDTO postDTO) {
        Post post = postsService.convertToPost(postDTO);
        PostDTO createdPost = postsService.createPost(post);
        return ResponseEntity.created(URI.create("/posts/" + createdPost.idPost())).body(createdPost);
    }

    @PutMapping("/update")
    public ResponseEntity<PostDTO> updatePost(@RequestBody PostDTO postDTO) {
        postsService.update(postDTO);
        return ResponseEntity.ok(postDTO);
    }

    @DeleteMapping("/delete/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Integer postId) {
        boolean deleted = postsService.deletePost(postId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/many/{postIds}")
    public ResponseEntity<Void> deletePost(@PathVariable("postIds") List<Integer> postId) {
        boolean deleted = postsService.deletePost(postId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/comentarios/delete/{comentarioId}")
    public ResponseEntity<Void> deleteComentario(@PathVariable Integer comentarioId) {
        int deletedCount = postsService.deleteComentario(comentarioId);
        if (deletedCount > 0) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/all")
    public ResponseEntity<List<PostDTO>> getAllPosts() {
        List<PostDTO> posts = postsService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<PostDTO> getPostById(@PathVariable Integer postId) {
        Post post = postsService.getPostById(postId);
        PostDTO postDto = postsService.convertToPostDTO(post);
        return postDto != null ? ResponseEntity.ok(postDto) : ResponseEntity.notFound().build();
    }

    @GetMapping("/publishDate")
    public ResponseEntity<PostDTO> getPostByPublishDate(@RequestParam("dataPublicacao") LocalDateTime dataPublicacao) {
        Post post = postsService.getPostByPublishDate(dataPublicacao);
        PostDTO postDto = postsService.convertToPostDTO(post);
        return postDto != null ? ResponseEntity.ok(postDto) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{postId}/comentarios")
    public ResponseEntity<List<ComentarioDTO>> getComentariosByPost(@PathVariable Integer postId) {
        List<ComentarioDTO> comentarios = postsService.getComentariosByPost(postId);
        return ResponseEntity.ok(comentarios);
    }

    @GetMapping("/comentarios/{comentarioId}")
    public ResponseEntity<ComentarioDTO> getComentarioById(@PathVariable Integer comentarioId) {
        ComentarioDTO comentario = postsService.getComentarioById(comentarioId);
        return comentario != null ? ResponseEntity.ok(comentario) : ResponseEntity.notFound().build();
    }
    @PostMapping("/comentarios")
    public ResponseEntity<ComentarioDTO> createComentario(@RequestBody CreateComentarioDTO comentario) {
        ComentarioDTO createdComentario = postsService.createComentario(comentario);
        int postId = comentario.idPost();
        return ResponseEntity.created(URI.create("/posts/" + postId + "/comentarios/" + createdComentario.idComentario())).body(createdComentario);
    }




}
