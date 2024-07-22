package br.upe.sap.sistemasapupe.api.services.atividades;

import br.upe.sap.sistemasapupe.api.dtos.atividades.AtendimentoIndividualDTO;
import br.upe.sap.sistemasapupe.api.dtos.atividades.CreateAtendimentoIndividualDTO;
import br.upe.sap.sistemasapupe.data.model.atividades.Atividade;
import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FichaRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.AtividadeRepositoryFacade;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.sala.SalaRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class AtendimentoIndividualService {

    AtividadeRepositoryFacade atividadeRepository;
    FuncionarioRepository funcionarioRepository;
    FichaRepository fichaRepository;
    SalaRepository salaRepository;

    public Atividade create(CreateAtendimentoIndividualDTO createDTO){
        Sala sala = salaRepository.findById(salaRepository.findIds(createDTO.sala()).get(createDTO.sala()));
        Funcionario funcionario = funcionarioRepository.findById(funcionarioRepository.findIds(createDTO.funcionario()).get(createDTO.funcionario()));
        Funcionario terapeuta = funcionarioRepository.findById(funcionarioRepository.findIds(createDTO.terapeuta()).get(createDTO.terapeuta()));
        Ficha ficha = fichaRepository.findById(fichaRepository.findIds(createDTO.ficha()).get(createDTO.ficha()));
        Atividade atividadeTransformada = CreateAtendimentoIndividualDTO.from(createDTO, sala, funcionario, terapeuta, ficha);
        return atividadeRepository.create(atividadeTransformada);
    }

    public Atividade update(AtendimentoIndividualDTO dto){
        Sala sala = salaRepository.findById(salaRepository.findIds(dto.sala()).get(dto.sala()));
        Funcionario funcionario = funcionarioRepository.findById(funcionarioRepository.findIds(dto.funcionario()).get(dto.funcionario()));
        Funcionario terapeuta = funcionarioRepository.findById(funcionarioRepository.findIds(dto.terapeuta()).get(dto.terapeuta()));
        Ficha ficha = fichaRepository.findById(fichaRepository.findIds(dto.ficha()).get(dto.ficha()));

        Atividade atividadeExistente = atividadeRepository
                .findById(atividadeRepository.findIds(dto.id()).get(dto.id()));
        if (atividadeExistente == null) {
            throw new EntityNotFoundException("Grupo de estudos não encontrado para o id " + atividadeRepository.findIds(dto.id()).get(dto.id()));
        }

        atividadeExistente.setSala(sala);
        atividadeExistente.setFuncionario(funcionario);
        atividadeExistente.setStatus(dto.statusAtividade());
        atividadeExistente.setTempoInicio(dto.tempoInicio());
        atividadeExistente.setTempoFim(dto.tempoFim());

        return atividadeRepository.update(atividadeExistente);
    }

    public Atividade getById(AtendimentoIndividualDTO dto){
        Sala sala = salaRepository.findById(salaRepository.findIds(dto.sala()).get(dto.sala()));
        Funcionario funcionario = funcionarioRepository.findById(funcionarioRepository.findIds(dto.funcionario()).get(dto.funcionario()));
        Funcionario terapeuta = funcionarioRepository.findById(funcionarioRepository.findIds(dto.terapeuta()).get(dto.terapeuta()));
        Ficha ficha = fichaRepository.findById(fichaRepository.findIds(dto.ficha()).get(dto.ficha()));

        Atividade atividadeExistente = atividadeRepository
                .findById(atividadeRepository.findIds(dto.id()).get(dto.id()));
        if (atividadeExistente == null) {
            throw new EntityNotFoundException("Grupo de estudos não encontrado para o id " + atividadeRepository.findIds(dto.id()).get(dto.id()));
        }

        return atividadeExistente;
    }


}
