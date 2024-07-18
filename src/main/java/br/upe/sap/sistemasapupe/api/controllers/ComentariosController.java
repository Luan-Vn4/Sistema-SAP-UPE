package br.upe.sap.sistemasapupe.api.controllers;

import br.upe.sap.sistemasapupe.api.dtos.posts.comentarios.ComentarioDTO;
import br.upe.sap.sistemasapupe.api.dtos.posts.comentarios.CreateComentarioDTO;
import br.upe.sap.sistemasapupe.api.services.PostsService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.net.URI;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/comentarios")
public class ComentariosController {
    PostsService postsService;


    @DeleteMapping("/delete/{comentarioId}")
    public ResponseEntity<Void> deleteComentario(@PathVariable Integer comentarioId) {
        int deletedCount = postsService.deleteComentario(comentarioId);
        if (deletedCount > 0) {
            return ResponseEntity.noContent().build();
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{comentarioId}")
    public ResponseEntity<ComentarioDTO> getComentarioById(@PathVariable Integer comentarioId) {
        ComentarioDTO comentario = postsService.getComentarioById(comentarioId);
        return comentario != null ? ResponseEntity.ok(comentario) : ResponseEntity.notFound().build();
    }
    @PostMapping()
    public ResponseEntity<ComentarioDTO> createComentario(@RequestBody CreateComentarioDTO comentario) {
        ComentarioDTO createdComentario = postsService.createComentario(comentario);
        int postId = comentario.idPost();
        return ResponseEntity.created(URI.create("/posts/" + postId + "/comentarios/" + createdComentario.id())).body(createdComentario);
    }




}

