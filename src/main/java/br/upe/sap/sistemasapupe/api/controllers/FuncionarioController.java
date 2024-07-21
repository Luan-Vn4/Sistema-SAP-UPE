package br.upe.sap.sistemasapupe.api.controllers;

import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.api.dtos.funcionarios.UpdateFuncionarioDTO;
import br.upe.sap.sistemasapupe.api.services.FuncionarioService;
import br.upe.sap.sistemasapupe.exceptions.utils.HttpErrorUtils;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/funcionarios")
@AllArgsConstructor
public class FuncionarioController {

    // DEPENDÊNCIAS
    FuncionarioService funcionarioService;


    // BUSCAS
    @GetMapping("/many/all")
    public ResponseEntity<List<FuncionarioDTO>> searchAll() {
        return ResponseEntity.ok().body(funcionarioService.getAll());
    }

    @PostMapping(value = "/many/uids")
    public ResponseEntity<List<FuncionarioDTO>> searchByUids(@RequestBody List<UUID> uids)
                                                             throws BadRequestException{
        if (uids == null) throw new BadRequestException("Nenhuma lista de uids fornecida no corpo da " +
                                                        "requisição");
        if (uids.isEmpty()) return ResponseEntity.ok(List.of());

        List<FuncionarioDTO> result = funcionarioService.getByUids(uids);
        return ResponseEntity.ok().body(result);
    }

    @GetMapping(value = "/many/ativos")
    public ResponseEntity<List<FuncionarioDTO>> searchByAtivos() {
        return ResponseEntity.ok(funcionarioService.getByAtivo(true));
    }

    @GetMapping(value = "/many/inativos")
    public ResponseEntity<List<FuncionarioDTO>> searchByInativos() {
        return ResponseEntity.ok(funcionarioService.getByAtivo(false));
    }

    @GetMapping(value = "/many/tecnicos")
    public ResponseEntity<List<FuncionarioDTO>> searchByTecnicos() {
        return ResponseEntity.ok(funcionarioService.getAllTecnicos());
    }

    @GetMapping(value = "/many/supervisionados", params = {"uid"})
    public ResponseEntity<List<FuncionarioDTO>> searchBySupervisionados(@RequestParam UUID uid) {
        List<FuncionarioDTO> result;
        try {
            result = funcionarioService.getSupervisionados(uid);
        } catch (IllegalArgumentException e) {
            throw HttpErrorUtils.unprocessableEntityException(e.getMessage(), null, null);
        } catch (EntityNotFoundException e) {
            throw HttpErrorUtils.notFoundException(e.getMessage(), null,null);
        }
        return ResponseEntity.ok(result);
    }

    @GetMapping(value = "/one", params = {"uid"})
    public ResponseEntity<FuncionarioDTO> searchByUid(@RequestParam UUID uid) {
        return ResponseEntity.ok(funcionarioService.getByUid(uid));
    }


    // UPDATES
    @PutMapping("/one")
    public ResponseEntity<FuncionarioDTO> updateCredentials(@RequestBody UpdateFuncionarioDTO dto) {
        try {
            return ResponseEntity.ok().body(funcionarioService.updateCredentials(dto));
        } catch (EntityNotFoundException e) {
            throw HttpErrorUtils.notFoundException(e.getMessage(),null,null);
        }
    }

    @PutMapping("/one/activation")
    public ResponseEntity<FuncionarioDTO> changeAtivo(@RequestParam(name="uid") UUID uid,
                                                      @RequestParam(name="status") boolean status) {
        return ResponseEntity.ok().body(funcionarioService.updateActivation(uid, status));
    }

    @PutMapping(value = "/one/supervisor", params = {"uid-funcionario", "uid-supervisor"})
    public ResponseEntity<FuncionarioDTO> changeSupervisor(
                                          @RequestParam(name="uid-funcionario") UUID uidFuncionario,
                                          @RequestParam(name="uid-supervisor") UUID uidSupervisor) {
        try {
            return ResponseEntity.ok().body(funcionarioService.changeSupervisor(uidFuncionario, uidSupervisor));
        } catch (IllegalArgumentException e) {
            throw HttpErrorUtils.unprocessableEntityException(e.getMessage(), null, null);
        }
    }

}
