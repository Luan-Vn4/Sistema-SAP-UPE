package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.configuration.EmbeddedDatabaseConfiguration;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Estagiario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Tecnico;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;

@JdbcTest
@ContextConfiguration(classes = {EmbeddedDatabaseConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EntityScan(basePackages = {"br.upe.sap.sistemasapupe.data"})
public class JdbiFuncionariosRepositoryTest {

    @Autowired
    Jdbi jdbi;

    @Autowired
    JdbiFuncionariosRepository repository;

    @AfterEach
    public void truncateTables() {
        jdbi.withHandle(handle -> handle.execute("TRUNCATE TABLE funcionarios, supervisoes CASCADE"));
    }

    @Test()
    @DisplayName("Dado um técnico, quando criado, retornar técnico com as chaves auto-geradas retornadas")
    public void givenTecnico_whenCreate_ThenReturnTecnicoWithAutoGeneratedKeys() {
        Tecnico tecnico = Tecnico.tecnicoBuilder()
                .nome("Carlinhos").sobrenome("Carlos")
                .email("carlos@gmail.com").senha("123456")
                .urlImagem("www.com").build();

        Tecnico result = (Tecnico) repository.create(tecnico);

        Assertions.assertNotNull(result.getId(), "ID nulo");
        Assertions.assertNotNull(result.getId(), "UID nulo");
    }

    @Test()
    @DisplayName("Dado um estagiário, quando criado, retornar funcionário com as chaves auto-geradas retornadas")
    public void givenEstagiario_whenCreate_thenReturnEstagiarioWithAutoGeneratedKeys() {
        Estagiario estagiario = Estagiario.estagiarioBuilder()
                .nome("Luan").sobrenome("Vilaça")
                .email("luan@gmail.com").senha("1234")
                .urlImagem("www.com").build();

        Estagiario result = (Estagiario) repository.create(estagiario);

        Assertions.assertNotNull(result.getId(), "Null id");
        Assertions.assertNotNull(result.getUid(), "Null uid");
    }

}