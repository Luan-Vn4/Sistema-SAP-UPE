package br.upe.sap.sistemasapupe.api.controllers.atividades;

import br.upe.sap.sistemasapupe.api.dtos.atividades.geral.AnyAtividadeDTO;
import br.upe.sap.sistemasapupe.api.dtos.atividades.geral.AtividadeDTO;
import br.upe.sap.sistemasapupe.api.services.atividades.AtividadeService;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import br.upe.sap.sistemasapupe.exceptions.utils.HttpErrorUtils;
import jakarta.persistence.EntityNotFoundException;
import jakarta.websocket.server.PathParam;
import lombok.AllArgsConstructor;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@RestController
@RequestMapping("/api/v1/atividades/")
public class AtividadeController {

    private AtividadeService atividadeService;

    @GetMapping(value = "/one", params = {"id"})
    public ResponseEntity<AtividadeDTO> getById(@RequestParam("id") UUID uid) {
        return ResponseEntity.ok(atividadeService.getByUid(uid));
    }

    @PostMapping(value = "/many")
    public ResponseEntity<AnyAtividadeDTO> getByIds(@RequestBody List<UUID> uids) {
        return ResponseEntity.ok(atividadeService.getByUids(uids));
    }

    @GetMapping(value = "/many", params = {"status"})
    public ResponseEntity<AnyAtividadeDTO> getByStatus(@RequestParam("status") StatusAtividade status) {
        return ResponseEntity.ok(atividadeService.getByStatus(status));
    }

    @GetMapping(value = "/many", params = "idFuncionario")
    public ResponseEntity<AnyAtividadeDTO> getByFuncionario(@RequestParam("idFuncionario") UUID uid){
        return ResponseEntity.ok(atividadeService.getByFuncionario(uid));
    }

    @GetMapping(value = "/many", params = "idSala")
    public ResponseEntity<AnyAtividadeDTO> getBySala(@RequestParam("idSala") UUID uid){
        return ResponseEntity.ok(atividadeService.getBySala(uid));
    }


    @DeleteMapping(value = "/many")
    public ResponseEntity<?> delete(@RequestBody List<UUID> uids) {
        try {
            atividadeService.delete(uids);
        } catch (EntityNotFoundException e) {
            throw HttpErrorUtils.notFoundException(e.getMessage(), uids, null);
        }

        return ResponseEntity.ok().build();
    }

}
