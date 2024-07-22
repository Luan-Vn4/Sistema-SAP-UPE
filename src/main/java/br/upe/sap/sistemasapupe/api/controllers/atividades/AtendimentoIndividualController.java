package br.upe.sap.sistemasapupe.api.controllers.atividades;

import br.upe.sap.sistemasapupe.api.dtos.atividades.atendimentoindividual.AtendimentoIndividualDTO;
import br.upe.sap.sistemasapupe.api.dtos.atividades.atendimentoindividual.CreateAtendimentoIndividualDTO;
import br.upe.sap.sistemasapupe.api.services.atividades.AtendimentoIndividualService;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/atendimentosIndividuais")
@AllArgsConstructor
public class AtendimentoIndividualController {

    private final AtendimentoIndividualService atendimentoIndividualService;

    @PostMapping("/create")
    public ResponseEntity<AtendimentoIndividualDTO> create(@RequestBody CreateAtendimentoIndividualDTO createDTO) {
        AtendimentoIndividualDTO created = atendimentoIndividualService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/update")
    public ResponseEntity<AtendimentoIndividualDTO> update(@RequestBody AtendimentoIndividualDTO updateDTO) {
        AtendimentoIndividualDTO updated = atendimentoIndividualService.update(updateDTO);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<AtendimentoIndividualDTO> getById(@PathVariable UUID id) {
        AtendimentoIndividualDTO found = atendimentoIndividualService.getById(id);
        if (found == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(found);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<AtendimentoIndividualDTO>> getByStatus(@PathVariable StatusAtividade status) {
        List<AtendimentoIndividualDTO> atendimentos = atendimentoIndividualService.getByStatus(status);
        return ResponseEntity.ok(atendimentos);
    }

    @DeleteMapping("/delete/{uid}")
    public ResponseEntity<String> deleteByUid(@PathVariable UUID uid) {
        if (uid == null) {
            throw new EntityNotFoundException("UID nulo");
        }

        atendimentoIndividualService.deleteByUid(uid);
        return ResponseEntity.ok("Atendimento Individual com UID " + uid + " deletado com sucesso");
    }
}
