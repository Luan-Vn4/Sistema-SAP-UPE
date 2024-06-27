package br.upe.sap.sistemasapupe.data.repositories.interfaces;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import java.util.List;
import java.util.UUID;

public interface FuncionarioRepository extends Repository<Funcionario, UUID> {

    List<Funcionario> findSupervisionados(UUID uidTecnico);

    List<Funcionario> findFuncionariosAtivos();

    List<Funcionario> findTecnicos();

    Funcionario updateSupervisionado(Funcionario estagiario, Funcionario novoSupervisor);

    Funcionario updateAtivo(UUID uidFuncionario, boolean isAtivo);

}
