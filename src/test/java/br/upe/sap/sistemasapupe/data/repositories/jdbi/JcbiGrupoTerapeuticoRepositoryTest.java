package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.configuration.DataSourceTestConfiguration;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Tecnico;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import br.upe.sap.sistemasapupe.data.model.posts.Comentario;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

@JdbcTest
@ContextConfiguration(classes = {DataSourceTestConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class JcbiGrupoTerapeuticoRepositoryTest {

    @Autowired
    Jdbi jdbi;

    @Autowired
    JdbiGrupoTerapeuticoRepository repository;

    @Autowired
    JdbiFuncionariosRepository funcionariosRepository;

    @Autowired
    JdbiFichaRepository fichaRepository;

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

    private List<Ficha> getFichas() {
        Ficha ficha1 = Ficha.fichaBuilder()
                .funcionario(funcionariosRepository.findById(1))
                .nome("Pedin")
                .build();
        Ficha ficha2 = Ficha.fichaBuilder()
                .funcionario(funcionariosRepository.findById(2))
                .nome("Erick")
                .build();

        return List.of(ficha1, ficha2);
    }

    @AfterEach
    public void truncateTables() {
        jdbi.withHandle(handle -> handle.execute("TRUNCATE TABLE grupos_terapeuticos, atendimentos_grupo CASCADE"));
        jdbi.withHandle(handle -> handle.execute("TRUNCATE TABLE funcionarios, supervisoes CASCADE"));
        jdbi.withHandle(handle -> handle.execute("TRUNCATE TABLE fichas, ficha_atendimento_grupo CASCADE"));
    }

    @Test
    @DisplayName("Dado um grupo terapeutico, quando criado, retornar esse grupo terapeutico com as chaves auto-generadas")
    public void givenTherapeuticGroup_whenCreate_thenReturnTherapeuticGroupWithAutoGenerateKeys(){
        Tecnico supervisor1 = (Tecnico) funcionariosRepository.create(getTecnicos().get(0));
        Tecnico supervisor2 = (Tecnico) funcionariosRepository.create(getTecnicos().get(1));
        List <Funcionario> funcionarios = List.of(supervisor1, supervisor2);

        Ficha ficha1 = fichaRepository.create(getFichas().get(0));
        Ficha ficha2 = fichaRepository.create(getFichas().get(0));
        List <Ficha> fichas = List.of(ficha1, ficha2);

        GrupoTerapeutico grupoTerapeutico = GrupoTerapeutico
                .grupoTerapeuticoBuilder()
                .temaTerapia("SAP")
                .coordenadores(funcionarios)
                .fichas(fichas)
                .descricao("muito legal")
                .build();

        GrupoTerapeutico createdGroup = repository.create(grupoTerapeutico);
        assertNotNull(grupoTerapeutico);
        assertIdsAreNotNull(createdGroup);
        assertEqualsWithoutIds(grupoTerapeutico, createdGroup);
    }

    private void assertIdsAreNotNull(GrupoTerapeutico grupoTerapeutico) {
        assertNotNull(grupoTerapeutico.getUid(), "UUID do grupo terapeutico não deve ser nulo");
        assertNotNull(grupoTerapeutico.getId(), "ID do grupo terapeutico não deve ser nulo");
    }

    private void assertEqualsWithoutIds(GrupoTerapeutico expected, GrupoTerapeutico actual) {
        assertEquals(expected.getCoordenadores(), actual.getCoordenadores(), "Coordenadores do grupo não correspondem");
        assertEquals(expected.getFichas(), actual.getFichas(), "Fichas não correspondem");
        assertEquals(expected.getTemaTerapia(), actual.getTemaTerapia(), "Temas terapia não correspondem");
        assertEquals(expected.getDescricao(), actual.getDescricao(), "As descrições não correspondem");
    }

}
