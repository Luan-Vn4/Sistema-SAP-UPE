package br.upe.sap.sistemasapupe.api.services;

import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.api.dtos.funcionarios.UpdateFuncionarioDTO;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Estagiario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Tecnico;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import jakarta.persistence.EntityNotFoundException;
import lombok.AllArgsConstructor;
import org.apache.commons.collections4.BidiMap;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@Service
@AllArgsConstructor
public class FuncionarioService {

    FuncionarioRepository funcionarioRepository;

    /**
     * Muda o supervisor (técnico) responsável pelo estagiário especificado
     * @param uidEstagiario {@link UUID} do estagiário cujo supervisor será trocado
     * @param uidSupervisor {@link UUID} do novo supervisor (técnico)
     * @return {@link FuncionarioDTO} com os dados do estagiário
     * @throws IllegalArgumentException caso o uuid de estagiário ou supervisor fornecidos não corresponda(m)
     *                                  ao(s) cargo(s) esperado(s) ou não pelo menos um deles não exista
     */
    @Transactional
    public FuncionarioDTO changeSupervisor(UUID uidEstagiario, UUID uidSupervisor) {
        Funcionario estagiario = getFuncionarioByUid(uidEstagiario);
        Funcionario supervisor = getFuncionarioByUid(uidSupervisor);

        if (!(estagiario instanceof Estagiario) || !(supervisor instanceof Tecnico)) {
            throw new IllegalArgumentException("Algum dos uids fornecidos não correspondem aos cargos " +
                    "esperados para estagiário ou supervisor. Apenas o estagiário possui supervisor e " +
                    "supervisores devem ser técnicos");
        }

        return FuncionarioDTO.from(funcionarioRepository.updateSupervisao(
            estagiario.getId(), supervisor.getId()));
    }

    /**
     * Atualiza as credenciais, dados, daquele funcionário fornecido. Sua identificação é feita através do
     * {@link UUID} presente em {@link FuncionarioDTO}
     * @param dto @{@link FuncionarioDTO} com os novos dados, ainda não persistidos
     * @return {@link FuncionarioDTO} com os dados persistidos
     * @throws EntityNotFoundException caso não seja encontrado um funcionário com uuid correspondente
     */
    @Transactional
    public FuncionarioDTO updateCredentials(UpdateFuncionarioDTO dto) {
        Funcionario funcionario = getFuncionarioByUid(dto.id());

        if (funcionario == null) throw new EntityNotFoundException("Não foi possível encontrar um funcionario " +
                "com o UID: " + dto.id());

        funcionario.setNome(valueOrElse(dto.nome(), funcionario.getNome()));
        funcionario.setEmail(valueOrElse(dto.email(), funcionario.getEmail()));
        funcionario.setAtivo(valueOrElse(dto.isAtivo(), funcionario.isAtivo()));

        return FuncionarioDTO.from(funcionarioRepository.update(funcionario));
    }

    private <T> T valueOrElse(T value, T alternative) {
        return (value != null ? value : alternative);
    }

    @Transactional
    public FuncionarioDTO updateActivation(UUID uidFuncionario, boolean ativo) {
        Integer id = funcionarioRepository.findIds(uidFuncionario).get(uidFuncionario);

        funcionarioRepository.updateAtivo(id, ativo);
        return FuncionarioDTO.from(funcionarioRepository.findById(id));
    }

    public List<FuncionarioDTO> getAll() {
        return mapToFuncionarioDTO(funcionarioRepository.findAll());
    }

    private List<FuncionarioDTO> mapToFuncionarioDTO(List<? extends Funcionario> funcionarios) {
        return funcionarios.stream().map(FuncionarioDTO::from).toList();
    }

    /**
     * Procura pelos supervisionados de um técnico com base em seu {@link UUID}
     * @param uidTecnico {@link UUID} do técnico cujos supervisionados serão buscados
     * @return {@link List} com as informações de todos os seus supervisionados
     * @throws IllegalArgumentException caso o {@link UUID} fornecido não corresponda a um técnico
     * @throws EntityNotFoundException caso não seja achado algum funcionário com tal {@link UUID}
     */
    public List<FuncionarioDTO> getSupervisionados(UUID uidTecnico) {
        Integer id = funcionarioRepository.findIds(uidTecnico).get(uidTecnico);

        try {
            return mapToFuncionarioDTO(funcionarioRepository.findSupervisionados(id));
        } catch (IllegalArgumentException e) {
            throw new IllegalArgumentException("O funcionário fornecido não é um técnico. UID: " + uidTecnico);
        } catch (EntityNotFoundException e) {
            throw new EntityNotFoundException("Não foi possível achar um funcionário com o UID: " + uidTecnico);
        }
    }

    public Funcionario getFuncionarioByUid(UUID uid) {
        Integer id = funcionarioRepository.findIds(uid).get(uid);

        if (id == null) return null;

        return funcionarioRepository.findById(id);
    }

    public List<Funcionario> getFuncionarioByUid(List<UUID> uids) {
        BidiMap<UUID, Integer> ids = funcionarioRepository.findIds(uids);

        if (ids.isEmpty()) return List.of();

        return funcionarioRepository.findById(ids.values().stream().toList());
    }

    public List<FuncionarioDTO> getByAtivo(boolean ativo) {
        return mapToFuncionarioDTO(funcionarioRepository.findByAtivo(ativo));
    }

    public FuncionarioDTO getByUid(UUID uid) {
        return FuncionarioDTO.from(getFuncionarioByUid(uid));
    }

    public List<FuncionarioDTO> getByUids(List<UUID> uids) {
        return mapToFuncionarioDTO(getFuncionarioByUid(uids));
    }

    public List<FuncionarioDTO> getAllTecnicos() {
        return mapToFuncionarioDTO(funcionarioRepository.findTecnicos());
    }

}
