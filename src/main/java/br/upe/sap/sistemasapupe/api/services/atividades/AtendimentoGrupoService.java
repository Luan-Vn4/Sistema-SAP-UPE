package br.upe.sap.sistemasapupe.api.services.atividades;

import br.upe.sap.sistemasapupe.api.dtos.atividades.AtendimentoGrupoDTO;
import br.upe.sap.sistemasapupe.api.dtos.atividades.AtendimentoIndividualDTO;
import br.upe.sap.sistemasapupe.api.dtos.atividades.CreateAtendimentoGrupoDTO;
import br.upe.sap.sistemasapupe.data.model.atividades.AtendimentoGrupo;
import br.upe.sap.sistemasapupe.data.model.atividades.AtendimentoIndividual;
import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.GrupoTerapeuticoRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.AtividadeRepositoryFacade;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.sala.SalaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.UUID;

@Service
@AllArgsConstructor
public class AtendimentoGrupoService {

    AtividadeRepositoryFacade atividadeRepository;
    SalaRepository salaRepository;
    FuncionarioRepository funcionarioRepository;
    GrupoTerapeuticoRepository grupoTerapeuticoRepository;

    public AtendimentoGrupoDTO create(CreateAtendimentoGrupoDTO atendimentoGrupoDTO){
        int idGrupoTerapeutico = grupoTerapeuticoRepository
                .findIds(atendimentoGrupoDTO.idGrupoTerapeutico())
                .get(atendimentoGrupoDTO.idGrupoTerapeutico());
        Sala sala = salaRepository.findById(salaRepository.findIds(atendimentoGrupoDTO.idSala()).get(atendimentoGrupoDTO.idSala()));
        Funcionario funcionario = funcionarioRepository.findById(funcionarioRepository.findIds(atendimentoGrupoDTO.idFuncionario())
                .get(atendimentoGrupoDTO.idFuncionario()));

        AtendimentoGrupo atividadeTransformada = CreateAtendimentoGrupoDTO.from(atendimentoGrupoDTO, idGrupoTerapeutico, sala, funcionario);
        AtendimentoGrupo resultado = (AtendimentoGrupo) atividadeRepository.create(atividadeTransformada);
        return AtendimentoGrupoDTO.to(resultado, atendimentoGrupoDTO.idGrupoTerapeutico());
    }

    public AtendimentoGrupoDTO update(AtendimentoGrupoDTO dto) {
        Sala sala = salaRepository.findById(salaRepository.findIds(dto.sala()).get(dto.sala()));
        Funcionario funcionario = funcionarioRepository.findById(funcionarioRepository.findIds(dto.funcionario()).get(dto.funcionario()));
        int idGrupoTerapeutico = grupoTerapeuticoRepository.findIds(dto.idGrupoTerapeutico()).get(dto.idGrupoTerapeutico());

        int id = atividadeRepository.findIds(dto.id()).get(dto.id());

        AtendimentoGrupo atendimentoGrupo = AtendimentoGrupo.builder()
                .id(id)
                .sala(sala)
                .funcionario(funcionario)
                .tempoInicio(dto.tempoInicio())
                .tempoFim(dto.tempoFim())
                .statusAtividade(dto.statusAtividade())
                .idGrupoTerapeutico(idGrupoTerapeutico)
                .build();

        AtendimentoGrupo atualizado = (AtendimentoGrupo) atividadeRepository.update(atendimentoGrupo);

        return AtendimentoGrupoDTO.to(atualizado, dto.idGrupoTerapeutico());
    }

    public AtendimentoGrupoDTO getById(UUID id){
        AtendimentoGrupo atividadeExistente = (AtendimentoGrupo) atividadeRepository
                .findById(atividadeRepository.findIds(id).get(id));

        if (atividadeExistente == null) {
            throw new EntityNotFoundException("Atendimento grupo não encontrado para o id " + atividadeRepository.findIds(id).get(id));
        }

        int idGrupoTerapeutico = atividadeExistente.getIdGrupoTerapeutico();
        UUID uidGrupoTerapeutico = grupoTerapeuticoRepository.findById(idGrupoTerapeutico).getUid();


        return AtendimentoGrupoDTO.to(atividadeExistente, uidGrupoTerapeutico);
    }
}
