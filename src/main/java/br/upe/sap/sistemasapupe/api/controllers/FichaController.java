package br.upe.sap.sistemasapupe.api.controllers;

import br.upe.sap.sistemasapupe.api.dtos.paciente.CreateFichaDTO;
import br.upe.sap.sistemasapupe.api.dtos.paciente.FichaDTO;
import br.upe.sap.sistemasapupe.api.dtos.paciente.UpdateFichaDTO;
import br.upe.sap.sistemasapupe.api.services.FichaService;
import lombok.AllArgsConstructor;
import org.apache.coyote.BadRequestException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/fichas")
@AllArgsConstructor
public class FichaController {
    //dependencias
    FichaService fichaService;

    //Criar
    @PostMapping("/")
    public ResponseEntity<FichaDTO> createFicha(@RequestBody CreateFichaDTO fichaDTO) {
        FichaDTO ficha = fichaService.createFicha(fichaDTO);
        return ResponseEntity.ok().body(ficha);
    }


    //buscas
    @GetMapping("/many/all")
    public ResponseEntity<List<FichaDTO>> searchAll(){
        return ResponseEntity.ok().body(fichaService.getAll());
    }

    @PostMapping(value = "/many/uids", params =  {"uid"})
    public ResponseEntity<List<FichaDTO>> searchByUids(@RequestBody List<UUID> uids) throws BadRequestException{
            if (uids == null) throw new BadRequestException("Nenhuma lista de uids fornecida no corpo da " +
                    "requisição");
            if (uids.isEmpty()) return ResponseEntity.ok(List.of());

            List<FichaDTO> result = fichaService.getFichaByUids(uids);
            return ResponseEntity.ok().body(result);
    }

    @GetMapping(value = "/one", params = {"uid"})
    public ResponseEntity<FichaDTO> searchByUid(@RequestParam UUID uid) {
        return ResponseEntity.ok(fichaService.getFichaByUid(uid));
    }

    @GetMapping(value = "/many/funcionario", params = {"uidFuncionario"})
    public ResponseEntity<List<FichaDTO>> searchByFuncionarios(@RequestParam UUID uidFuncionario){
        return ResponseEntity.ok(fichaService.getFichaByFuncionario(uidFuncionario));
    }

    // UPDATES
    @PutMapping("/one")
    public ResponseEntity<FichaDTO> updateFicha (@RequestBody UpdateFichaDTO dto){
        try {
            return ResponseEntity.ok().body(fichaService.updateFicha(dto));
        } catch (Exception e) {
            throw new RuntimeException("O update da ficha não foi realizado.", e);
        }
    }

    // deletes
    @DeleteMapping(value = "/delete/", params = {"uid"})
    public ResponseEntity<Void> deleteFicha(@RequestParam UUID uid){
        boolean deleted = fichaService.deleteFichaByUid(uid);
        return deleted? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping(value = "many/delete/", params = {"uid"})
    public ResponseEntity<Void> deleteFichas(@RequestParam List<UUID> uids){
        boolean deleted = fichaService.deleteFichaByUids(uids);
        return deleted ? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }
}
