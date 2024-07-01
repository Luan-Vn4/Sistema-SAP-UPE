package br.upe.sap.sistemasapupe.data.repositories.interfaces;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FuncionarioRepository extends BasicRepository<Funcionario, UUID> {

    List<Funcionario> findSupervisionados(UUID uidTecnico);

    List<Funcionario> findFuncionariosAtivos();

    List<Funcionario> findTecnicos();

    Funcionario updateSupervisionado(Funcionario estagiario, Funcionario novoSupervisor);

    Funcionario updateAtivo(UUID uidFuncionario, boolean isAtivo);

}
