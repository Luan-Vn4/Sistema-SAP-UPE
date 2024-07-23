package br.upe.sap.sistemasapupe.api.controllers.atividades;

import br.upe.sap.sistemasapupe.api.dtos.atividades.encontro.CreateEncontroDTO;
import br.upe.sap.sistemasapupe.api.dtos.atividades.encontro.EncontroDTO;
import br.upe.sap.sistemasapupe.api.services.atividades.EncontroService;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;
@RestController
@RequestMapping("/api/v1/atividades/encontros")
@AllArgsConstructor
public class EncontroController {

    private EncontroService encontroService;

    @PostMapping("/one")
    public ResponseEntity<EncontroDTO> create(@RequestBody CreateEncontroDTO createDTO) {
        EncontroDTO created = encontroService.create(createDTO);
        return ResponseEntity.status(HttpStatus.CREATED).body(created);
    }

    @PutMapping("/one")
    public ResponseEntity<EncontroDTO> update(@RequestBody EncontroDTO updateDTO) {
        EncontroDTO updated = encontroService.update(updateDTO);
        return ResponseEntity.ok(updated);
    }

    @GetMapping(value = "/one", params = {"uid"})
    public ResponseEntity<EncontroDTO> getById(@RequestParam(name = "uid") UUID uid) {
        EncontroDTO found = encontroService.getByUid(uid);
        if (found == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok(found);
    }

    @GetMapping(value = "/one", params = {"status"})
    public ResponseEntity<List<EncontroDTO>> getByStatus(
                                                @RequestParam(name = "status") StatusAtividade status) {
        List<EncontroDTO> atendimentos = encontroService.getByStatus(status);
        return ResponseEntity.ok(atendimentos);
    }

    @GetMapping("/many/id-grupo/{idGrupo}")
    public ResponseEntity<EncontroDTO> getByGrupo(@PathVariable UUID idGrupo) {
        List<EncontroDTO> encontros = encontroService.getByGrupoEstudo(idGrupo);
        return ResponseEntity.ok(encontros.get(0));
    }

}
