package br.upe.sap.sistemasapupe.configuration;

import io.github.cdimascio.dotenv.Dotenv;
import io.github.cdimascio.dotenv.DotenvEntry;
import org.springframework.boot.context.event.ApplicationPreparedEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Configuration;

@Configuration
public class EnvironmentConfiguration implements ApplicationListener<ApplicationPreparedEvent> {

    @Override
    public void onApplicationEvent(ApplicationPreparedEvent event) {
        setEnvironmentVariables();
    }

    private void setEnvironmentVariables() {
        Dotenv dotenv = Dotenv.configure()
                .directory("src/main/resources")
                .filename(".env")
                .load();

        for (DotenvEntry entry : dotenv.entries(Dotenv.Filter.DECLARED_IN_ENV_FILE)) {
            System.setProperty(entry.getKey(), entry.getValue());
        }
    }

}
