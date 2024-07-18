package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.configuration.DataSourceTestConfiguration;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Estagiario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Tecnico;
import org.apache.commons.collections4.BidiMap;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;
import java.util.ArrayList;
import java.util.List;
import java.util.UUID;
import java.util.stream.Collectors;

@JdbcTest
@ContextConfiguration(classes = {DataSourceTestConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class JdbiFuncionariosRepositoryTest {

    @Autowired
    Jdbi jdbi;

    @Autowired
    JdbiFuncionariosRepository repository;

    @AfterEach
    public void truncateTables() {
        jdbi.withHandle(handle -> handle.execute("TRUNCATE TABLE funcionarios, supervisoes CASCADE"));
    }

    private List<Tecnico> getTecnicos() {
        Tecnico tecnico1 = Tecnico.tecnicoBuilder()
                .nome("Carlinhos").sobrenome("Carlos")
                .email("carlos@gmail.com").senha("123456")
                .isAtivo(true).urlImagem("www.com").build();
        Tecnico tecnico2 = Tecnico.tecnicoBuilder()
                .nome("Jiró").sobrenome("Brabo")
                .email("Jaca@gmail.com").senha("1210")
                .isAtivo(true).urlImagem("www.com").build();

        return List.of(tecnico1, tecnico2);
    }

    private List<Estagiario> getEstagiarios() {
        Estagiario estagiario1 = Estagiario.estagiarioBuilder()
                .nome("Luan").sobrenome("Vilaça")
                .email("luan@gmail.com").senha("1234")
                .isAtivo(true).urlImagem("www.com").build();
        Estagiario estagiario2 = Estagiario.estagiarioBuilder()
                .nome("Maria").sobrenome("Joaquina")
                .email("maria@gmail.com").senha("1234")
                .isAtivo(true).urlImagem("www.com").build();

        return List.of(estagiario1, estagiario2);
    }


    // CREATE
    @Test()
    @DisplayName("Dado um técnico, quando criado, retornar técnico com as chaves auto-geradas retornadas")
    public void givenTecnico_whenCreate_thenReturnTecnicoWithAutoGeneratedKeys() {
        Tecnico tecnico = getTecnicos().get(0);

        Tecnico result = (Tecnico) repository.create(tecnico);

        Assertions.assertNotNull(result, "Retorno nulo");
        Assertions.assertTrue(result.isAtivo());
        assertIdsAreNotNull(result);
        assertEqualsWithoutIds(result, tecnico);
    }

    private static void assertEqualsWithoutIds(Funcionario f1, Funcionario f2) {
        if (f1 == f2) return;

        boolean comparison = f1.getCargo().equals(f2.getCargo()) &&
                             f1.getNome().equals(f2.getNome()) &&
                             f1.getEmail().equals(f2.getEmail()) &&
                             f1.getSenha().trim().equals(f2.getSenha().trim()) &&
                             f1.getUrlImagem().equals(f2.getUrlImagem()) &&
                             f1.isAtivo() == f2.isAtivo();

        Assertions.assertTrue(comparison, "O funcionário retornado tem atributos diferentes");
    }

    private static void assertIdsAreNotNull(Funcionario funcionario) {
        Assertions.assertNotNull(funcionario.getId(), "ID nulo");
        Assertions.assertNotNull(funcionario.getId(), "UID nulo");
    }

    @Test
    @DisplayName("Dado um estagiário, quando criado, retornar funcionário com as chaves auto-geradas retornadas")
    public void givenEstagiario_whenCreate_thenReturnEstagiarioWithAutoGeneratedKeysAndSupervisor() {
        Estagiario estagiario = getEstagiarios().get(0);
        Tecnico supervisor = (Tecnico) repository.create(getTecnicos().get(0));

        estagiario.setSupervisor(supervisor);

        Estagiario result = (Estagiario) repository.create(estagiario);

        Assertions.assertNotNull(result, "Retorno nulo");
        Assertions.assertNotNull(result.getSupervisor(), "Supervisor nulo");
        assertIdsAreNotNull(result.getSupervisor());
        assertEqualsWithoutIds(result.getSupervisor(), estagiario.getSupervisor());
        assertIdsAreNotNull(result);
        assertEqualsWithoutIds(estagiario, result);
    }

    @Test
    @DisplayName("Dados vários estagiários, quando criados, retornar funcionários com as chaves auto-geradas" +
                 "retornadas")
    public void givenEstagiarios_whenCreate_thenReturnEstagiariosWithAutoGeneratedKeys() {
        List<Estagiario> estagiarios = getEstagiarios();
        Tecnico createdTecnico = (Tecnico) repository.create(getTecnicos().get(0));
        Tecnico tecnico = getTecnicos().get(1);

        estagiarios.get(0).setSupervisor(createdTecnico);
        estagiarios.get(1).setSupervisor(createdTecnico);

        List<Funcionario> funcionarios = estagiarios.stream()
            .map(x -> (Funcionario) x)
            .collect(Collectors.toCollection(ArrayList::new));
        funcionarios.add(tecnico);

        List<Funcionario> results = repository.create(funcionarios);

        Assertions.assertFalse(results.isEmpty(), "Nenhum retorno");
        Assertions.assertEquals(results.size(), funcionarios.size(), "Resultado de tamanho diferente" +
                                                                     "dos registros criados");
        for (int i = 0; i < results.size(); i++) {
            assertIdsAreNotNull(results.get(i));
            assertEqualsWithoutIds(results.get(i), funcionarios.get(i));
        }
    }


    // UPDATE
    @Test
    @DisplayName("Dado um funcionário com dados atualizados, quando atualizar, retornar funcionário com novos dados")
    public void givenUpdatedFuncionario_whenUpdate_thenReturnFuncionarioWithUpdatedData() {
        Funcionario funcionario = repository.create(getTecnicos().get(0));

        funcionario.setNome("kaka");
        funcionario.setSobrenome("tatu");
        funcionario.setEmail("avatar2@gmail.com");
        funcionario.setSenha("567");
        funcionario.setUrlImagem("www.google.com");
        funcionario.setAtivo(false);

        Funcionario updated = repository.update(funcionario);
        updated.setSenha(updated.getSenha().trim());

        Assertions.assertNotNull(updated);
        assertIdsAreNotNull(updated);
        Assertions.assertEquals(funcionario, updated);
    }

    @Test
    @DisplayName("Dado um id de estagiário e técnico, quando atualizar supervisor, retornar estagiário com" +
                 "nome supervisor")
    public void givenIdEstagiarioAndIdTecnico_whenUpdateSupervisao_thenReturnEstagiarioWithNewTecnico() {
        Estagiario estagiario = getEstagiarios().get(0);
        Tecnico supervisor = (Tecnico) repository.create(getTecnicos().get(0));
        Tecnico supervisor2 = (Tecnico) repository.create(getTecnicos().get(1));

        estagiario.setSupervisor(supervisor);
        estagiario = (Estagiario) repository.create(estagiario);

        Estagiario result = repository.updateSupervisao(estagiario.getId(), supervisor2.getId());

        Assertions.assertNotNull(result.getSupervisor(), "Supervisor nulo");
        Assertions.assertEquals(supervisor2.getUid(), result.getSupervisor().getUid(),
                "UID do novo supervisor diferente do esperado");
        Assertions.assertEquals(supervisor2.getId(), result.getSupervisor().getId(),
                "Id do novo supervisor diferente do esperado");
    }

    @Test
    @DisplayName("Dado o id de um funcionario e seu status, ao atualizar atividade, retornar booleano correspondente")
    public void givenIdFuncionarioAndAtivo_whenUpdateAtivo_thenReturnBoolean() {
        Tecnico tecnico = repository.createTecnico(getTecnicos().get(0));

        boolean status = repository.updateAtivo(tecnico.getId(), false);

        Assertions.assertFalse(status);
    }

    // READ
    @Test
    @DisplayName("Dado um id, quando procurar por id, retornar um funcionário com os dados e cargo corretos")
    public void givenId_whenFinById_thenReturnFuncionarioWithRightDataAndRole() {
        Estagiario estagiario = getEstagiarios().get(0);
        Tecnico tecnico = (Tecnico) repository.create(getTecnicos().get(0));
        estagiario.setSupervisor(tecnico);

        estagiario = (Estagiario) repository.create(estagiario);

        Estagiario estagiario2 = (Estagiario) repository.findById(estagiario.getId());
        Tecnico tecnico2 = (Tecnico) repository.findById(tecnico.getId());

        Assertions.assertNotNull(estagiario2, "Retorno nulo para estagiário");
        Assertions.assertNotNull(tecnico2, "Retorno nulo para técnico");
        assertEqualsWithoutIds(estagiario, estagiario2);
        assertEqualsWithoutIds(tecnico, tecnico2);
    }

    @Test
    @DisplayName("Dados ids, quando procurar por ids, retornar funcionários com dados e cargos corretos")
    public void givenIds_whenFindById_thenReturnFuncionariosWithRightDataAndRoles() {
        List<Funcionario> funcionarios = repository.create(
            getTecnicos().stream()
            .map(x -> (Funcionario) x)
            .toList());

        List<Integer> idsList = funcionarios.stream().map(Funcionario::getId).toList();
        List<Funcionario> results = repository.findById(idsList);

        Assertions.assertEquals(funcionarios.size(), results.size());
        for (int i = 0; i < results.size(); i++) {
            assertIdsAreNotNull(results.get(i));
            assertEqualsWithoutIds(funcionarios.get(i), results.get(i));
        }

    }

    @Test
    @DisplayName("Dado um valor booleano, quando procurar por ativo, retornar funcionários ativos ou inativos")
    public void givenBoolean_whenFindByAtivo_thenReturnAtivoAsTrueOrInativoAsFalse() {
        List<Estagiario> estagiarios = getEstagiarios();
        List<Tecnico> tecnicos = getTecnicos();

        List<Funcionario> result1 = repository.create(tecnicos.stream().map(x -> (Funcionario) x).toList());
        estagiarios.forEach(x -> x.setSupervisor((Tecnico) result1.get(0)));
        List<Funcionario> result2 = repository.create(estagiarios.stream().map(x -> (Funcionario) x).toList());

        repository.updateAtivo(result1.get(1).getId(), false);
        repository.updateAtivo(result2.get(1).getId(), false);

        List<Funcionario> expectedAtivos = List.of(result1.get(0), result2.get(0));
        List<Funcionario> expectedInativos = List.of(result1.get(1), result2.get(1));
        List<Funcionario> ativos = repository.findByAtivo(true);
        List<Funcionario> inativos = repository.findByAtivo(false);

        expectedInativos.forEach(x -> x.setAtivo(false));
        Assertions.assertEquals(expectedAtivos.size(), ativos.size());
        Assertions.assertEquals(expectedInativos.size(), inativos.size());

        for (int i = 0; i < expectedAtivos.size(); i++) {
            assertIdsAreNotNull(ativos.get(i));
            assertIdsAreNotNull(inativos.get(i));
            assertEqualsWithoutIds(expectedAtivos.get(i), ativos.get(i));
            assertEqualsWithoutIds(expectedInativos.get(i), inativos.get(i));
        }

    }

    @Test
    @DisplayName("Dado ids, quando procurar por todos, retornar todos os funcionários com os dados " +
                 "e cargos corretos")
    public void givenIds_whenFindAll_thenReturnFuncionariosWithRightDataAndRole() {
        List<Estagiario> estagiarios = getEstagiarios();
        Tecnico createdTecnico = (Tecnico) repository.create(getTecnicos().get(0));
        Tecnico tecnico = getTecnicos().get(1);

        estagiarios.forEach(x -> x.setSupervisor(createdTecnico));

        List<Funcionario> funcionarios = estagiarios.stream()
            .map(x -> (Funcionario) x)
            .collect(Collectors.toCollection(ArrayList::new));
        funcionarios.add(tecnico);

        repository.create(funcionarios);

        // Adiciona no início, pois o findAll retorna na ordem de criação
        funcionarios.add(0, createdTecnico);
        List<Funcionario> results = repository.findAll();

        Assertions.assertFalse(results.isEmpty(), "Nenhum retorno");
        Assertions.assertEquals(funcionarios.size(), results.size(), "Resultado de tamanho diferente" +
                                                                     "dos registros criados");
        for (int i = 0; i < results.size(); i++) {
            assertIdsAreNotNull(results.get(i));
            assertEqualsWithoutIds(results.get(i), funcionarios.get(i));
        }
    }

    @Test
    @DisplayName("Dado id, quando procurar se existe, então retorna verdadeiro se existir")
    public void givenId_whenExists_thenReturnTrueIfExistsAndFalseIfNot() {
        Funcionario funcionario = repository.create(getTecnicos().get(0));

        Assertions.assertTrue(repository.exists(funcionario.getId()));
        repository.delete(funcionario.getId());
        Assertions.assertFalse(repository.exists(funcionario.getId()));
    }

    @Test
    @DisplayName("Dado um id de técnico, quando procurar por supervisionados, retorna supervisionados")
    public void givenIdTecnico_whenFindSupervisionados_thenReturnSupervisionados() {
        Tecnico tecnico = getTecnicos().get(0);
        List<Estagiario> supervisionados = getEstagiarios();

        Tecnico createdTecnico = (Tecnico) repository.create(tecnico);

        supervisionados.forEach(x -> x.setSupervisor(createdTecnico));

        List<Estagiario> createdSupervisionados = supervisionados.stream()
            .map(repository::create)
            .map(x -> (Estagiario) x)
            .toList();

        List<Estagiario> results = repository.findSupervisionados(createdTecnico.getId());

        Assertions.assertFalse(results.isEmpty(), "Nenhum retorno");
        Assertions.assertEquals(results.size(), createdSupervisionados.size(), "Resultado de tamanho diferente" +
                                                                     "dos registros criados");
        for (int i = 0; i < results.size(); i++) {
            assertIdsAreNotNull(results.get(i));
            assertEqualsWithoutIds(results.get(i), createdSupervisionados.get(i));
        }
    }

    @Test
    @DisplayName("Dado Id do estagiário, quando procurar por supervisor, retornar supervisor")
    public void givenIdEstagiario_whenFindSupervisor_thenReturnSupervisor() {
        Tecnico supervisor = (Tecnico) repository.create(getTecnicos().get(0));
        Estagiario estagiario = getEstagiarios().get(0);
        estagiario.setSupervisor(supervisor);
        estagiario = (Estagiario) repository.create(estagiario);

        Tecnico result = repository.findSupervisor(estagiario.getId());

        Assertions.assertNotNull(result);
        Assertions.assertEquals(supervisor.getId(), result.getId(), "Ids não iguais");
        Assertions.assertEquals(supervisor.getUid(), result.getUid(), "Uids não iguais");
        assertEqualsWithoutIds(result, supervisor);
    }

    @Test
    @DisplayName("quando procurar por técnicas, então retornar uma lista de técnicos")
    public void whenFindTecnicos_thenReturnTecnicos() {
        List<Tecnico> tecnicos = getTecnicos();
        Estagiario estagiario = getEstagiarios().get(0);

        List<Funcionario> createdTecnicos = repository.create(tecnicos.stream().map(x -> (Funcionario) x).toList());
        estagiario.setSupervisor((Tecnico) createdTecnicos.get(0));

        Assertions.assertNotNull(repository.create(estagiario), "Estagiário não foi criado");

        List<Tecnico> foundTecnicos = repository.findTecnicos();

        Assertions.assertFalse(foundTecnicos.isEmpty());
        Assertions.assertEquals(tecnicos.size(), foundTecnicos.size());
        for (int i = 0; i < foundTecnicos.size(); i++) {
            assertIdsAreNotNull(foundTecnicos.get(i));
            assertEqualsWithoutIds(tecnicos.get(i), foundTecnicos.get(i));
        }
    }

    @Test
    @DisplayName("Dado um email, quando procurar por email, retornar funcionário")
    public void givenEmail_whenFindByEmail_thenReturnFuncionario() {
        Tecnico tecnico = (Tecnico) repository.create(getTecnicos().get(0));

        Estagiario estagiario = getEstagiarios().get(0);
        estagiario.setSupervisor(tecnico);
        estagiario = (Estagiario) repository.create(estagiario);

        Estagiario resultEstagiario = (Estagiario) repository.findByEmail(estagiario.getEmail());
        Tecnico resultTecnico = (Tecnico) repository.findByEmail(tecnico.getEmail());

        Assertions.assertNotNull(resultTecnico, "Técnico nulo");
        Assertions.assertNotNull(resultEstagiario, "Estagiário nulo");
        assertIdsAreNotNull(resultTecnico);
        assertIdsAreNotNull(resultEstagiario);
        Assertions.assertEquals(tecnico, resultTecnico, "O técnico não é igual ao funcionário esperado");
        Assertions.assertEquals(estagiario, resultEstagiario, "O estagiário não é igual ao funcionário esperado");
    }

    @Test
    @DisplayName("Dados uuids, quando procurar por ids, então retornar ids")
    public void givenUUIDs_whenFindIds_thenReturnIds() {
        List<Funcionario> funcionarios = repository.create(getTecnicos()
            .stream()
            .map(x -> (Funcionario) x)
            .toList());

        BidiMap<UUID, Integer> ids = repository.findIds(
                funcionarios.stream().map(Funcionario::getUid).toList());

        for (Funcionario funcionario : funcionarios) {
            Assertions.assertTrue(ids.containsKey(funcionario.getUid()));
            Assertions.assertEquals(ids.get(funcionario.getUid()), funcionario.getId());
        }
    }

    @Test
    @DisplayName("Dado uuid, quando procurar por ids, então retornar ids")
    public void givenUUID_whenFindIds_thenReturnIds() {
        Funcionario funcionario = repository.create(getTecnicos().get(0));

        BidiMap<UUID, Integer> ids = repository.findIds(funcionario.getUid());

        Assertions.assertTrue(ids.containsKey(funcionario.getUid()));
        Assertions.assertEquals(ids.get(funcionario.getUid()), funcionario.getId());
    }

    // DELETE
    @Test
    @DisplayName("Dado o id de um funcionário, ao deletar, retornar 1")
    public void givenIdFuncionario_whenDelete_thenReturn1() {
        Funcionario funcionario = repository.create(getTecnicos().get(0));

        Integer deleted = repository.delete(funcionario.getId());
        Funcionario findResult = repository.findById(funcionario.getId());

        Assertions.assertEquals(1, deleted, "Nenhum registro foi afetado");
        Assertions.assertNull(findResult, "A busca retornou o suposto funcionário deletado");
    }

    @Test
    @DisplayName("Dados ids de funcionários, quando deletar, retornar número de funcionários deletados")
    public void GivenIdsFuncionarios_whenDelete_thenReturnNumberOfDeletedRows() {
        List<Funcionario> funcionarios = repository.create(
            getTecnicos().stream()
            .map(x -> (Funcionario) x)
            .toList());

        List<Integer> idsList = funcionarios.stream().map(Funcionario::getId).toList();
        int deleted = repository.delete(idsList);
        List<Funcionario> findResult = repository.findById(idsList);

        Assertions.assertTrue(findResult.isEmpty());
        Assertions.assertEquals(funcionarios.size(), deleted, "Nenhum registro foi afetado");
    }

}
