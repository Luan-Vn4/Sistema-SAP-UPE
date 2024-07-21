package br.upe.sap.sistemasapupe.data.repositories.interfaces;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Estagiario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Tecnico;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Cargo;
import org.apache.commons.collections4.BidiMap;
import jakarta.persistence.EntityNotFoundException;
import java.util.List;
import java.util.UUID;

public interface FuncionarioRepository extends BasicRepository<Funcionario, Integer> {

    // CREATE //
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
     * @throws
     */
    @Override
    Funcionario create(Funcionario funcionario);


    // READ //
    /**
     * Procura pelos supervisionados do técnico fornecido
     * @param idTecnico uid do técnico cujos supervisionados serão buscados
     * @return {@link List} com todos os estagiários supervisionados por aquele técnico
     * @throws IllegalArgumentException caso o id passado não corresponda a um técnico
     * @throws EntityNotFoundException caso não exista um funcionário com o id passado
     */
    List<Estagiario> findSupervisionados(Integer idTecnico);

    List<Funcionario> findByAtivo(boolean ativo);

    List<Tecnico> findTecnicos();

    Funcionario findByEmail(String email);

    BidiMap<UUID, Integer> findIds(UUID uuid);

    BidiMap<UUID, Integer> findIds(List<UUID> uuids);

    boolean exists(Integer id);


    // UPDATE //
    Estagiario updateSupervisao(Integer uidEstagiario, Integer uidSupervisor);

    boolean updateAtivo(Integer uidFuncionario, boolean isAtivo);

    void updatePassword(Integer idFuncionario, String newPassword);

}
