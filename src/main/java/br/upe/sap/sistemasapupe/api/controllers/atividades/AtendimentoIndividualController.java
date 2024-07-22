package br.upe.sap.sistemasapupe.api.controllers.atividades;

import br.upe.sap.sistemasapupe.api.dtos.atividades.AtendimentoIndividualDTO;
import br.upe.sap.sistemasapupe.api.dtos.atividades.CreateAtendimentoIndividualDTO;
import br.upe.sap.sistemasapupe.api.services.atividades.AtendimentoIndividualService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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
        return ResponseEntity.ok(found);
    }
}
