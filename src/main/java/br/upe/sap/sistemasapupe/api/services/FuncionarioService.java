package br.upe.sap.sistemasapupe.api.services;

import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.api.dtos.funcionarios.UpdateFuncionarioDTO;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
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

    @Transactional
    public FuncionarioDTO changeSupervisor(UUID uidEstagiario, UUID uidSupervisor) {
        BidiMap<UUID, Integer> ids = funcionarioRepository.findIds(
            List.of(uidEstagiario, uidSupervisor));

        return FuncionarioDTO.from(funcionarioRepository.updateSupervisao(
            ids.get(uidEstagiario), ids.get(uidSupervisor)).getSupervisor());
    }

    @Transactional
    public FuncionarioDTO updateCredentials(UpdateFuncionarioDTO dto) {
        UUID uuid = dto.id();
        Integer id = funcionarioRepository.findIds(uuid).get(uuid);

        Funcionario funcionario = funcionarioRepository.findById(id);

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

    public List<FuncionarioDTO> getSupervisionados(UUID uidTecnico) {
        Integer id = funcionarioRepository.findIds(uidTecnico).get(uidTecnico);

        return mapToFuncionarioDTO(funcionarioRepository.findSupervisionados(id));
    }

    public List<FuncionarioDTO> getByAtivo(boolean ativo) {
        return mapToFuncionarioDTO(funcionarioRepository.findByAtivo(ativo));
    }

    public FuncionarioDTO getByUid(UUID uid) {
        Integer id = funcionarioRepository.findIds(uid).get(uid);

        return FuncionarioDTO.from(funcionarioRepository.findById(id));
    }

    public List<FuncionarioDTO> getByUids(List<UUID> uids) {
        List<Integer> ids = funcionarioRepository.findIds(uids).values().stream().toList();

        return mapToFuncionarioDTO(funcionarioRepository.findById(ids));
    }

    public List<FuncionarioDTO> getAllTecnicos() {
        return mapToFuncionarioDTO(funcionarioRepository.findTecnicos());
    }


}
