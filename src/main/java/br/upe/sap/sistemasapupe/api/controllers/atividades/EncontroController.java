package br.upe.sap.sistemasapupe.api.controllers.atividades;

import br.upe.sap.sistemasapupe.api.dtos.atividades.atendimentogrupo.AtendimentoGrupoDTO;
import br.upe.sap.sistemasapupe.api.dtos.atividades.atendimentogrupo.CreateAtendimentoGrupoDTO;
import br.upe.sap.sistemasapupe.api.dtos.atividades.encontro.CreateEncontroDTO;
import br.upe.sap.sistemasapupe.api.dtos.atividades.encontro.EncontroDTO;
import br.upe.sap.sistemasapupe.api.services.atividades.AtendimentoGrupoService;
import br.upe.sap.sistemasapupe.api.services.atividades.EncontroService;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/encontro")
@AllArgsConstructor
public class EncontroController {

    private EncontroService encontroService;

    @PostMapping("/create")
    public ResponseEntity<EncontroDTO> create(@RequestBody CreateEncontroDTO createDTO) {
        EncontroDTO created = encontroService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/update")
    public ResponseEntity<EncontroDTO> update(@RequestBody EncontroDTO updateDTO) {
        EncontroDTO updated = encontroService.update(updateDTO);
        return ResponseEntity.ok(updated);
    }

    @GetMapping("/{id}")
    public ResponseEntity<EncontroDTO> getById(@PathVariable UUID id) {
        EncontroDTO found = encontroService.getByUid(id);
        if (found == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(found);
    }

    @GetMapping("/status/{status}")
    public ResponseEntity<List<EncontroDTO>> getByStatus(@PathVariable StatusAtividade status) {
        List<EncontroDTO> atendimentos = encontroService.getByStatus(status);
        return ResponseEntity.ok(atendimentos);
    }

    @DeleteMapping("/delete/{uid}")
    public ResponseEntity<String> deleteByUid(@PathVariable UUID uid) {
        if (uid == null) {
            throw new EntityNotFoundException("UID nulo");
        }

        encontroService.deleteByUid(uid);
        return ResponseEntity.ok("Encomcontro  UID " + uid + " deletado com sucesso");
    }


}
