package br.upe.sap.sistemasapupe.api.controllers;

import br.upe.sap.sistemasapupe.api.services.AtividadeService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api/v1/atividades")
@AllArgsConstructor
public class AtividadeController {
    AtividadeService atividadeService;


}
