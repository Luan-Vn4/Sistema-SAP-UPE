package br.upe.sap.sistemasapupe.api.services;

import br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.AtividadeRepositoryFacade;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AtividadeService {

    AtividadeRepositoryFacade atividadesRepository;

}
