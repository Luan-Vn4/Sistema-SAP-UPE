package br.upe.sap.sistemasapupe.api.controllers.atividades;

import br.upe.sap.sistemasapupe.api.dtos.atividades.geral.AnyAtividadeDTO;
import br.upe.sap.sistemasapupe.api.dtos.atividades.geral.AtividadeDTO;
import br.upe.sap.sistemasapupe.api.services.atividades.AtividadeService;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import br.upe.sap.sistemasapupe.exceptions.utils.HttpErrorUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import java.time.LocalDate;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/atividades/")
public class AtividadeController {

    private AtividadeService atividadeService;

    @GetMapping(value = "/one/uid/{uid}")
    public ResponseEntity<AtividadeDTO> getById(@PathVariable("uid") UUID uid) {
        return ResponseEntity.ok(atividadeService.getByUid(uid));
    }

    @GetMapping(value = "/many/all")
    public ResponseEntity<AnyAtividadeDTO> getAll() {
        return ResponseEntity.ok(atividadeService.getAll());
    }

    @PostMapping(value = "/many/uids")
    public ResponseEntity<AnyAtividadeDTO> getByIds(@RequestBody List<UUID> uids) {
        return ResponseEntity.ok(atividadeService.getByUids(uids));
    }

    @GetMapping(value = "/many/status/{status}")
    public ResponseEntity<AnyAtividadeDTO> getByStatus(@PathVariable("status") StatusAtividade status) {
        return ResponseEntity.ok(atividadeService.getByStatus(status));
    }

    @GetMapping(value = "/many/id-funcionario/{idFuncionario}")
    public ResponseEntity<AnyAtividadeDTO> getByFuncionario(@PathVariable("idFuncionario") UUID uid){
        return ResponseEntity.ok(atividadeService.getByFuncionario(uid));
    }

    @GetMapping(value = "/many/id-sala/{idSala}")
    public ResponseEntity<AnyAtividadeDTO> getBySala(@PathVariable("idSala") UUID uid) {
        return ResponseEntity.ok(atividadeService.getBySala(uid));
    }

    @GetMapping(value = "/many/sala-date/", params = {"uid-sala", "date"})
    public ResponseEntity<AnyAtividadeDTO> getBySalaAndDate(@RequestParam(name = "uid-sala") UUID uidSala,
                                                            @RequestParam(name = "date") LocalDate date) {
        return ResponseEntity.ok(atividadeService.getBySalaAndDate(uidSala, date));
    }

    @PutMapping(value = "/one/status/", params = {"status", "uid"})
    public ResponseEntity<AtividadeDTO> updateStatus(@RequestParam("status") StatusAtividade status,
                                                     @RequestParam("uid") UUID uid) {
        return ResponseEntity.ok(atividadeService.updateStatus(status, uid));
    }

    @DeleteMapping(value = "/many/uids")
    public ResponseEntity<?> delete(@RequestBody List<UUID> uids) {
        try {
            atividadeService.delete(uids);
        } catch (EntityNotFoundException e) {
            throw HttpErrorUtils.notFoundException(e.getMessage(), uids, null);
        }

        return ResponseEntity.ok().build();
    }

}
