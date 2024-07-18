package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.configuration.EmbeddedDatabaseConfiguration;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterEach;
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

public class JdbiFichaRepositoryTest {
    @Autowired
    Jdbi jdbi;

    @Autowired
    JdbiFichaRepository repository;
    JdbiFuncionariosRepository funcionariosRepository;


    @AfterEach
    public void truncateTables() {
        jdbi.withHandle(handle -> handle.execute("TRUNCATE TABLE ficha, funcionarios, grupos_terapeuticos CASCADE"));
    }

   /* @Test
    public void CreateTest (){
        Ficha ficha = new Ficha()
    }*/
}
