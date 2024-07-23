package br.upe.sap.sistemasapupe.api.services.atividades;

import br.upe.sap.sistemasapupe.api.dtos.atividades.atendimentogrupo.AtendimentoGrupoDTO;
import br.upe.sap.sistemasapupe.api.dtos.atividades.atendimentogrupo.CreateAtendimentoGrupoDTO;
import br.upe.sap.sistemasapupe.api.services.FichaService;
import br.upe.sap.sistemasapupe.api.services.FuncionarioService;
import br.upe.sap.sistemasapupe.api.services.GrupoTerapeuticoService;
import br.upe.sap.sistemasapupe.api.services.SalaService;
import br.upe.sap.sistemasapupe.data.model.atividades.AtendimentoGrupo;
import br.upe.sap.sistemasapupe.data.model.atividades.Atividade;
import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.AtividadeRepositoryFacade;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class AtendimentoGrupoService {

    AtividadeRepositoryFacade atividadeRepository;

    FuncionarioService funcionarioService;

    FichaService fichaService;

    SalaService salaService;

    GrupoTerapeuticoService grupoTerapeuticoService;

    public AtendimentoGrupoDTO getDtoFrom(AtendimentoGrupo atendimentoGrupo) {
        UUID uidGrupoTerapeutico = grupoTerapeuticoService.getById(
                atendimentoGrupo.getIdGrupoTerapeutico()).getUid();

        return AtendimentoGrupoDTO.from(atendimentoGrupo, uidGrupoTerapeutico);
    }

    public AtendimentoGrupoDTO create(CreateAtendimentoGrupoDTO atendimentoGrupoDTO){
        Sala sala = salaService.getSalaByUid(atendimentoGrupoDTO.idSala());
        Funcionario funcionario = funcionarioService.getFuncionarioByUid(atendimentoGrupoDTO.idFuncionario());
        GrupoTerapeutico grupoTerapeutico = grupoTerapeuticoService.getGrupoTerapeuticoByUid(atendimentoGrupoDTO.idGrupoTerapeutico());

        AtendimentoGrupo received = atendimentoGrupoDTO.toAtendimentoGrupo(grupoTerapeutico.getId(), sala, funcionario); //aqui
        AtendimentoGrupo result = (AtendimentoGrupo) atividadeRepository.create(received);
        return AtendimentoGrupoDTO.from(result, grupoTerapeutico.getUid());
    }

    public AtendimentoGrupoDTO update(AtendimentoGrupoDTO dto) {
        var atividade = (AtendimentoGrupo) getAtividadeByUid(dto.id());

        if (atividade == null) throw
                new EntityNotFoundException("Não existe um atendimento em grupo com UID: " + dto.id());

        Funcionario funcionario = funcionarioService.getFuncionarioByUid(dto.idFuncionario());
        Sala sala = salaService.getSalaByUid(dto.idSala());
        GrupoTerapeutico grupoTerapeutico = grupoTerapeuticoService.getGrupoTerapeuticoByUid(dto.idGrupoTerapeutico());

        atividade.setSala(valueOrElse(sala, atividade.getSala()));
        atividade.setFuncionario(valueOrElse(funcionario, atividade.getFuncionario()));
        atividade.setIdGrupoTerapeutico(valueOrElse(grupoTerapeutico.getId(),atividade.getIdGrupoTerapeutico()));
        atividade.setTempoInicio(valueOrElse(dto.tempoInicio(),atividade.getTempoInicio()));
        atividade.setTempoFim(valueOrElse(dto.tempoFim(), atividade.getTempoFim()));
        atividade.setStatus(valueOrElse(dto.statusAtividade(), atividade.getStatus()));

        atividadeRepository.update(atividade);

        AtendimentoGrupo result = (AtendimentoGrupo) getAtividadeByUid(dto.id());

        return AtendimentoGrupoDTO.from(result, grupoTerapeutico.getUid());
    }

    private <T> T valueOrElse(T value, T alternative) {
        return (value != null ? value : alternative);
    }

    public Atividade getAtividadeByUid(UUID uid) {
        Integer id = atividadeRepository.findIds(uid).get(uid);

        if (id == null) return null;

        return atividadeRepository.findById(id);
    }

    public AtendimentoGrupoDTO getById(UUID id){
        AtendimentoGrupo atividadeExistente = (AtendimentoGrupo) atividadeRepository
                .findById(atividadeRepository.findIds(id).get(id));

        if (atividadeExistente == null) {
            throw new EntityNotFoundException("Atendimento grupo não encontrado para o id " + atividadeRepository.findIds(id).get(id));
        }

        return AtendimentoGrupoDTO.from(atividadeExistente, getUidGrupoTerapeutico(atividadeExistente.getIdGrupoTerapeutico()));
    }

    private UUID getUidGrupoTerapeutico(int idGrupoTerapeutico) {
        return grupoTerapeuticoService.getById(idGrupoTerapeutico).getUid();
    }

    public List<AtendimentoGrupoDTO> getByStatus(StatusAtividade status){
        List<Atividade> atividades = atividadeRepository.findByStatus(status);

        return atividades.stream()
                .filter(atividade -> atividade instanceof AtendimentoGrupo)
                .map(atividade -> {
                    AtendimentoGrupo atendimentoGrupo = (AtendimentoGrupo) atividade;
                    return AtendimentoGrupoDTO.from(atendimentoGrupo, getUidGrupoTerapeutico(atendimentoGrupo.getIdGrupoTerapeutico()));
                }).toList();
    }

    public void deleteById(UUID uid) {
        Integer id = atividadeRepository.findIds(uid).get(uid);

        if (id == null) throw new EntityNotFoundException("Não há uma entidade com o UID: " + uid);

        atividadeRepository.delete(id);
    }
}
