package br.upe.sap.sistemasapupe.data.repositories.interfaces;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Estagiario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Tecnico;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public interface FuncionarioRepository extends BasicRepository<Funcionario, UUID> {

    /**
     * Cria um estagiário e estabelece uma relação de supervisionado com um supervisor
     * existente
     * @param estagiario estagiario que será registrado
     * @return estagiário criado
     */
    Estagiario createEstagiario(Estagiario estagiario);

    Tecnico createTecnico(Tecnico tecnico);

    List<Estagiario> findSupervisionados(UUID uidTecnico);

    List<Funcionario> findFuncionariosAtivos();

    List<Tecnico> findTecnicos();

    Estagiario updateSupervisao(UUID uidEstagiario, UUID uidSupervisor);

    Funcionario updateAtivo(UUID uidFuncionario, boolean isAtivo);

}
