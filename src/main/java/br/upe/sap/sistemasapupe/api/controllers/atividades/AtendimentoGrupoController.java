package br.upe.sap.sistemasapupe.api.controllers.atividades;

import br.upe.sap.sistemasapupe.api.dtos.atividades.atendimentogrupo.AtendimentoGrupoDTO;
import br.upe.sap.sistemasapupe.api.dtos.atividades.atendimentogrupo.CreateAtendimentoGrupoDTO;
import br.upe.sap.sistemasapupe.api.dtos.atividades.atendimentoindividual.AtendimentoIndividualDTO;
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
@RequestMapping("/api/v1/atendimentosGrupo")
@AllArgsConstructor
public class AtendimentoGrupoController {
    private AtendimentoGrupoService atendimentoGrupoService;

    @PostMapping("/create")
    public ResponseEntity<AtendimentoGrupoDTO> create(@RequestBody CreateAtendimentoGrupoDTO createDTO) {
        AtendimentoGrupoDTO created = atendimentoGrupoService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/update")
    public ResponseEntity<AtendimentoGrupoDTO> update(@RequestBody AtendimentoGrupoDTO updateDTO) {
        AtendimentoGrupoDTO updated = atendimentoGrupoService.update(updateDTO);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtendimentoGrupoDTO> getById(@PathVariable UUID id) {
        AtendimentoGrupoDTO found = atendimentoGrupoService.getById(id);
        if (found == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(found);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AtendimentoGrupoDTO>> getByStatus(@PathVariable StatusAtividade status) {
        List<AtendimentoGrupoDTO> atendimentos = atendimentoGrupoService.getByStatus(status);
        return ResponseEntity.ok(atendimentos);
    }

    @DeleteMapping("/delete/{uid}")
    public ResponseEntity<String> deleteByUid(@PathVariable UUID uid) {
        if (uid == null) {
            throw new EntityNotFoundException("UID nulo");
        }

        atendimentoGrupoService.deleteById(uid);
        return ResponseEntity.ok("Atendimento Grupo com UID " + uid + " deletado com sucesso");
    }

}
