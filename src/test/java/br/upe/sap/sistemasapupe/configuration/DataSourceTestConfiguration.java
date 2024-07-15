package br.upe.sap.sistemasapupe.configuration;

import io.zonky.test.db.postgres.embedded.FlywayPreparer;
import io.zonky.test.db.postgres.embedded.PreparedDbProvider;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.RowMapper;
import org.jdbi.v3.core.spi.JdbiPlugin;
import org.jdbi.v3.core.statement.Slf4JSqlLogger;
import org.springframework.boot.test.context.TestConfiguration;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.jdbc.datasource.TransactionAwareDataSourceProxy;

import javax.sql.DataSource;
import java.sql.SQLException;
import java.util.List;

@TestConfiguration
@ComponentScan(basePackages = {"br.upe.sap.sistemasapupe.data"})
public class DataSourceTestConfiguration {

    @Bean
    public DataSource dataSource() throws SQLException {
        return PreparedDbProvider
                .forPreparer(FlywayPreparer.forClasspathLocation("migrations"))
                .createDataSource();
    }

    @Bean
    public Jdbi jdbi(DataSource dataSource, List<JdbiPlugin> plugins, List<RowMapper<?>> rowMappers) {
        var proxy = new TransactionAwareDataSourceProxy(dataSource);
        Jdbi jdbi = Jdbi.create(proxy);
        jdbi.setSqlLogger(new Slf4JSqlLogger());

        plugins.forEach(jdbi::installPlugin);
        rowMappers.forEach(jdbi::registerRowMapper);

        return jdbi;
    }

}
