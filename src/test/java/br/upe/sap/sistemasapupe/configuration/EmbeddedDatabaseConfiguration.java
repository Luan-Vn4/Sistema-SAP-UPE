package br.upe.sap.sistemasapupe.configuration;

import io.zonky.test.db.postgres.embedded.FlywayPreparer;
import io.zonky.test.db.postgres.embedded.PreparedDbProvider;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.context.annotation.Configuration;

import javax.sql.DataSource;
import java.sql.SQLException;


@Configuration
@ComponentScan(basePackages = {"br.upe.sap.sistemasapupe.data"})
public class EmbeddedDatabaseConfiguration {

    @Bean
    public DataSource dataSource() throws SQLException {
        return PreparedDbProvider
                .forPreparer(FlywayPreparer.forClasspathLocation("migrations"))
                .createDataSource();
    }

}
