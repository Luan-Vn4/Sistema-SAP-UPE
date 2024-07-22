package br.upe.sap.sistemasapupe.api.services.atividades;

import br.upe.sap.sistemasapupe.api.dtos.atividades.AtendimentoGrupoDTO;
import br.upe.sap.sistemasapupe.api.dtos.atividades.CreateAtendimentoGrupoDTO;
import br.upe.sap.sistemasapupe.data.model.atividades.AtendimentoGrupo;
import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.GrupoTerapeuticoRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.sala.SalaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AtendimentoGrupoService {

    SalaRepository salaRepository;
    FuncionarioRepository funcionarioRepository;
    GrupoTerapeuticoRepository grupoTerapeuticoRepository;

    public AtendimentoGrupoDTO create(CreateAtendimentoGrupoDTO atendimentoGrupoDTO){
        int idGrupoTerapeutico = grupoTerapeuticoRepository
                .findIds(atendimentoGrupoDTO.idGrupoTerapeutico())
                .get(atendimentoGrupoDTO.idGrupoTerapeutico());
        Sala sala = salaRepository;
        Funcionario funcionario = funcionarioRepository.;

        AtendimentoGrupo atendimentoGrupo = CreateAtendimentoGrupoDTO.to(atendimentoGrupoDTO, idGrupoTerapeutico, );
    }
}
