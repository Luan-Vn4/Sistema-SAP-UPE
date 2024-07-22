package br.upe.sap.sistemasapupe.api.controllers;

import br.upe.sap.sistemasapupe.api.dtos.grupo.GrupoTerapeuticoDTO;
import br.upe.sap.sistemasapupe.api.services.GrupoTerapeuticoService;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/grupo-terapeutico")
@AllArgsConstructor
public class GrupoTerapeuticoController {

    GrupoTerapeuticoService service;

    // MÉTODOS GET
    @GetMapping("/many/all")
    public ResponseEntity<List<GrupoTerapeuticoDTO>> searchAll(){
        return ResponseEntity.ok().body(service.getAll());
    }

    @GetMapping("/one/{uid}")
    public ResponseEntity<GrupoTerapeuticoDTO> searchByUid(@PathVariable UUID uid){
        GrupoTerapeuticoDTO dto = service.getById(uid);
        return dto != null ? ResponseEntity.ok(dto) : ResponseEntity.notFound().build();
    }

    @GetMapping("/many/{uidFuncionario}")
    public ResponseEntity<List<GrupoTerapeuticoDTO>> searchByFuncionario(@PathVariable UUID uidFuncionario){
        List<GrupoTerapeuticoDTO> grupos = service.getByFuncionario(uidFuncionario);
        return grupos != null ? ResponseEntity.ok(grupos) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{uidFicha}")
    public ResponseEntity<GrupoTerapeuticoDTO> searcByFicha(@PathVariable UUID uidFicha){
        GrupoTerapeuticoDTO grupo = service.getByFicha(uidFicha);
        return grupo != null ? ResponseEntity.ok(grupo) : ResponseEntity.notFound().build();
    }

    // MÉTODO UPDATE

    @PutMapping("/one")
    public ResponseEntity<GrupoTerapeuticoDTO> updateGrupoTerapeutico(GrupoTerapeuticoDTO dto){
        GrupoTerapeuticoDTO grupoAtualizado = service.update(dto);
        return grupoAtualizado != null ? ResponseEntity.ok(grupoAtualizado) : ResponseEntity.notFound().build();
    }

    @PostMapping(value = "/one/coordenadores", params = {"uid-funcionario", "uid-grupo"})
    public ResponseEntity<GrupoTerapeuticoDTO> addFuncionario(
                                                            @RequestParam(name = "uid-funcionario") UUID uidFuncioario,
                                                            @RequestParam(name = "uid-grupo") UUID uidGrupo){
        GrupoTerapeuticoDTO grupoComFuncionario = service.addFuncionario(uidFuncioario, uidGrupo);
        return grupoComFuncionario != null ? ResponseEntity.ok(grupoComFuncionario) : ResponseEntity.notFound().build();
    }

    @PostMapping(value = "/many/coordenadores", params = {"uid-grupo"})
    public ResponseEntity<GrupoTerapeuticoDTO> addFuncionario(
                                                              @RequestBody List<UUID> uidsCoordenadores,
                                                              @RequestParam(name = "uid-grupo") UUID uidGrupo){
        GrupoTerapeuticoDTO grupoComFuncionarios = service.addFuncionario(uidsCoordenadores, uidGrupo);
        return grupoComFuncionarios != null ? ResponseEntity.ok(grupoComFuncionarios) : ResponseEntity.notFound().build();
    }

    @PostMapping(value = "one/ficha", params = {"uid-ficha", "uid-grupo"})
    public ResponseEntity<GrupoTerapeuticoDTO> addFicha(
                                                        @RequestParam(name = "uid-ficha") UUID uidFicha,
                                                        @RequestParam(name = "uid-grupo") UUID uidGrupo){
        GrupoTerapeuticoDTO grupoComFuncionarios = service.addFicha(uidFicha, uidGrupo);
        return grupoComFuncionarios != null ? ResponseEntity.ok(grupoComFuncionarios) : ResponseEntity.notFound().build();
    }

    @PostMapping(value = "/many/fichas", params = {"uid-grupo"})
    public ResponseEntity<GrupoTerapeuticoDTO> addFicha(
                                                        @RequestBody List<UUID> uidsFichas,
                                                        @RequestParam(name = "uid-grupo") UUID uidGrupo){
        GrupoTerapeuticoDTO grupoComFuncionarios = service.addFicha(uidsFichas, uidGrupo);
        return grupoComFuncionarios != null ? ResponseEntity.ok(grupoComFuncionarios) : ResponseEntity.notFound().build();
    }

    // MÉTODOS DELETE
    @DeleteMapping(value = "onde/grupo", params = {"uid-grupo"})
    public ResponseEntity<Void> deleteGrupo(@RequestParam(name = "uid-grupo") UUID uidGrupo){
        service.deleteGrupoTerapeutico(uidGrupo);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/one/coordenador", params = {"uid-funcionario", "uid-grupo"})
    public ResponseEntity<GrupoTerapeuticoDTO> removeFuncionario(
                                                        @RequestParam(name = "uid-funcionario") UUID uidFuncionario,
                                                        @RequestParam(name = "uid-grupo") UUID uidGrupo){
        GrupoTerapeuticoDTO grupoSemFuncionario = service.removeFuncionario(uidFuncionario, uidGrupo);
        return grupoSemFuncionario != null ? ResponseEntity.ok(grupoSemFuncionario) : ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "/one/ficha", params = {"uid-ficha", "uid-grupo"})
    public ResponseEntity<GrupoTerapeuticoDTO> removeFicha(
                                                        @RequestParam(name = "uid-ficha") UUID uidFicha,
                                                        @RequestParam(name = "uid-grupo") UUID uidGrupo){
        GrupoTerapeuticoDTO grupoSemFicha = service.removerFicha(uidFicha, uidGrupo);
        return grupoSemFicha != null ? ResponseEntity.ok(grupoSemFicha) : ResponseEntity.notFound().build();
    }
}
