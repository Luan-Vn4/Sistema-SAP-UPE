package br.upe.sap.sistemasapupe.api.services.atividades;

import br.upe.sap.sistemasapupe.api.dtos.atividades.encontro.CreateEncontroDTO;
import br.upe.sap.sistemasapupe.api.dtos.atividades.encontro.EncontroDTO;
import br.upe.sap.sistemasapupe.api.services.FuncionarioService;
import br.upe.sap.sistemasapupe.api.services.GrupoEstudoService;
import br.upe.sap.sistemasapupe.api.services.SalaService;
import br.upe.sap.sistemasapupe.data.model.atividades.Atividade;
import br.upe.sap.sistemasapupe.data.model.atividades.Encontro;
import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoEstudo;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.AtividadeRepositoryFacade;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.BidiMap;
import java.util.List;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
@AllArgsConstructor
public class EncontroService {

    AtividadeRepositoryFacade atividadeRepository;

    FuncionarioService funcionarioService;

    SalaService salaService;

    GrupoEstudoService grupoEstudoService;

    public EncontroDTO create(CreateEncontroDTO dto) {
        // Falta tratar exceções caso não existam (retornem null)
        Funcionario funcionario = funcionarioService.getFuncionarioByUid(dto.idFuncionario());
        Sala sala = salaService.getSalaByUid(dto.idSala());
        GrupoEstudo grupoEstudo = grupoEstudoService.getGrupoEstudoByUid(dto.idGrupoEstudo());

        Encontro received = dto.toEncontro(sala,funcionario,grupoEstudo.getId());
        Encontro result = (Encontro) atividadeRepository.create(received);

        return EncontroDTO.from(result, grupoEstudo.getUid());
    }

    public EncontroDTO update(EncontroDTO dto) {
        var atividade = (Encontro) getAtividadeByUid(dto.id());

        if (atividade == null) throw
                new EntityNotFoundException("Não existe um encontro com UID: " + dto.id());

        Funcionario funcionario = funcionarioService.getFuncionarioByUid(dto.idFuncionario());
        Sala sala = salaService.getSalaByUid(dto.idSala());
        GrupoEstudo grupoEstudo = grupoEstudoService.getGrupoEstudoByUid(dto.idGrupoEstudo());

        atividade.setSala(valueOrElse(sala, atividade.getSala()));
        atividade.setFuncionario(valueOrElse(funcionario, atividade.getFuncionario()));
        atividade.setTempoInicio(valueOrElse(dto.tempoInicio(),atividade.getTempoInicio()));
        atividade.setTempoFim(valueOrElse(dto.tempoFim(), atividade.getTempoFim()));
        atividade.setStatus(valueOrElse(dto.statusAtividade(), atividade.getStatus()));
        atividade.setIdGrupoEstudo(valueOrElse(grupoEstudo.getId(),atividade.getIdGrupoEstudo()));

        return EncontroDTO.from(atividade, grupoEstudo.getUid());
    }

    private <T> T valueOrElse(T value, T alternative) {
        return (value != null ? value : alternative);
    }

    public EncontroDTO getByUid(UUID uid) {
        Encontro result = (Encontro) getAtividadeByUid(uid);

        if (result == null) throw new EntityNotFoundException("Não existe um encontro com o UID: " + uid);

        return EncontroDTO.from(result, getUidGrupoEstudo(result.getIdGrupoEstudo()));
    }

    private UUID getUidGrupoEstudo(int idGrupoEstudo) {
        return grupoEstudoService.getGrupoEstudoById(idGrupoEstudo).getUid();
    }

    public List<EncontroDTO> getByStatus(StatusAtividade status) {
        List<Atividade> result = atividadeRepository.findByStatus(status);

        return result.stream()
            .filter(atividade -> atividade instanceof Encontro)
            .map(atividade -> {
                Encontro encontro = (Encontro) atividade;
                return EncontroDTO.from(encontro, getUidGrupoEstudo(encontro.getIdGrupoEstudo()));
            }).toList();
    }

    public Atividade getAtividadeByUid(UUID uid) {
        Integer id = atividadeRepository.findIds(uid).get(uid);

        if (id == null) return null;

        return atividadeRepository.findById(id);
    }

    public List<Atividade> getAtividadeByUids(List<UUID> uids) {
        BidiMap<UUID, Integer> ids = atividadeRepository.findIds(uids);

        if (ids.isEmpty()) return List.of();

        return atividadeRepository.findById(ids.values().stream().toList());
    }

    public void deleteByUid(UUID uid) {
        Integer id = atividadeRepository.findIds(uid).get(uid);

        if (id == null) throw new EntityNotFoundException("Não há uma entidade com o UID: " + uid);

        atividadeRepository.delete(id);
    }

}
