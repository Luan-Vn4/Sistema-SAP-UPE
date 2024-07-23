package br.upe.sap.sistemasapupe.api.services.atividades;

import br.upe.sap.sistemasapupe.api.dtos.atividades.atendimentoindividual.AtendimentoIndividualDTO;
import br.upe.sap.sistemasapupe.api.dtos.atividades.atendimentoindividual.CreateAtendimentoIndividualDTO;
import br.upe.sap.sistemasapupe.api.services.FichaService;
import br.upe.sap.sistemasapupe.api.services.FuncionarioService;
import br.upe.sap.sistemasapupe.api.services.SalaService;
import br.upe.sap.sistemasapupe.data.model.atividades.AtendimentoIndividual;
import br.upe.sap.sistemasapupe.data.model.atividades.Atividade;
import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.AtividadeRepositoryFacade;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.BidiMap;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AtendimentoIndividualService {

    AtividadeRepositoryFacade atividadeRepository;

    FuncionarioService funcionarioService;

    FichaService fichaService;

    SalaService salaService;

    public AtendimentoIndividualDTO create(CreateAtendimentoIndividualDTO createDTO) {
        Sala sala = salaService.getSalaByUid(createDTO.idSala());
        Funcionario funcionario = funcionarioService.getFuncionarioByUid(createDTO.idFuncionario());
        Funcionario terapeuta = funcionarioService.getFuncionarioByUid(createDTO.idFuncionario());
        Ficha ficha = fichaService.getFichaByUid(createDTO.idFicha());

        Atividade received = createDTO.to(sala, funcionario, terapeuta, ficha);
        AtendimentoIndividual result = (AtendimentoIndividual) atividadeRepository.create(received);
        return getDTOfrom(result);
    }

    public AtendimentoIndividualDTO update(AtendimentoIndividualDTO dto) {
        var atividade = (AtendimentoIndividual) getAtividadeByUid(dto.id());

        if (atividade == null) throw new EntityNotFoundException("Não existe um atendimento individual com " +
                                                                 "UID: " + dto.id());

        Sala sala = salaService.getSalaByUid(dto.idSala());
        Funcionario funcionario = funcionarioService.getFuncionarioByUid(dto.idFuncionario());
        Funcionario terapeuta = funcionarioService.getFuncionarioByUid(dto.idTerapeuta());
        Ficha ficha = fichaService.getFichaByUid(dto.idFicha());

        atividade.setSala(valueOrElse(sala, atividade.getSala()));
        atividade.setTerapeuta(valueOrElse(terapeuta, atividade.getTerapeuta()));
        atividade.setFuncionario(valueOrElse(funcionario, atividade.getFuncionario()));
        atividade.setFicha(valueOrElse(ficha, atividade.getFicha()));
        atividade.setTempoInicio(valueOrElse(dto.tempoInicio(),atividade.getTempoInicio()));
        atividade.setTempoFim(valueOrElse(dto.tempoFim(), atividade.getTempoFim()));
        atividade.setStatus(valueOrElse(dto.statusAtividade(), atividade.getStatus()));

        atividadeRepository.update(atividade);

        return getDTOfrom((AtendimentoIndividual) getAtividadeByUid(dto.id()));
    }

    private <T> T valueOrElse(T value, T alternative) {
        return (value != null ? value : alternative);
    }

    private Atividade getAtividadeByUid(UUID uid) {
        Integer id = atividadeRepository.findIds(uid).get(uid);

        if (id == null) return null;

        return atividadeRepository.findById(id);
    }

    private List<Atividade> getAtividadeByUids(List<UUID> uids) {
        BidiMap<UUID, Integer> ids = atividadeRepository.findIds(uids);

        if (ids.isEmpty()) return List.of();

        return atividadeRepository.findById(ids.values().stream().toList());
    }

    public AtendimentoIndividualDTO getById(UUID uid) {
        AtendimentoIndividual result = (AtendimentoIndividual) getAtividadeByUid(uid);

        if (result == null) throw new EntityNotFoundException("Não existe um atendimento individual com o" +
                                                              "UID: " + uid);

        return getDTOfrom(result);
    }

    public AtendimentoIndividualDTO getDTOfrom(AtendimentoIndividual atividade) {
        return AtendimentoIndividualDTO.from(atividade);
    }

    public List<AtendimentoIndividualDTO> getByStatus(StatusAtividade status){
        List<Atividade> atividades = atividadeRepository.findByStatus(status);

        return atividades.stream()
            .filter(atividade -> atividade instanceof AtendimentoIndividual)
            .map(atividade -> (AtendimentoIndividual) atividade)
            .map(this::getDTOfrom)
            .toList();
    }

    public void deleteByUid(UUID uid) {
        Integer id = atividadeRepository.findIds(uid).get(uid);

        if (id == null) throw new jakarta.persistence.EntityNotFoundException("Não há uma entidade com o UID: " + uid);

        atividadeRepository.delete(id);
    }


}
