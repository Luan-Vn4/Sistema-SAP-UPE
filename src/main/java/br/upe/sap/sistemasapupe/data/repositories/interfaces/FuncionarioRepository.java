package br.upe.sap.sistemasapupe.data.repositories.interfaces;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Estagiario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Tecnico;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Cargo;
import jakarta.annotation.Nullable;

import java.util.List;
import java.util.UUID;

public interface FuncionarioRepository extends BasicRepository<Funcionario, UUID> {

    /**
     * Cria um {@link Estagiario} e estabelece uma relação de supervisão com um {@link Tecnico}
     * existente. O supervisor do estagiário passado já deve estar salvo para que aquele seja criado.<br><br>
     * <b>Obs.:</b> apenas será usado o <b>id</b> do supervisor, portanto, não espere que o supervisor retorne
     * com os dados preenchidos. Para isso, procure pelo técnico propriamente
     * @param estagiario estagiario que será registrado (com supervisor já criado)
     * @return {@link Estagiario} criado com suas chaves preenchidas, além de seu supervisor
     * @throws IllegalArgumentException se o estiagiário não tiver um supervisor definido ou se o supervisor
     *                                  do estigiário tiver id nulo
     * @throws java.sql.SQLException caso ocorra algum problema na inserção, por exemplo, se for fornecido um
     *                               supervisor com id inválido ou que não pertença a um supervisor cadastrado
     *                               no sistema, ou, ainda os dados violem alguma constraint
     */
    Estagiario createEstagiario(Estagiario estagiario);

    /**
     * Cria um técnico com base nos dados fornecidos
     * @param tecnico {@link Tecnico} que deseja salvar
     * @return {@link Tecnico} com as suas chaves preenchidas
     * @throws java.sql.SQLException caso ocorra algum problema na inserção, por exemplo, se forem passados
     *                               dados que violem alguma constraint
     */
    Tecnico createTecnico(Tecnico tecnico);

    /**
     * Cria um funcionário conforme o cargo identificado, definidos em {@link Cargo}. Assim, fica responsável
     * por chamar a operação mais adequada para criar aquele cargo. Note que este método apenas centraliza as
     * chamadas para outros métodos de criação. Caso queira saber informações específicas para a criação de
     * algum cargo específico, verifique o método de criação específico daquele cargo.
     * @param funcionario {@link Funcionario} que deseja salvar
     * @return {@link Funcionario} com os dados e tipo da instância definida conforme o cargo identificado
     */
    @Override
    Funcionario create(Funcionario funcionario);

    @Nullable
    Funcionario findByIdInteger(Integer id);

    List<Estagiario> findSupervisionados(UUID uidTecnico);

    List<Funcionario> findFuncionariosAtivos();

    List<Tecnico> findTecnicos();

    Funcionario findByEmail(String email);

    Estagiario updateSupervisao(UUID uidEstagiario, UUID uidSupervisor);

    boolean updateAtivo(UUID uidFuncionario, boolean isAtivo);

}
