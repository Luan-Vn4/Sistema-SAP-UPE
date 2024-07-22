package br.upe.sap.sistemasapupe.api.services.atividades;

import br.upe.sap.sistemasapupe.api.services.FichaService;
import br.upe.sap.sistemasapupe.api.services.FuncionarioService;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.AtividadeRepositoryFacade;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AtendimentoGrupoService {

    AtividadeRepositoryFacade atividadeRepository;

    FichaService fichaService;

    FuncionarioService funcionarioService;



}
