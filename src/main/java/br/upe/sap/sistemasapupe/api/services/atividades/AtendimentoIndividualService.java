package br.upe.sap.sistemasapupe.api.services.atividades;

import br.upe.sap.sistemasapupe.api.dtos.atividades.AtendimentoIndividualDTO;
import br.upe.sap.sistemasapupe.api.dtos.atividades.CreateAtendimentoIndividualDTO;
import br.upe.sap.sistemasapupe.data.model.atividades.AtendimentoIndividual;
import br.upe.sap.sistemasapupe.data.model.atividades.Atividade;
import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FichaRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.AtividadeRepositoryFacade;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.sala.SalaRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;

import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@Service
@AllArgsConstructor
public class AtendimentoIndividualService {

    AtividadeRepositoryFacade atividadeRepository;
    FuncionarioRepository funcionarioRepository;
    FichaRepository fichaRepository;
    SalaRepository salaRepository;

    public AtendimentoIndividualDTO create(CreateAtendimentoIndividualDTO createDTO){
        Sala sala = salaRepository.findById(salaRepository.findIds(createDTO.sala()).get(createDTO.sala()));
        Funcionario funcionario = funcionarioRepository.findById(funcionarioRepository.findIds(createDTO.funcionario()).get(createDTO.funcionario()));
        Funcionario terapeuta = funcionarioRepository.findById(funcionarioRepository.findIds(createDTO.terapeuta()).get(createDTO.terapeuta()));
        Ficha ficha = fichaRepository.findById(fichaRepository.findIds(createDTO.ficha()).get(createDTO.ficha()));
        Atividade atividadeTransformada = CreateAtendimentoIndividualDTO.from(createDTO, sala, funcionario, terapeuta, ficha);
        AtendimentoIndividual resultado = (AtendimentoIndividual) atividadeRepository.create(atividadeTransformada);
        return AtendimentoIndividualDTO.to(resultado);
    }

    public AtendimentoIndividualDTO update(AtendimentoIndividualDTO dto) {
        Sala sala = salaRepository.findById(salaRepository.findIds(dto.sala()).get(dto.sala()));
        Funcionario funcionario = funcionarioRepository.findById(funcionarioRepository.findIds(dto.funcionario()).get(dto.funcionario()));
        Funcionario terapeuta = funcionarioRepository.findById(funcionarioRepository.findIds(dto.terapeuta()).get(dto.terapeuta()));
        Ficha ficha = fichaRepository.findById(fichaRepository.findIds(dto.ficha()).get(dto.ficha()));
        int id = atividadeRepository.findIds(dto.id()).get(dto.id());

        AtendimentoIndividual atendimentoIndividual = AtendimentoIndividual.builder()
                .id(id)
                .sala(sala)
                .terapeuta(terapeuta)
                .funcionario(funcionario)
                .ficha(ficha)
                .tempoInicio(dto.tempoInicio())
                .tempoFim(dto.tempoFim())
                .statusAtividade(dto.statusAtividade())
                .build();

        AtendimentoIndividual atualizado = (AtendimentoIndividual) atividadeRepository.update(atendimentoIndividual);

        return AtendimentoIndividualDTO.to(atualizado);
    }

    public AtendimentoIndividualDTO getById(UUID id){
        Integer internalId = atividadeRepository.findIds(id).get(id);
        if (internalId == null) {
            return null;
        }
        AtendimentoIndividual atividadeExistente = (AtendimentoIndividual) atividadeRepository.findById(internalId);
        if (atividadeExistente == null) {
            return null;
        }
        return AtendimentoIndividualDTO.to(atividadeExistente);
    }

    //n meche pfv
    public List<AtendimentoIndividualDTO> getByStatus(StatusAtividade status){
        List<Atividade> atividades = atividadeRepository.findByStatus(status);

        return atividades.stream()
                .filter(atividade -> atividade instanceof AtendimentoIndividual)
                .map(atividade -> (AtendimentoIndividual) atividade)
                .map(AtendimentoIndividualDTO::to)
                .collect(Collectors.toList());
    }

    public boolean deleteById(UUID uid) {
        int id = atividadeRepository.findIds(uid).get(uid);
        int rowsAffected = atividadeRepository.delete(id);
        return rowsAffected > 0;
    }


}
