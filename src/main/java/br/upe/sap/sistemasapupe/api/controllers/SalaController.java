package br.upe.sap.sistemasapupe.api.controllers;

import br.upe.sap.sistemasapupe.api.services.SalaService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/atividades/sala")
@AllArgsConstructor
public class SalaController {
    SalaService salaService;


}
