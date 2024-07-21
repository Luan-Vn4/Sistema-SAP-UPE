package br.upe.sap.sistemasapupe.data.repositories.jdbi.atividades;

import br.upe.sap.sistemasapupe.configuration.DataSourceTestConfiguration;
import br.upe.sap.sistemasapupe.data.model.atividades.AtendimentoIndividual;
import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import br.upe.sap.sistemasapupe.data.model.enums.TipoSala;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Estagiario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Tecnico;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FichaRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.sala.SalaRepository;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;

import java.time.LocalDateTime;
import java.util.List;
import java.util.UUID;



@JdbcTest
@ContextConfiguration(classes = {DataSourceTestConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
public class JdbiAtividadeRepositoryFacadeTest {

    @Autowired
    Jdbi jdbi;

    @Autowired
    JdbiAtividadeRepositoryFacade repository;

    @Autowired
    SalaRepository salaRepository;

    @Autowired
    FichaRepository fichaRepository;

    @Autowired
    FuncionarioRepository funcionarioRepository;

    @BeforeEach
    public void truncateTables() {
        jdbi.useHandle(handle -> {
            handle.execute("TRUNCATE TABLE atividades CASCADE");
            handle.execute("TRUNCATE TABLE funcionarios, supervisoes CASCADE");
        });
    }

    private List<Funcionario> getFuncionarios() {
        Tecnico tecnico1 = Tecnico.tecnicoBuilder()
                .nome("Carlinhos").sobrenome("Carlos")
                .email("carlos@gmail.com").senha("123456")
                .isAtivo(true).urlImagem("www.com").build();
        Tecnico tecnico2 = Tecnico.tecnicoBuilder()
                .nome("Jiró").sobrenome("Brabo")
                .email("Jaca@gmail.com").senha("1210")
                .isAtivo(true).urlImagem("www.com").build();

        return funcionarioRepository.create(List.of(tecnico1, tecnico2));
    }

    private Estagiario getEstagiario() {
        Tecnico tecnico = Tecnico.tecnicoBuilder()
                .nome("José").sobrenome("Carlos")
                .email("feijao@gmail.com").senha("123456")
                .isAtivo(true).urlImagem("www.com").build();
        funcionarioRepository.create(tecnico);

        Estagiario estagiario = Estagiario.estagiarioBuilder()
            .nome("Luan").sobrenome("Vilaça")
            .email("luan@gmail.com").senha("1234")
            .supervisor(tecnico)
            .isAtivo(true).urlImagem("www.com").build();

        return funcionarioRepository.createEstagiario(estagiario);
    }

    private List<Sala> getSalas() {
        Sala sala1 = Sala.salaBuilder()
            .id(1).uid(UUID.randomUUID())
            .nome("SALA 1").tipoSala(TipoSala.INDIVIDUAL).build();

        Sala sala2 = Sala.salaBuilder()
            .id(2).uid(UUID.randomUUID())
            .nome("SALA 2").tipoSala(TipoSala.GRUPO).build();

        return salaRepository.create(List.of(sala1, sala2));
    }

    @Test
    public void givenAtendimentoIndividual_whenCreate_thenReturnAtendimentoIndividual() {
        Funcionario funcionario = getFuncionarios().get(0);
        Sala sala = getSalas().get(0);

        var atividade = AtendimentoIndividual.builder()
            .statusAtividade(StatusAtividade.PENDENTE)
            .funcionario(funcionario)
            .tempoInicio(LocalDateTime.of(2005,1,1,12,30))
            .tempoFim(LocalDateTime.of(2006,1,1,12,30))
            .sala(sala);
            //.ficha();

    }

}
