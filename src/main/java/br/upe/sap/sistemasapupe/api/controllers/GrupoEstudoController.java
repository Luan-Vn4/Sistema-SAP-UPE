package br.upe.sap.sistemasapupe.api.controllers;

import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.api.dtos.grupo.CreateGrupoEstudoDTO;
import br.upe.sap.sistemasapupe.api.dtos.grupo.GrupoEstudoDTO;
import br.upe.sap.sistemasapupe.api.services.GrupoEstudoService;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoEstudo;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.List;
import java.util.UUID;

@RestController
@AllArgsConstructor
@RequestMapping("/api/v1/grupoEstudo")
public class GrupoEstudoController {
    GrupoEstudoService grupoEstudoService;

    @PostMapping("/")
    public ResponseEntity<GrupoEstudoDTO> create(@RequestBody CreateGrupoEstudoDTO grupoEstudo) {
        GrupoEstudoDTO grupoEstudoCriado = grupoEstudoService.create(grupoEstudo);
        return ResponseEntity.created(URI.create("/posts/" + grupoEstudoCriado.id())).body(grupoEstudoCriado);
    }

    @PostMapping("/addFuncionario")
    public ResponseEntity<FuncionarioDTO> addFuncionario(@RequestBody UUID uid, @RequestBody UUID uidGrupo){
        FuncionarioDTO resposta = grupoEstudoService.addFuncionario(uid, uidGrupo);
        return ResponseEntity.ok(resposta);
    }

    @GetMapping("/{uid}")
    public ResponseEntity<GrupoEstudoDTO> get(@PathVariable UUID uid) {
        GrupoEstudoDTO grupoEstudo = grupoEstudoService.getById(uid);
        return grupoEstudo != null ? ResponseEntity.ok(grupoEstudo) : ResponseEntity.notFound().build();
    }

    @GetMapping("/all")
    public ResponseEntity<List<GrupoEstudoDTO>> getAll() {
        List<GrupoEstudoDTO> grupoEstudo = grupoEstudoService.getAll();
        return grupoEstudo != null ? ResponseEntity.ok(grupoEstudo) : ResponseEntity.notFound().build();
    }

    @GetMapping("/funcionario/{uid}")
    public ResponseEntity<List<GrupoEstudoDTO>> getByFuncionario(@PathVariable UUID uid) {
        List<GrupoEstudoDTO> grupoEstudo = grupoEstudoService.getByFuncionarioId(uid);
        return grupoEstudo != null ? ResponseEntity.ok(grupoEstudo) : ResponseEntity.notFound().build();
    }

    @PutMapping("/update")
    public ResponseEntity<GrupoEstudoDTO> update(@RequestBody GrupoEstudoDTO grupoEstudo) {
        GrupoEstudoDTO grupoEstudoDTO  = grupoEstudoService.update(grupoEstudo);
        return ResponseEntity.ok(grupoEstudoDTO);
    }

    @DeleteMapping("delete/{uid}")
    public ResponseEntity<GrupoEstudoDTO> delete(@PathVariable UUID uid) {
        Boolean deleted = grupoEstudoService.deleteById(uid);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping("deleteParticipacao/{uid}")
    public ResponseEntity<GrupoEstudoDTO> deleteParticipacao(@PathVariable UUID uid) {
        Boolean deleted = grupoEstudoService.deletedParticipacao(uid);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/many")
    public ResponseEntity<GrupoEstudoDTO> deleteMany(@RequestBody List<UUID> ids) {
        Boolean deleted = grupoEstudoService.deleteManyByIds(ids);
        return deleted ? ResponseEntity.ok().build() : ResponseEntity.notFound().build();
    }
}