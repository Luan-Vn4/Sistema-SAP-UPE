package br.upe.sap.sistemasapupe.api.controllers;

import br.upe.sap.sistemasapupe.api.dtos.FuncionarioDTO;
import br.upe.sap.sistemasapupe.api.dtos.UpdateFuncionarioDTO;
import br.upe.sap.sistemasapupe.api.services.FuncionarioService;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.Map;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/funcionarios")
@AllArgsConstructor
public class FuncionarioController {

    FuncionarioService funcionarioService;


    // GET
    public enum Search {
        ALL, ATIVOS, INATIVOS, UIDS, TECNICOS, UID, SUPERVISIONADOS;

        public static Search from(String value) {
            return Search.valueOf(value.toUpperCase());
        }
    }

    @GetMapping(value = "/many")
    public ResponseEntity<List<FuncionarioDTO>> getMany(
            @RequestParam(name="by", required=false) String searchType,
            @RequestParam(name = "uid", required=false) UUID uid,
            @RequestBody(required = false) Map<String, List<UUID>> uids) throws BadRequestException{

        List<FuncionarioDTO> result;
        switch (Search.from(searchType)) {
            case ATIVOS -> result = funcionarioService.getByAtivo(true);
            case INATIVOS -> result = funcionarioService.getByAtivo(false);
            case UIDS -> result = searchByUids(uids);
            case TECNICOS -> result = funcionarioService.getAllTecnicos();
            case SUPERVISIONADOS -> result = searchSupervisionados(uid);
            default -> result = funcionarioService.getAll();
        }

        return ResponseEntity.ok().body(result);
    }

    private List<FuncionarioDTO> searchByUids(Map<String, List<UUID>> uids) {
        if (uids == null) throw new RuntimeException();
        return funcionarioService.getByUids(uids.get("uids"));
    }

    private List<FuncionarioDTO> searchSupervisionados(UUID uidTecnico) throws BadRequestException{
        if (uidTecnico == null) throw new BadRequestException("Não foi fornecido um uid de técnico para buscar " +
                                                              "os supervisonados");

        return funcionarioService.getSupervisionados(uidTecnico);
    }

    @GetMapping(value = "/one")
    public ResponseEntity<FuncionarioDTO> getOne(
            @RequestParam(name="by", required=false) String searchType,
            @RequestParam(name="uid", required=false) UUID uid) throws BadRequestException{

        FuncionarioDTO result;
        switch (Search.from(searchType)) {
            case UID -> result = searchByUid(uid);
            default -> throw new BadRequestException("Não foram fornecido um tipo de busca válido");
        }

        return ResponseEntity.ok().body(result);
    }

    private FuncionarioDTO searchByUid(UUID uid) throws BadRequestException{
        if (uid == null) throw new BadRequestException("Não foi fornecido um uid válido");

        return funcionarioService.getByUid(uid);
    }


    // PUT
    @PutMapping("/one")
    public ResponseEntity<FuncionarioDTO> updateCredentials(@RequestBody UpdateFuncionarioDTO dto) {
        return ResponseEntity.ok().body(funcionarioService.updateCredentials(dto));
    }

    @PutMapping("/activation")
    public ResponseEntity<FuncionarioDTO> changeAtivo(@RequestParam(name="uid") UUID uid,
                                                      @RequestParam(name="status") boolean status) {
        return ResponseEntity.ok().body(funcionarioService.updateActivation(uid, status));
    }

    public ResponseEntity<FuncionarioDTO> changeSupervisor(
                                          @RequestParam(name="uid-funcionario") UUID uidFuncionario,
                                          @RequestParam(name="uid-supervisor") UUID uidSupervisor) {
        return ResponseEntity.ok().body(funcionarioService.changeSupervisor(uidFuncionario, uidSupervisor));
    }

}
