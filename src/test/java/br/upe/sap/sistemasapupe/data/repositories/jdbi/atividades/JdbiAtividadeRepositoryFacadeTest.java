package br.upe.sap.sistemasapupe.data.repositories.jdbi.atividades;

import br.upe.sap.sistemasapupe.configuration.DataSourceTestConfiguration;
import br.upe.sap.sistemasapupe.data.model.atividades.*;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import br.upe.sap.sistemasapupe.data.model.enums.TipoSala;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Estagiario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Tecnico;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoEstudo;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FichaRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.GrupoEstudoRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.GrupoTerapeuticoRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.sala.SalaRepository;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
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
    GrupoEstudoRepository grupoEstudoRepository;

    @Autowired
    FuncionarioRepository funcionarioRepository;

    @Autowired
    GrupoTerapeuticoRepository grupoTerapeuticoRepository;

    @BeforeEach
    public void truncateTables() {
        jdbi.useHandle(handle -> {
            handle.execute("TRUNCATE TABLE atividades CASCADE");
            handle.execute("TRUNCATE TABLE funcionarios, supervisoes CASCADE");
            handle.execute("TRUNCATE TABLE fichas CASCADE");
            handle.execute("TRUNCATE TABLE grupos_terapeuticos CASCADE ");
        });
    }

    private List<Funcionario> createFuncionarios() {
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

    private Estagiario createEstagiario() {
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

    private List<Sala> createSalas() {
        Sala sala1 = Sala.salaBuilder()
            .nome("SALA 1").tipoSala(TipoSala.INDIVIDUAL).build();

        Sala sala2 = Sala.salaBuilder()
            .nome("SALA 2").tipoSala(TipoSala.GRUPO).build();

        return salaRepository.create(List.of(sala1, sala2));
    }

    private List<Ficha> createFichas(int idResponsavel, Integer idGrupoTerapeutico) {
        Ficha ficha1 = Ficha.builder()
            .nome("Ficha 01")
            .idResponsavel(idResponsavel)
            .idGrupoTerapeutico(idGrupoTerapeutico)
            .build();

        Ficha ficha2 = Ficha.builder()
            .nome("Ficha 02")
            .idResponsavel(idResponsavel)
            .idGrupoTerapeutico(idGrupoTerapeutico)
            .build();

        return fichaRepository.create(List.of(ficha1, ficha2));
    }

    private GrupoEstudo createGrupoEstudo(int idDono) {
        GrupoEstudo grupoEstudo = GrupoEstudo.grupoEstudoBuilder()
            .tema("A influência de LOL nos índices de evasão escolar")
            .descricao("É complicado isso aí, cara")
            .dono(idDono)
            .build();

        return grupoEstudoRepository.create(grupoEstudo);
    }

    private GrupoTerapeutico createGrupoTerapeutico(int idDono) {
        GrupoTerapeutico grupoTerapeutico = GrupoTerapeutico.grupoTerapeuticoBuilder()
            .tema("PÁSSAROS VOAM!!")
            .idDono(idDono)
            .descricao("Eles voam com as asas")
            .build();

        return grupoTerapeuticoRepository.create(grupoTerapeutico);
    }

    private AtendimentoIndividual getAtendimentoIndividual(StatusAtividade status,
                                                           Funcionario funcionario, Sala sala, Ficha ficha) {
        return AtendimentoIndividual.builder()
            .statusAtividade(status)
            .funcionario(funcionario)
            .tempoInicio(LocalDateTime.of(2005,1,1,12,30))
            .tempoFim(LocalDateTime.of(2006,1,1,12,30))
            .sala(sala)
            .ficha(ficha)
            .terapeuta(funcionario)
            .build();
    }

    private Encontro getEncontro(StatusAtividade status, Funcionario funcionario, Sala sala,
                                 Integer idGrupoEstudo, List<Integer> idsPresentes) {
        return Encontro.builder()
            .statusAtividade(status)
            .funcionario(funcionario)
            .tempoInicio(LocalDateTime.of(2005,1,1,12,30))
            .tempoFim(LocalDateTime.of(2006,1,1,12,30))
            .sala(sala)
            .idGrupoEstudo(idGrupoEstudo)
            .idsPresentes(idsPresentes)
            .build();
    }

    private AtendimentoGrupo getAtendimentoGrupo(StatusAtividade status, Funcionario funcionario, Sala sala,
                                                 int idGrupoTerapeutico, List<Integer> idsMinistrantes,
                                                 List<Integer> idsParticipantes) {
        return AtendimentoGrupo.builder()
            .statusAtividade(status)
            .funcionario(funcionario)
            .tempoInicio(LocalDateTime.of(2005,1,1,12,30))
            .tempoFim(LocalDateTime.of(2006,1,1,12,30))
            .sala(sala)
            .idGrupoTerapeutico(idGrupoTerapeutico)
            .idsMinistrantes(idsMinistrantes)
            .idsParticipantes(idsParticipantes)
            .build();
    }

    private <T extends Atividade> void assertEqualsWithoutIds(T expected, T actual) {
        Assertions.assertEquals(expected.getSala(), actual.getSala(), "Sala diferente");
        Assertions.assertEquals(expected.getFuncionario(), actual.getFuncionario(), "Funcionario diferente");
        Assertions.assertEquals(expected.getStatus(), actual.getStatus(), "Status diferentes");
        Assertions.assertEquals(expected.getTempoFim(), actual.getTempoFim(), "TempoFim diferente");
        Assertions.assertEquals(expected.getTempoInicio(), actual.getTempoInicio(), "TempoInicio diferente");

        if (expected instanceof AtendimentoIndividual)
            assertAtendimentoIndividualEqualsWithoutIds((AtendimentoIndividual) expected, (AtendimentoIndividual) actual);
        else if (expected instanceof Encontro)
            assertEncontroEqualsWithoutIds((Encontro) expected, (Encontro) actual);
        else
            assertAtendimentoGrupoEqualsWithoutIds((AtendimentoGrupo) expected, (AtendimentoGrupo) actual);
    }

    private void assertAtendimentoIndividualEqualsWithoutIds(AtendimentoIndividual expected,
                                                             AtendimentoIndividual actual) {
        Assertions.assertEquals(expected.getTerapeuta(), actual.getTerapeuta(), "Terapeuta diferente");
        Assertions.assertEquals(expected.getFicha(), actual.getFicha(), "Ficha diferente");
    }

    private void assertEncontroEqualsWithoutIds(Encontro expected, Encontro actual) {
        Assertions.assertEquals(expected.getIdGrupoEstudo(), actual.getIdGrupoEstudo(), "Grupos de estudo diferente");
        Assertions.assertEquals(expected.getIdsPresentes(), actual.getIdsPresentes(), "Comparecimentos diferente");
    }

    private void assertAtendimentoGrupoEqualsWithoutIds(AtendimentoGrupo expected,
                                                        AtendimentoGrupo actual) {
        Assertions.assertEquals(expected.getIdsMinistrantes(), actual.getIdsMinistrantes(), "Ministrantes diferente");
        Assertions.assertEquals(expected.getIdsParticipantes(), actual.getIdsParticipantes(), "Participantes diferente");
    }


    // CREATE //
    @Test
    @DisplayName("Dado um atendimento individual, ao criar, retornar atendimento individual com dados" +
                 "preenchidos")
    public void givenAtendimentoIndividual_whenCreate_thenReturnAtendimentoIndividual() {
        Funcionario funcionario = createFuncionarios().get(0);
        Sala sala = createSalas().get(0);
        Ficha ficha = createFichas(funcionario.getId(),null).get(0);

        var atividade = getAtendimentoIndividual(StatusAtividade.PENDENTE, funcionario, sala, ficha);

        var result = (AtendimentoIndividual) repository.create(atividade);

        Assertions.assertNotNull(result, "Retorno nulo");
        assertIdsNotNull(result);
        assertEqualsWithoutIds(atividade, result);
    }

    private void assertIdsNotNull(Atividade atividade) {
        Assertions.assertNotNull(atividade.getId(), "ID nulo!");
        Assertions.assertNotNull(atividade.getUid(), "UID nulo!");
    }

    @Test
    @DisplayName("Dado um encontro, quando criar, então retornar encontro com dados preenchidos")
    public void givenEncontro_whenCreate_thenReturnEncontro() {
        Funcionario funcionario = createFuncionarios().get(0);
        Sala sala = createSalas().get(0);
        GrupoEstudo grupoEstudo = createGrupoEstudo(funcionario.getId());

        Encontro encontro = getEncontro(
            StatusAtividade.PENDENTE, funcionario, sala, grupoEstudo.getId(), List.of());

        Encontro result = (Encontro) repository.create(encontro);

        Assertions.assertNotNull(result, "Retorno nulo");
        assertIdsNotNull(result);
        assertEqualsWithoutIds(encontro,result);
    }

    @Test
    @DisplayName("Dado um atendimento em grupo, quando criar, retornar atendimento em grupo com dados " +
                 "preenchidos")
    public void givenAtendimentoGrupo_whenCreate_thenReturnAtendimentoGrupo() {
        Funcionario funcionario = createFuncionarios().get(0);
        Sala sala = createSalas().get(0);
        GrupoTerapeutico grupoTerapeutico = createGrupoTerapeutico(funcionario.getId());

        AtendimentoGrupo atendimentoGrupo = getAtendimentoGrupo(StatusAtividade.PENDENTE,
                funcionario, sala, grupoTerapeutico.getId(), List.of(), List.of());

        AtendimentoGrupo result = (AtendimentoGrupo) repository.create(atendimentoGrupo);

        Assertions.assertNotNull(result);
        assertIdsNotNull(result);
        assertEqualsWithoutIds(atendimentoGrupo, result);
    }

    @Test
    public void givenIdsParticipantes_whenAddToAtendimentoGrupo_thenReturnAddedIds() {
        Funcionario funcionario = createFuncionarios().get(0);
        Sala sala = createSalas().get(0);
        GrupoTerapeutico grupoTerapeutico = createGrupoTerapeutico(funcionario.getId());
        List<Ficha> participantes = createFichas(funcionario.getId(),grupoTerapeutico.getId());

        AtendimentoGrupo atividade = (AtendimentoGrupo) repository.create(
                getAtendimentoGrupo(StatusAtividade.PENDENTE, funcionario, sala,
                        grupoTerapeutico.getId(), List.of(), List.of()));

        List<Integer> ids = participantes.stream().map(Ficha::getId).toList();
        List<Integer> result = repository.addParticipantesToAtendimentoGrupo(ids, atividade.getId());

        Assertions.assertEquals(ids.size(), result.size(), "Quantidade de resultados menor");
        Assertions.assertEquals(ids,result,"Resultado com ids diferentes");
    }

    @Test
    public void givenIdsMinistrantes_whenAddToAtendimentoGrupo_thenReturnAddedIds() {
        List<Funcionario> funcionarios = createFuncionarios();
        Sala sala = createSalas().get(0);
        GrupoTerapeutico grupoTerapeutico = createGrupoTerapeutico(funcionarios.get(0).getId());

        funcionarios.forEach(x -> grupoTerapeuticoRepository.addFuncionario(x.getId(), grupoTerapeutico.getId()));

        AtendimentoGrupo atividade = (AtendimentoGrupo) repository.create(
                getAtendimentoGrupo(StatusAtividade.PENDENTE, funcionarios.get(0), sala,
                        grupoTerapeutico.getId(), List.of(), List.of()));

        List<Integer> ids = funcionarios.stream().map(Funcionario::getId).toList();
        List<Integer> result = repository.addMinistrantesToAtendimentoGrupo(ids, atividade.getId());
    }

    @Test
    public void givenIdsComparecidos_whenAddComparecimentoToEncontro_thenReturnIdsComparecidos() {
        List<Funcionario> funcionario = createFuncionarios();
        Sala sala = createSalas().get(0);
        GrupoEstudo grupoEstudo = createGrupoEstudo(funcionario.get(0).getId());

        funcionario.stream()
            .map(Funcionario::getId)
            .forEach(id -> grupoEstudoRepository.addFuncionario(id, grupoEstudo.getId()));

        Encontro atividade = (Encontro) repository.create(
            getEncontro(StatusAtividade.PENDENTE, funcionario.get(0), sala, grupoEstudo.getId(), List.of()));

        List<Integer> ids = funcionario.stream().map(Funcionario::getId).toList();
        List<Integer> results = repository.addComparecimentosToEncontro(ids, atividade.getId());

        Assertions.assertEquals(ids.size(), results.size(), "Quantidade de resultados diferente");
        Assertions.assertEquals(ids, results,"Resultado com ids diferentes");
    }


    // READ //
    @Test
    public void whenFindAll_thenReturnAllAtividades() {
        Funcionario funcionario = createFuncionarios().get(0);
        Sala sala = createSalas().get(0);
        GrupoEstudo grupoEstudo = createGrupoEstudo(funcionario.getId());
        GrupoTerapeutico grupoTerapeutico = createGrupoTerapeutico(funcionario.getId());
        Ficha ficha = createFichas(funcionario.getId(),null).get(0);

        Encontro encontro = getEncontro(
                StatusAtividade.PENDENTE, funcionario, sala, grupoEstudo.getId(), List.of());

        AtendimentoGrupo atendimentoGrupo = getAtendimentoGrupo(StatusAtividade.PENDENTE,
                funcionario, sala, grupoTerapeutico.getId(), List.of(), List.of());

        var atividade = getAtendimentoIndividual(StatusAtividade.PENDENTE, funcionario, sala, ficha);

        List<Atividade> atividades = repository.create(List.of(encontro, atendimentoGrupo, atividade));
        List<Atividade> result = repository.findAll();

        Assertions.assertNotNull(result);
        result.forEach(this::assertIdsNotNull);
        Assertions.assertEquals(atividades.size(),result.size(), "Quantidade diferente de atividades");
        Assertions.assertEquals(atividades,result, "Atividades diferentes");
    }

    @Test
    public void givenAtendimentoIndividualId_whenFindById_thenReturnAtendimentoIndividual() {
        Funcionario funcionario = createFuncionarios().get(0);
        Sala sala = createSalas().get(0);
        Ficha ficha = createFichas(funcionario.getId(),null).get(0);

        var atividade = (AtendimentoIndividual) repository.create(
                getAtendimentoIndividual(StatusAtividade.PENDENTE, funcionario, sala, ficha));

        var result = (AtendimentoIndividual) repository.findById(atividade.getId());

        Assertions.assertNotNull(result);
        assertIdsNotNull(result);
        Assertions.assertEquals(atividade,result,"Atividade diferente daquela criada");
    }

    @Test
    public void givenEncontroId_whenFindById_thenReturnEncontro() {
        Funcionario funcionario = createFuncionarios().get(0);
        Sala sala = createSalas().get(0);
        GrupoEstudo grupoEstudo = createGrupoEstudo(funcionario.getId());

        Encontro atividade = (Encontro) repository.create(
            getEncontro(StatusAtividade.PENDENTE, funcionario, sala, grupoEstudo.getId(), List.of()));

        Encontro result = (Encontro) repository.findById(atividade.getId());

        Assertions.assertNotNull(result);
        assertIdsNotNull(result);
        Assertions.assertEquals(atividade, result,"Atividade diferente daquela criada");
    }

    @Test
    public void givenAtendimentoGrupoId_whenFindById_thenReturnAtendimentoGrupo() {
        Funcionario funcionario = createFuncionarios().get(0);
        Sala sala = createSalas().get(0);
        GrupoTerapeutico grupoTerapeutico = createGrupoTerapeutico(funcionario.getId());

        AtendimentoGrupo atividade = (AtendimentoGrupo) repository.create(
                getAtendimentoGrupo(StatusAtividade.PENDENTE, funcionario, sala,
                    grupoTerapeutico.getId(), List.of(), List.of()));

        var result = (AtendimentoGrupo) repository.findById(atividade.getId());

        Assertions.assertNotNull(result);
        assertIdsNotNull(result);
        Assertions.assertEquals(atividade, result, "Atividade diferente daquela criada");
    }

    @Test
    public void givenIds_whenFindById_thenReturnCorrespondingAtividades() {
        Funcionario funcionario = createFuncionarios().get(0);
        Sala sala = createSalas().get(0);
        GrupoEstudo grupoEstudo = createGrupoEstudo(funcionario.getId());
        GrupoTerapeutico grupoTerapeutico = createGrupoTerapeutico(funcionario.getId());
        Ficha ficha = createFichas(funcionario.getId(),null).get(0);

        Encontro encontro = getEncontro(
                StatusAtividade.PENDENTE, funcionario, sala, grupoEstudo.getId(), List.of());

        AtendimentoGrupo atendimentoGrupo = getAtendimentoGrupo(StatusAtividade.PENDENTE,
                funcionario, sala, grupoTerapeutico.getId(), List.of(), List.of());

        var atividade = getAtendimentoIndividual(StatusAtividade.PENDENTE, funcionario, sala, ficha);

        List<Atividade> atividades = repository.create(
                List.of(encontro, atendimentoGrupo, atividade));
        List<Atividade> results = repository.findById(
                List.of(atividades.get(0).getId(), atividades.get(1).getId()));

        Assertions.assertFalse(results.isEmpty(), "Lista de retorno vazia");
        Assertions.assertEquals(results.size(),2);
        for (int i = 0; i < results.size(); i++) {
            assertIdsNotNull(results.get(i));
            assertEqualsWithoutIds(atividades.get(i), results.get(i));
        }
    }


    // UPDATE //
    @Test
    public void givenAtividade_whenSimpleUpdate_thenReturnAtividade() {
        Funcionario funcionario = createFuncionarios().get(0);
        Sala sala = createSalas().get(0);
        Ficha ficha = createFichas(funcionario.getId(),null).get(0);

        var atividade = (AtendimentoIndividual) repository.create(
                getAtendimentoIndividual(StatusAtividade.PENDENTE, funcionario, sala, ficha));

        AtendimentoIndividual newAtividade = getAtendimentoIndividual(StatusAtividade.APROVADO, funcionario, sala, ficha);
        newAtividade.setId(atividade.getId());
        newAtividade.setUid(atividade.getUid());
        LocalDateTime newTempoInicio = LocalDateTime.of(2014,12,1,13,40);
        LocalDateTime newTempoFim = LocalDateTime.of(2015,12,1,13,40);

        newAtividade = (AtendimentoIndividual) repository.simpleUpdate(newAtividade);
        Assertions.assertNotNull(newAtividade, "Nova atividade nula");

        var found = (AtendimentoIndividual) repository.findById(atividade.getId());

        Assertions.assertEquals(newAtividade,found,"A atividade não foi atualizada" +
                                                   "devidamente");

    }


    // DELETE //
    @Test
    public void givenIdsMinistrantes_whenDeleteFromAtendimentoGrupo_thenReturnIds() {
        List<Funcionario> funcionarios = createFuncionarios();
        Sala sala = createSalas().get(0);
        GrupoTerapeutico grupoTerapeutico = createGrupoTerapeutico(funcionarios.get(0).getId());

        AtendimentoGrupo atividade = (AtendimentoGrupo) repository.create(
            getAtendimentoGrupo(StatusAtividade.PENDENTE, funcionarios.get(0), sala,
                    grupoTerapeutico.getId(), List.of(), List.of()));

        List<Integer> idsMinistrantes = funcionarios.stream().map(Funcionario::getId).toList();
        grupoTerapeuticoRepository.addFuncionario(idsMinistrantes,grupoTerapeutico.getId());
        repository.addMinistrantesToAtendimentoGrupo(idsMinistrantes, grupoTerapeutico.getId());
        repository.deleteMinistrantesFromAtendimentoGrupo(idsMinistrantes, grupoTerapeutico.getId());
        List<Integer> result = repository.findIdsMinistrantesFromAtendimentoGrupo(grupoTerapeutico.getId());

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void givenIdsParticipantes_whenDeleteFromAtendimentoGrupo_thenReturnIds() {
        Funcionario funcionario = createFuncionarios().get(0);
        Sala sala = createSalas().get(0);
        GrupoTerapeutico grupoTerapeutico = createGrupoTerapeutico(funcionario.getId());
        List<Ficha> participantes = createFichas(funcionario.getId(),grupoTerapeutico.getId());

        AtendimentoGrupo atividade = (AtendimentoGrupo) repository.create(
            getAtendimentoGrupo(StatusAtividade.PENDENTE, funcionario, sala,
                grupoTerapeutico.getId(), List.of(), List.of()));

        List<Integer> ids = participantes.stream().map(Ficha::getId).toList();
        repository.addParticipantesToAtendimentoGrupo(ids, atividade.getId());
        repository.deleteParticipantesFromAtendimentoGrupo(ids, atividade.getId());
        List<Integer> result = repository.findIdsParticipantesFromAtendimentoGrupo(atividade.getId());

        Assertions.assertTrue(result.isEmpty());
    }

    @Test
    public void givenIdsComparecidos_whenDeleteFromEncontro_thenReturnIds() {
        List<Funcionario> funcionario = createFuncionarios();
        Sala sala = createSalas().get(0);
        GrupoEstudo grupoEstudo = createGrupoEstudo(funcionario.get(0).getId());

        funcionario.stream()
            .map(Funcionario::getId)
            .forEach(id -> grupoEstudoRepository.addFuncionario(id, grupoEstudo.getId()));

        Encontro atividade = (Encontro) repository.create(
                getEncontro(StatusAtividade.PENDENTE, funcionario.get(0), sala, grupoEstudo.getId(), List.of()));

        List<Integer> idsComparecidos = funcionario.stream().map(Funcionario::getId).toList();
        repository.addComparecimentosToEncontro(idsComparecidos, atividade.getId());
        repository.deleteComparecidosFromEncontro(idsComparecidos, atividade.getId());
        List<Integer> results = repository.findIdsComparecidosFromEncontro(atividade.getId());

        Assertions.assertTrue(results.isEmpty());
    }

}
