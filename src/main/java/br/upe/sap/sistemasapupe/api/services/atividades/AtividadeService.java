package br.upe.sap.sistemasapupe.api.services.atividades;

import br.upe.sap.sistemasapupe.api.dtos.atividades.geral.AnyAtividadeDTO;
import br.upe.sap.sistemasapupe.api.dtos.atividades.geral.AtividadeDTO;
import br.upe.sap.sistemasapupe.api.services.FuncionarioService;
import br.upe.sap.sistemasapupe.api.services.SalaService;
import br.upe.sap.sistemasapupe.data.model.atividades.*;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.AtividadeRepositoryFacade;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.BidiMap;
import org.springframework.stereotype.Service;

import javax.persistence.EntityNotFoundException;
import java.util.List;
import java.util.Map;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AtividadeService {

    AtividadeRepositoryFacade atividadeRepository;

    AtendimentoGrupoService atdGrupoService;

    EncontroService encontroService;

    AtendimentoIndividualService atdIndividualService;

    FuncionarioService funcionarioService;

    SalaService salaService;

    public AtividadeDTO updateStatus(StatusAtividade statusAtividade, UUID uidAtividade) {
        Atividade atividade = getAtividadeByUid(uidAtividade);

        atividade.setStatus(statusAtividade);

        return mapToDTO(getAtividadeByUid(uidAtividade));
    }

    public AtividadeDTO getByUid(UUID uid) {
        Atividade atividade = getAtividadeByUid(uid);

        if (atividade == null) throw new EntityNotFoundException("Não há uma atividade com o UID: " + uid);

        return mapToDTO(atividade);
    }

    public AnyAtividadeDTO getByUids(List<UUID> uids) {

        return getAtividadeByUid(uids).stream()
            .map(this::mapToDTO)
            .collect(AnyAtividadeDTO.collector());
    }

    public AnyAtividadeDTO getAll() {
        return atividadeRepository.findAll().stream()
            .map(this::mapToDTO)
            .collect(AnyAtividadeDTO.collector());
    }

    public AnyAtividadeDTO getByStatus(StatusAtividade status) {
        return atividadeRepository.findByStatus(status).stream()
            .map(this::mapToDTO)
            .collect(AnyAtividadeDTO.collector());
    }

    public AnyAtividadeDTO getByFuncionario(UUID uidFuncionario) {
        int idFuncionario = funcionarioService.getFuncionarioByUid(uidFuncionario).getId();

        return atividadeRepository.findByFuncionario(idFuncionario).stream()
            .map(this::mapToDTO)
            .collect(AnyAtividadeDTO.collector());
    }

    public AnyAtividadeDTO getBySala(UUID uidSala) {
        Sala sala = salaService.getSalaByUid(uidSala);

        if (sala == null) throw new EntityNotFoundException("Não existe uma sala com o UID: " + uidSala);

        return atividadeRepository.findBySala(sala.getId()).stream()
            .map(this::mapToDTO)
            .collect(AnyAtividadeDTO.collector());
    }

    private AtividadeDTO mapToDTO(Atividade atividade) {
        if (atividade instanceof AtendimentoIndividual atendimentoIndividual) {
            return atdIndividualService.getDTOfrom(atendimentoIndividual);
        } else if (atividade instanceof AtendimentoGrupo atendimentoGrupo) {
            return atdGrupoService.getDtoFrom(atendimentoGrupo);
        }
        return encontroService.getDTOFrom((Encontro) atividade);
    }

    public Atividade getAtividadeByUid(UUID uid) {
        Integer id = atividadeRepository.findIds(uid).get(uid);

        if (id == null) return null;

        return atividadeRepository.findById(id);
    }

    public List<Atividade> getAtividadeByUid(List<UUID> uids) {
        BidiMap<UUID, Integer> ids = atividadeRepository.findIds(uids);

        if (ids.isEmpty()) return List.of();

        return atividadeRepository.findById(ids.values().stream().toList());
    }

    /**
     * Deleta uma atividade
     * @param uidAtividade {@link UUID} da atividade que deseja deletar
     * @throws EntityNotFoundException caso não exista uma atividade com o uuid passado
     */
    public void delete(UUID uidAtividade) {
        BidiMap<UUID, Integer> ids = atividadeRepository.findIds(uidAtividade);

        if (ids.isEmpty()) throw new EntityNotFoundException("Não há uma entidade com o UID: " + uidAtividade);

        atividadeRepository.delete(ids.values().stream().toList());
    }

    public void delete(List<UUID> uidsAtividades) {
        BidiMap<UUID, Integer> ids = atividadeRepository.findIds(uidsAtividades);

        for (Map.Entry<UUID, Integer> entry : ids.entrySet()) {
            if (!atividadeRepository.exists(entry.getValue())) throw
                new EntityNotFoundException("Não há um entidade com o UID: " + entry.getKey());
        }

        atividadeRepository.delete(ids.values().stream().toList());
    }

}
