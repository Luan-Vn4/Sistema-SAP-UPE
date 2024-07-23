package br.upe.sap.sistemasapupe.api.controllers.atividades;

import br.upe.sap.sistemasapupe.api.dtos.atividades.atendimentogrupo.AtendimentoGrupoDTO;
import br.upe.sap.sistemasapupe.api.dtos.atividades.atendimentogrupo.CreateAtendimentoGrupoDTO;
import br.upe.sap.sistemasapupe.api.services.atividades.AtendimentoGrupoService;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/atividades/atendimentos-grupo")
@AllArgsConstructor
public class AtendimentoGrupoController {
    private AtendimentoGrupoService atendimentoGrupoService;

    @PostMapping("/one")
    public ResponseEntity<AtendimentoGrupoDTO> create(@RequestBody CreateAtendimentoGrupoDTO createDTO) {
        AtendimentoGrupoDTO created = atendimentoGrupoService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/one")
    public ResponseEntity<AtendimentoGrupoDTO> update(@RequestBody AtendimentoGrupoDTO updateDTO) {
        AtendimentoGrupoDTO updated = atendimentoGrupoService.update(updateDTO);
        return ResponseEntity.ok(updated);
    }

    @GetMapping(value = "/one", params = {"uid"})
    public ResponseEntity<AtendimentoGrupoDTO> getById(@RequestParam(name = "id") UUID id) {
        AtendimentoGrupoDTO found = atendimentoGrupoService.getById(id);
        if (found == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(found);
    }

    @GetMapping(value = "/one", params = {"status"})
    public ResponseEntity<List<AtendimentoGrupoDTO>> getByStatus(
                                                @RequestParam(name = "status") StatusAtividade status) {
        List<AtendimentoGrupoDTO> atendimentos = atendimentoGrupoService.getByStatus(status);
        return ResponseEntity.ok(atendimentos);
    }

    @GetMapping("/many/id-grupo/{idGrupo}")
    public ResponseEntity<List<AtendimentoGrupoDTO>> getByIdGrupo(@PathVariable UUID idGrupo){
        List<AtendimentoGrupoDTO> atendimentoGrupo = atendimentoGrupoService.getByGrupoTerapeutico(idGrupo);
        return ResponseEntity.ok(atendimentoGrupo);
    }

    @DeleteMapping(value = "/one", params = {"uid"})
    public ResponseEntity<String> deleteByUid(@RequestParam(name = "uid") UUID uid) {
        if (uid == null) {
            throw new EntityNotFoundException("UID nulo");
        }

        atendimentoGrupoService.deleteById(uid);
        return ResponseEntity.ok("Atendimento Grupo com UID " + uid + " deletado com sucesso");
    }

}
