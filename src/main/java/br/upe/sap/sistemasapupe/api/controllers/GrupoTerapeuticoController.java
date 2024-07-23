package br.upe.sap.sistemasapupe.api.controllers;

import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.api.dtos.grupo.CreateGrupoTerapeuticoDTO;
import br.upe.sap.sistemasapupe.api.dtos.grupo.GrupoTerapeuticoDTO;
import br.upe.sap.sistemasapupe.api.dtos.paciente.FichaDTO;
import br.upe.sap.sistemasapupe.api.services.GrupoTerapeuticoService;
import br.upe.sap.sistemasapupe.exceptions.utils.HttpErrorUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/grupo-terapeutico")
@AllArgsConstructor
public class GrupoTerapeuticoController {

    // DEPENDÊNCIAS //
    GrupoTerapeuticoService service;


    // CREATE
    @PostMapping("/")
    public ResponseEntity<GrupoTerapeuticoDTO> create(@RequestBody CreateGrupoTerapeuticoDTO dto){
        GrupoTerapeuticoDTO grupo = service.create(dto);
        return ResponseEntity.ok().body(grupo);
    }

    @PostMapping(value = "/one/coordenadores", params = {"uid-funcionario", "uid-grupo"})
    public ResponseEntity<FuncionarioDTO> addFuncionario(@RequestParam(name = "uid-funcionario") UUID uidFuncioario,
                                                         @RequestParam(name = "uid-grupo") UUID uidGrupo) {
        FuncionarioDTO result;
        try {
            result = service.addFuncionario(uidFuncioario, uidGrupo);
        } catch (EntityNotFoundException e) {
            throw HttpErrorUtils.notFoundException(e.getMessage(), null, null);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "/many/coordenadores", params = {"uid-grupo"})
    public ResponseEntity<List<FuncionarioDTO>> addFuncionario(@RequestBody List<UUID> uidsCoordenadores,
                                                               @RequestParam(name = "uid-grupo") UUID uidGrupo) {
        List<FuncionarioDTO> result;
        try {
            result = service.addFuncionario(uidsCoordenadores, uidGrupo);
        } catch (EntityNotFoundException e) {
            throw HttpErrorUtils.notFoundException(e.getMessage(), uidsCoordenadores, null);
        }
        return ResponseEntity.ok(result);
    }

    @PostMapping(value = "one/ficha", params = {"uid-ficha", "uid-grupo"})
    public ResponseEntity<FichaDTO> addFicha(@RequestParam(name = "uid-ficha") UUID uidFicha,
                                             @RequestParam(name = "uid-grupo") UUID uidGrupo) {
        FichaDTO fichaDTO;
        try {
            fichaDTO = service.addFicha(uidFicha, uidGrupo);
        } catch (EntityNotFoundException e) {
            throw HttpErrorUtils.notFoundException(e.getMessage(),null, null);
        }

        return ResponseEntity.ok(fichaDTO);
    }

    @PostMapping(value = "/many/fichas", params = {"uid-grupo"})
    public ResponseEntity<List<FichaDTO>> addFicha(@RequestBody List<UUID> uidsFichas,
                                                   @RequestParam(name = "uid-grupo") UUID uidGrupo) {
        List<FichaDTO> fichaDTOS;
        try {
            fichaDTOS = service.addFicha(uidsFichas, uidGrupo);
        } catch (EntityNotFoundException e) {
            throw HttpErrorUtils.notFoundException(e.getMessage(), uidsFichas, null);
        }
        return ResponseEntity.ok(fichaDTOS);
    }


    // READ //
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
    public ResponseEntity<GrupoTerapeuticoDTO> searcByFicha(@PathVariable UUID uidFicha) {
        GrupoTerapeuticoDTO grupo = service.getByFicha(uidFicha);
        return grupo != null ? ResponseEntity.ok(grupo) : ResponseEntity.notFound().build();
    }

    @GetMapping("/many/funcionarios/{uidGrupoTerapeutico}")
    public ResponseEntity<GrupoTerapeuticoDTO> searchFuncionaripos(@PathVariable UUID uidGrupoTerapeutico) {
        return null;
    }

    // MÉTODO UPDATE
    @PutMapping("/one")
    public ResponseEntity<GrupoTerapeuticoDTO> updateGrupoTerapeutico(GrupoTerapeuticoDTO dto){
        GrupoTerapeuticoDTO grupoAtualizado = service.update(dto);
        return grupoAtualizado != null ? ResponseEntity.ok(grupoAtualizado) : ResponseEntity.notFound().build();
    }


    // DELETE //
    @DeleteMapping(value = "onde/grupo", params = {"uid-grupo"})
    public ResponseEntity<Void> deleteGrupo(@RequestParam(name = "uid-grupo") UUID uidGrupo) {
        service.deleteGrupoTerapeutico(uidGrupo);
        return ResponseEntity.ok().build();
    }

    @DeleteMapping(value = "/one/coordenador", params = {"uid-funcionario", "uid-grupo"})
    public ResponseEntity<GrupoTerapeuticoDTO> removeFuncionario(
                                                    @RequestParam(name = "uid-funcionario") UUID uidFuncionario,
                                                    @RequestParam(name = "uid-grupo") UUID uidGrupo) {
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

    @GetMapping(value = "/grupo-nao-participados/many", params = {"uid-funcionario"})
    public ResponseEntity<List<GrupoTerapeuticoDTO>> getGruposNaoParticipados(
                                                @RequestParam(name = "uid-funcionario") UUID uidFuncionario){
        List<GrupoTerapeuticoDTO> retorno = service.getGruposNaoParticipados(uidFuncionario);
        return retorno != null ? ResponseEntity.ok(retorno) : ResponseEntity.notFound().build();
    }

}
