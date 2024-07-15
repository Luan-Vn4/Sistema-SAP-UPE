package br.upe.sap.sistemasapupe.api.services;

import br.upe.sap.sistemasapupe.api.dtos.FuncionarioDTO;
import br.upe.sap.sistemasapupe.api.dtos.UpdateFuncionarioDTO;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import java.util.List;
import java.util.UUID;

@AllArgsConstructor
@Service
public class FuncionarioService {

    FuncionarioRepository funcionarioRepository;

    @Transactional
    public FuncionarioDTO changeSupervisor(UUID uidEstagiario, UUID uidSupervisor) {
        return FuncionarioDTO.from(funcionarioRepository.updateSupervisao(uidEstagiario, uidSupervisor).getSupervisor());
    }

    @Transactional
    public FuncionarioDTO updateCredentials(UpdateFuncionarioDTO dto) {
        Funcionario funcionario = funcionarioRepository.findById(dto.uid());

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
        funcionarioRepository.updateAtivo(uidFuncionario, ativo);
        return FuncionarioDTO.from(funcionarioRepository.findById(uidFuncionario));
    }

    public List<FuncionarioDTO> getAll() {
        return mapToFuncionarioDTO(funcionarioRepository.findAll());
    }

    private List<FuncionarioDTO> mapToFuncionarioDTO(List<? extends Funcionario> funcionarios) {
        return funcionarios.stream().map(FuncionarioDTO::from).toList();
    }

    public List<FuncionarioDTO> getSupervisionados(UUID uidTecnico) {
        return mapToFuncionarioDTO(funcionarioRepository.findSupervisionados(uidTecnico));
    }

    public List<FuncionarioDTO> getAllAtivos() {
        return mapToFuncionarioDTO(funcionarioRepository.findFuncionariosAtivos());
    }

    public FuncionarioDTO getByUid(UUID uid) {
        return FuncionarioDTO.from(funcionarioRepository.findById(uid));
    }

    public List<FuncionarioDTO> getByUids(List<UUID> uids) {
        return mapToFuncionarioDTO(funcionarioRepository.findById(uids));
    }

    public List<FuncionarioDTO> getAllTecnicos() {
        return mapToFuncionarioDTO(funcionarioRepository.findTecnicos());
    }


}
