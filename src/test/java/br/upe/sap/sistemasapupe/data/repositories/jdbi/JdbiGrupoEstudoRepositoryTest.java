package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.configuration.DataSourceTestConfiguration;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Estagiario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Tecnico;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoEstudo;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@ContextConfiguration(classes = {DataSourceTestConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("Test")
public class JdbiGrupoEstudoRepositoryTest {
    @Autowired
    Jdbi jdbi;
    @Autowired
    JdbiFuncionariosRepository funcionariosRepository;
    @Autowired
    JdbiGrupoEstudoRepository grupoEstudoRepository;

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

    @AfterEach
    public void truncateTables() {
        jdbi.withHandle(handle -> handle.execute("TRUNCATE TABLE grupos_estudo CASCADE"));
        jdbi.withHandle(handle -> handle.execute("TRUNCATE TABLE funcionarios, supervisoes CASCADE"));
    }

    @Test
    @DisplayName("Dado um grupo de estudo, quando criar, então retorne grupo de estudo com as chaves auto-geradas")
    public void givenGrupoEstudo_whenCreate_thenReturnGrupoEstudoWithGeneratedKeys() {
        Estagiario estagiario = getEstagiarios().get(0);
        Tecnico supervisor = (Tecnico) funcionariosRepository.create(getTecnicos().get(0));
        estagiario.setSupervisor(supervisor);
        estagiario = funcionariosRepository.createEstagiario(estagiario);

        GrupoEstudo grupoEstudo = GrupoEstudo.grupoEstudoBuilder()
            .tema("AAAA")
            .dono(supervisor.getId())
            .descricao("cai dentro")
            .build();

        GrupoEstudo createdGrupoEstudo = grupoEstudoRepository.create(grupoEstudo);

        assertNotNull(createdGrupoEstudo);
        assertIdsAreNotNull(createdGrupoEstudo);
        assertEqualsWithoutIds(grupoEstudo, createdGrupoEstudo);
    }
    private void assertIdsAreNotNull(GrupoEstudo grupoEstudo) {
        assertNotNull(grupoEstudo.getId(), "ID não deve ser nulo");
        assertNotNull(grupoEstudo.getUid(), "UID não deve ser nulo");
    }

    private void assertEqualsWithoutIds(GrupoEstudo expected, GrupoEstudo actual) {
        assertEquals(expected.getTema(), actual.getTema(), "Temas de estudo devem ser iguais");
        assertEquals(expected.getDono(), actual.getDono(), "Donos devem ser iguais");
        assertEquals(expected.getDescricao(), actual.getDescricao(), "Descrições devem ser iguais");
    }

    @Test
    @DisplayName("Dado um grupo de estudo, quando buscar por seu ID, então retorne grupo de estudos")
    public void givenGrupoEstudo_whenFindById_thenReturnGrupoEstudo() {
        Estagiario estagiario = getEstagiarios().get(0);
        Tecnico supervisor = (Tecnico) funcionariosRepository.create(getTecnicos().get(0));
        estagiario.setSupervisor(supervisor);
        estagiario = funcionariosRepository.createEstagiario(estagiario);

        GrupoEstudo grupoEstudo = GrupoEstudo.grupoEstudoBuilder()
                .tema("AAAA")
                .dono(supervisor.getId())
                .descricao("cai dentro")
                .build();

        grupoEstudo = grupoEstudoRepository.create(grupoEstudo);
        GrupoEstudo foundGrupoEstudo = grupoEstudoRepository.findById(grupoEstudo.getId());

        assertNotNull(foundGrupoEstudo);
        assertIdsAreNotNull(foundGrupoEstudo);
        assertEqualsWithoutIds(grupoEstudo, foundGrupoEstudo);
    }

    @Test
    @DisplayName("Dado grupos de estudo, quando buscar por todos, então retorne todos")
    public void givenGrupoEstudo_whenFindAll_thenReturnGrupoEstudo() {
        Estagiario estagiario = getEstagiarios().get(0);
        Tecnico supervisor = (Tecnico) funcionariosRepository.create(getTecnicos().get(0));
        estagiario.setSupervisor(supervisor);
        estagiario = funcionariosRepository.createEstagiario(estagiario);

        GrupoEstudo grupoEstudo1 = GrupoEstudo.grupoEstudoBuilder()
                .tema("AAAA")
                .dono(supervisor.getId())
                .descricao("cai dentro")
                .build();
        GrupoEstudo grupoEstudo2 = GrupoEstudo.grupoEstudoBuilder()
                .tema("BBB")
                .dono(supervisor.getId())
                .descricao("ai dento")
                .build();

        grupoEstudo1 = grupoEstudoRepository.create(grupoEstudo1);
        grupoEstudo2 = grupoEstudoRepository.create(grupoEstudo2);
        List<GrupoEstudo> foundGrupoEstudo = grupoEstudoRepository.findAll();

        assertNotNull(foundGrupoEstudo);
        assertEquals(2, foundGrupoEstudo.size());
    }

    @Test
    @DisplayName("Dado um grupo de estud0, quando deletado, então retorne o ID desse grupo de estudo")
    public void givenGrupoEstudo_whenDeleted_thenReturnIdGrupoEstudo() {
        Estagiario estagiario = getEstagiarios().get(0);
        Tecnico supervisor = (Tecnico) funcionariosRepository.create(getTecnicos().get(0));
        estagiario.setSupervisor(supervisor);
        estagiario = funcionariosRepository.createEstagiario(estagiario);

        GrupoEstudo grupoEstudo1 = GrupoEstudo.grupoEstudoBuilder()
                .tema("AAAA")
                .dono(supervisor.getId())
                .descricao("cai dentro")
                .build();
        GrupoEstudo grupoEstudo2 = GrupoEstudo.grupoEstudoBuilder()
                .tema("BBB")
                .dono(supervisor.getId())
                .descricao("ai dento")
                .build();

        grupoEstudo1 = grupoEstudoRepository.create(grupoEstudo1);
        grupoEstudo2 = grupoEstudoRepository.create(grupoEstudo2);
        List<Integer> ids = List.of(grupoEstudo1.getId(), grupoEstudo2.getId());
        grupoEstudoRepository.delete(ids);

        assertNull(grupoEstudoRepository.findById(grupoEstudo1.getId()), "O grupo de estudo 1 deve ser nulo após a exclusão");
        assertNull(grupoEstudoRepository.findById(grupoEstudo2.getId()), "O grupo de estudo 2 deve ser nulo após a exclusão");
    }

    @Test
    @DisplayName("Dado um grupo de estudo, quando buscar por funcionario, então retorne o grupo de estudo relacionado")
    public void givenGrupoEstudo_whenFindByFuncionario_thenReturnGrupoEstudo() {
        Estagiario estagiario = getEstagiarios().get(0);
        Tecnico supervisor = (Tecnico) funcionariosRepository.create(getTecnicos().get(0));
        estagiario.setSupervisor(supervisor);
        estagiario = funcionariosRepository.createEstagiario(estagiario);

        GrupoEstudo grupoEstudo1 = GrupoEstudo.grupoEstudoBuilder()
            .tema("AAAA")
            .dono(supervisor.getId())
            .descricao("cai dentro")
            .build();

        grupoEstudo1 = grupoEstudoRepository.create(grupoEstudo1);
        grupoEstudoRepository.addFuncionario(estagiario.getId(), grupoEstudo1.getId());
        List<GrupoEstudo> foundGrupoEstudo = grupoEstudoRepository.findByFuncionario(estagiario.getId());

        assertNotNull(foundGrupoEstudo);
        assertEquals(grupoEstudo1.getId(), foundGrupoEstudo.get(0).getId());
        assertEquals(grupoEstudo1.getTema(), foundGrupoEstudo.get(0).getTema());
        assertEquals(grupoEstudo1.getDescricao(), foundGrupoEstudo.get(0).getDescricao());
        assertEquals(grupoEstudo1.getDono(), foundGrupoEstudo.get(0).getDono());
    }

    @Test
    @DisplayName("Dado um participante, quando excluir, então o registro deve ser removido da tabela de participações")
    public void givenParticipante_whenDeleteParticipacao_thenRemoveParticipacao() {
        Estagiario estagiario = getEstagiarios().get(0);
        Tecnico supervisor = (Tecnico) funcionariosRepository.create(getTecnicos().get(0));
        estagiario.setSupervisor(supervisor);
        estagiario = funcionariosRepository.createEstagiario(estagiario);

        GrupoEstudo grupoEstudo1 = GrupoEstudo.grupoEstudoBuilder()
                .tema("AAAA")
                .dono(supervisor.getId())
                .descricao("cai dentro")
                .build();

        grupoEstudo1 = grupoEstudoRepository.create(grupoEstudo1);
        grupoEstudoRepository.addFuncionario(estagiario.getId(), grupoEstudo1.getId());
        grupoEstudoRepository.deleteParticipacao(estagiario.getId(), grupoEstudo1.getId());

        List<GrupoEstudo> foundGrupoEstudo = grupoEstudoRepository.findByFuncionario(estagiario.getId());
    }

}
