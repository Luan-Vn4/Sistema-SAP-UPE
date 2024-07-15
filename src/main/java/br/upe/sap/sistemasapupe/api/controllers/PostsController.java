package br.upe.sap.sistemasapupe.api.controllers;

import br.upe.sap.sistemasapupe.data.model.posts.Comentario;
import br.upe.sap.sistemasapupe.data.model.posts.Post;
import br.upe.sap.sistemasapupe.api.services.PostsService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.time.LocalDateTime;
import java.util.List;

@RestController
@RequestMapping("/api/v1/posts")
public class PostsController {

    private final PostsService postsService;

    @Autowired
    public PostsController(PostsService postsService) {
        this.postsService = postsService;
    }

    @PostMapping
    public ResponseEntity<Post> createPost(@RequestBody Post post) {
        Post createdPost = postsService.createPost(post);
        return ResponseEntity.created(URI.create("/posts/" + createdPost.getId())).body(createdPost);
    }

    @PutMapping("/{postId}")
    public ResponseEntity<Post> updatePost(@PathVariable Integer postId, @RequestBody Post post) {
        if (!postId.equals(post.getId())) {
            return ResponseEntity.badRequest().build();
        }
        Post updatedPost = postsService.update(post);
        return ResponseEntity.ok(updatedPost);
    }

    @DeleteMapping("/{postId}")
    public ResponseEntity<Void> deletePost(@PathVariable Integer postId) {
        boolean deleted = postsService.deletePost(postId);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<Post>> getAllPosts() {
        List<Post> posts = postsService.getAllPosts();
        return ResponseEntity.ok(posts);
    }

    @GetMapping("/{postId}")
    public ResponseEntity<Post> getPostById(@PathVariable Integer postId) {
        Post post = postsService.getPostById(postId);
        return post != null ? ResponseEntity.ok(post) : ResponseEntity.notFound().build();
    }

    @GetMapping("/publishDate")
    public ResponseEntity<Post> getPostByPublishDate(@RequestParam("dataPublicacao") LocalDateTime dataPublicacao) {
        Post post = postsService.getPostByPublishDate(dataPublicacao);
        return post != null ? ResponseEntity.ok(post) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{postId}/comentarios")
    public ResponseEntity<List<Comentario>> getComentariosByPost(@PathVariable Integer postId) {
        List<Comentario> comentarios = postsService.getComentariosByPost(postId);
        return ResponseEntity.ok(comentarios);
    }

    @GetMapping("/comentario/{comentarioId}")
    public ResponseEntity<Comentario> getComentarioById(@PathVariable Integer comentarioId) {
        Comentario comentario = postsService.getComentarioById(comentarioId);
        return comentario != null ? ResponseEntity.ok(comentario) : ResponseEntity.notFound().build();
    }
    @PostMapping("/{postId}/comentarios")
    public ResponseEntity<Comentario> createComentario(@PathVariable Integer postId, @RequestBody Comentario comentario) {
        comentario.setId_post(postId);
        Comentario createdComentario = postsService.createComentario(comentario);
        return ResponseEntity.created(URI.create("/posts/" + postId + "/comentarios/" + createdComentario.getId())).body(createdComentario);
    }


}

