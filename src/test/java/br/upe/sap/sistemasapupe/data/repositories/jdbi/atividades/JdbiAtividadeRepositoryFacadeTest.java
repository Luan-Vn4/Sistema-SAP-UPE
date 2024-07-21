package br.upe.sap.sistemasapupe.data.repositories.jdbi.atividades;

import br.upe.sap.sistemasapupe.configuration.DataSourceTestConfiguration;
import br.upe.sap.sistemasapupe.data.model.atividades.AtendimentoIndividual;
import br.upe.sap.sistemasapupe.data.model.atividades.Atividade;
import br.upe.sap.sistemasapupe.data.model.atividades.Sala;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import br.upe.sap.sistemasapupe.data.model.enums.TipoSala;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Estagiario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Tecnico;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FichaRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.sala.SalaRepository;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import java.time.LocalDateTime;
import java.util.List;

@JdbcTest
@ContextConfiguration(classes = {DataSourceTestConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@ActiveProfiles("Test")
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
            .nome("SALA 1").tipoSala(TipoSala.INDIVIDUAL).build();

        Sala sala2 = Sala.salaBuilder()
            .nome("SALA 2").tipoSala(TipoSala.GRUPO).build();

        return salaRepository.create(List.of(sala1, sala2));
    }

    private List<Ficha> getFichas() {
        Ficha ficha1 = Ficha.builder()
            .nome("Ficha 01").build();

        Ficha ficha2 = Ficha.builder()
            .nome("Ficha 02").build();

        return List.of(ficha1, ficha2);
    }

    @Test
    public void givenAtendimentoIndividual_whenCreate_thenReturnAtendimentoIndividual() {
        Funcionario funcionario = getFuncionarios().get(0);
        Sala sala = getSalas().get(0);
        Ficha ficha = registerFicha(funcionario.getId(),null);

        var atividade = AtendimentoIndividual.builder()
            .statusAtividade(StatusAtividade.PENDENTE)
            .funcionario(funcionario)
            .tempoInicio(LocalDateTime.of(2005,1,1,12,30))
            .tempoFim(LocalDateTime.of(2006,1,1,12,30))
            .sala(sala)
            .ficha(ficha)
            .build();

        var result = (AtendimentoIndividual) repository.create(atividade);

        Assertions.assertNotNull(result, "Retorno nulo");
        assertAtendimentoIndividualEqualsWithoutIds(atividade,result);
    }

    private void assertAtividadeEqualsWithoutIds(Atividade expected, Atividade actual) {
        Assertions.assertEquals(expected.getSala(), actual.getSala(), "Sala diferente");
        Assertions.assertEquals(expected.getFuncionario(), actual.getFuncionario(), "Funcionario diferente");
        Assertions.assertEquals(expected.getStatus(), actual.getStatus(), "Status diferentes");
        Assertions.assertEquals(expected.getTempoFim(), actual.getTempoFim(), "TempoFim diferente");
        Assertions.assertEquals(expected.getTempoInicio(), actual.getTempoInicio(), "TempoInicio diferente");
    }

    private void assertAtendimentoIndividualEqualsWithoutIds(AtendimentoIndividual expected,
                                                             AtendimentoIndividual actual) {
        Assertions.assertEquals(expected.getTerapeuta(), actual.getTerapeuta(), "Ficha diferente");
        Assertions.assertEquals(expected.getFicha(), actual.getFicha(), "Ficha diferente");
        assertAtividadeEqualsWithoutIds(expected, actual);

    }

    private Ficha registerFicha(int idResponsavel, GrupoTerapeutico grupoTerapeutico) {
        Ficha ficha = getFichas().get(0);
        ficha.setIdResponsavel(idResponsavel);
        ficha.setGrupoTerapeutico(grupoTerapeutico);
        return fichaRepository.create(ficha);
    }

}
