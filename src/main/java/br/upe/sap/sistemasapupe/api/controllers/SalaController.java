package br.upe.sap.sistemasapupe.api.controllers;

import br.upe.sap.sistemasapupe.api.dtos.atividades.sala.CreateSalaDTO;
import br.upe.sap.sistemasapupe.api.dtos.atividades.sala.SalaDTO;
import br.upe.sap.sistemasapupe.api.services.SalaService;
import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.model.enums.TipoSala;
import lombok.AllArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.net.URI;
import java.util.Collections;
import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/v1/sala")
@AllArgsConstructor
public class SalaController {
    SalaService salaService;

    @GetMapping("/all")
    public List<SalaDTO> getAll() {
        return salaService.getAll();
    }

    @GetMapping("/nome/{nome}")
    public ResponseEntity<SalaDTO> getByNome(@PathVariable String nome) {
        SalaDTO sala = salaService.getSalaByNome(nome);
        return sala != null ? ResponseEntity.ok(sala) : ResponseEntity.notFound().build();
    }

    @GetMapping("/{uid}")
    public ResponseEntity<SalaDTO> getByUid(@PathVariable UUID uid) {
        SalaDTO sala = salaService.getSalaByUid(uid);
        return sala != null ? ResponseEntity.ok(sala) : ResponseEntity.notFound().build();
    }

    @GetMapping("/tipo/{tipoSala}")
    public ResponseEntity<List<SalaDTO>> getByTipo(@PathVariable String tipoSala){
        List<SalaDTO> salas = salaService.getSalaByTipo(TipoSala.valueOf(tipoSala));
        if (salas.isEmpty()) {
            return ResponseEntity.notFound().build();
        } else {
            return ResponseEntity.ok(salas);
        }
    }

    @PutMapping("/update")
    public ResponseEntity<SalaDTO> updateSala(@RequestBody SalaDTO salaDTO) {
        SalaDTO sala = salaService.updateSala(salaDTO);
        return ResponseEntity.ok(sala);
    }

    @PostMapping("/")
    public ResponseEntity<SalaDTO> createSala(@RequestBody CreateSalaDTO salaDTO) {
        SalaDTO createdSala = salaService.createSala(salaDTO);
        return ResponseEntity.created(URI.create("/sala/" + createdSala.uid())).body(createdSala);
    }

    @DeleteMapping("/delete/{uid}")
    public ResponseEntity<Void> deleteSala(@PathVariable UUID uid) {
        boolean deleted = salaService.deleteSalaByUid(uid);
        return deleted? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

    @DeleteMapping("/delete/many")
    public ResponseEntity<Void> deleteSalas(@RequestBody List<UUID> uids) {
        boolean deleted = salaService.deleteSalaByUids(uids);
        return deleted? ResponseEntity.noContent().build() : ResponseEntity.notFound().build();
    }

}
