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

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.*;

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
                .funcionario(1)
                .nome("Pedin")
                .build();
        Ficha ficha2 = Ficha.fichaBuilder()
                .funcionario(2)
                .nome("Erick")
                .build();

        return List.of(ficha1, ficha2);
    }

    private List<GrupoTerapeutico> getGrupoTerapeuticos(){
        List<Integer> ids = List.of(1, 2);
        GrupoTerapeutico grupoTerapeutico1 = GrupoTerapeutico
                .grupoTerapeuticoBuilder()
                .tema("SAP")
                .idDono(1)
                .descricao("muito legal")
                .build();
        GrupoTerapeutico grupoTerapeutico2 = GrupoTerapeutico
                .grupoTerapeuticoBuilder()
                .tema("Consciencia de classe")
                .idDono(1)
                .descricao("uau")
                .build();
        return List.of(grupoTerapeutico1, grupoTerapeutico2);
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
        List <Integer> funcionarios = List.of(supervisor1.getId(), supervisor2.getId());

        Ficha ficha1 = fichaRepository.create(getFichas().get(0));
        Ficha ficha2 = fichaRepository.create(getFichas().get(0));
        List <Integer> fichas = List.of(ficha1.getId(), ficha2.getId());

        GrupoTerapeutico grupoTerapeutico = GrupoTerapeutico
                .grupoTerapeuticoBuilder()
                .tema("SAP")
                .idDono(funcionarios.get(0))
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
        assertEquals(expected.getIdDono(), actual.getIdDono(), "idDono não correspondem");
        assertEquals(expected.getTema(), actual.getTema(), "Temas terapia não correspondem");
        assertEquals(expected.getDescricao(), actual.getDescricao(), "As descrições não correspondem");
    }

    @Test
    @DisplayName("Dado um grupo terapêutico, quando buscar pelo ID, retornar esse grupo terapêutico")
    public void givenTherapeuticGroup_whenFindByID_thenReturnTherapeuticGroup(){
        Tecnico supervisor1 = (Tecnico) funcionariosRepository.create(getTecnicos().get(0));
        Tecnico supervisor2 = (Tecnico) funcionariosRepository.create(getTecnicos().get(1));
        Ficha ficha1 = fichaRepository.create(getFichas().get(0));
        Ficha ficha2 = fichaRepository.create(getFichas().get(0));

        GrupoTerapeutico grupoTerapeutico = repository.create(getGrupoTerapeuticos().get(0));
        GrupoTerapeutico foundGrupoTerapeutico = repository.findById(grupoTerapeutico.getId());
        assertNotNull(foundGrupoTerapeutico);
        assertEquals(grupoTerapeutico.getId(), foundGrupoTerapeutico.getId());
    }


    @Test
    @DisplayName("Dado um grupo terapêutico, quando atualizado, retornar esse grupo terapêutico com os valores atualizados")
    public void givenTherapeuticGroup_whenUpdate_thenReturnUpdatedTherapeuticGroup() {
        Tecnico supervisor1 = (Tecnico) funcionariosRepository.create(getTecnicos().get(0));
        Tecnico supervisor2 = (Tecnico) funcionariosRepository.create(getTecnicos().get(1));
        Ficha ficha1 = fichaRepository.create(getFichas().get(0));
        Ficha ficha2 = fichaRepository.create(getFichas().get(0));

        GrupoTerapeutico grupoTerapeutico = repository.create(getGrupoTerapeuticos().get(0));

        grupoTerapeutico.setTema("Marxismo");

        GrupoTerapeutico grupoTerapeuticoResultado = repository.update(grupoTerapeutico);
        assertNotNull(grupoTerapeuticoResultado);
        assertEquals(repository.findById(1).getId(), grupoTerapeuticoResultado.getId());
        assertEquals(repository.findById(1).getTema(), grupoTerapeuticoResultado.getTema());
    }

    @Test
    @DisplayName("Dado lista de grupo terapêutico, quando buscar todos, retornar essa lista")
    public void givenTherapeuticGroups_whenFindAll_thenReturnTherapeuticGroups() {
        Tecnico supervisor1 = (Tecnico) funcionariosRepository.create(getTecnicos().get(0));
        Tecnico supervisor2 = (Tecnico) funcionariosRepository.create(getTecnicos().get(1));
        Ficha ficha1 = fichaRepository.create(getFichas().get(0));
        Ficha ficha2 = fichaRepository.create(getFichas().get(0));

        GrupoTerapeutico grupoTerapeutico1 = repository.create(getGrupoTerapeuticos().get(0));
        GrupoTerapeutico grupoTerapeutico2 = repository.create(getGrupoTerapeuticos().get(1));

        List<GrupoTerapeutico> foundGrupoTerapeuticos = repository.findAll();
        assertNotNull(foundGrupoTerapeuticos);
        assertEquals(2, foundGrupoTerapeuticos.size());
    }

    @Test
    @DisplayName("Dado um funcionário e um grupo terapêutico, quando adicionado, o funcionário deve ser associado ao grupo terapêutico")
    public void givenFuncionarioAndGrupoTerapeutico_whenAddFuncionario_thenFuncionarioIsAddedToGrupoTerapeutico() {
        Tecnico supervisor1 = (Tecnico) funcionariosRepository.create(getTecnicos().get(0));
        Tecnico supervisor2 = (Tecnico) funcionariosRepository.create(getTecnicos().get(1));

        GrupoTerapeutico grupoTerapeutico = repository.create(getGrupoTerapeuticos().get(0));

        GrupoTerapeutico updatedGrupo = repository.addFuncionario(supervisor1.getId(), grupoTerapeutico.getId());
        updatedGrupo = repository.addFuncionario(supervisor2.getId(), grupoTerapeutico.getId());

        List<GrupoTerapeutico> grupos = repository.findByFuncionario(supervisor1.getId());

        assertThat(grupos).isNotEmpty();
    }

    @Test
    @DisplayName("Dado um ID de funcionário, quando encontrado, retornar a lista de grupos terapêuticos associados")
    public void givenFuncionarioId_whenFindByFuncionario_thenReturnGrupoTerapeuticos() {
        Tecnico supervisor1 = (Tecnico) funcionariosRepository.create(getTecnicos().get(0));
        Tecnico supervisor2 = (Tecnico) funcionariosRepository.create(getTecnicos().get(1));
        List<Integer> idsSupervisores = List.of(supervisor1.getId(), supervisor2.getId());
        Ficha ficha1 = fichaRepository.create(getFichas().get(0));
        Ficha ficha2 = fichaRepository.create(getFichas().get(0));

        GrupoTerapeutico grupoTerapeutico1 = repository.create(getGrupoTerapeuticos().get(0));
        GrupoTerapeutico grupoTerapeutico2 = repository.create(getGrupoTerapeuticos().get(1));
        repository.addFuncionario(idsSupervisores, grupoTerapeutico1.getId());
        repository.addFuncionario(idsSupervisores, grupoTerapeutico2.getId());

        List<GrupoTerapeutico> foundGrupoTerapeuticos = repository.findByFuncionario(supervisor1.getId());

        assertNotNull(foundGrupoTerapeuticos);
        assertEquals(2, foundGrupoTerapeuticos.size());
    }

    @Test
    @DisplayName("Dado um ID de ficha, quando encontrado, retornar a lista de grupos terapêuticos associados")
    public void givenFichaId_whenFindByFicha_thenReturnGrupoTerapeuticos() {
        Tecnico supervisor1 = (Tecnico) funcionariosRepository.create(getTecnicos().get(0));
        Tecnico supervisor2 = (Tecnico) funcionariosRepository.create(getTecnicos().get(1));
        List<Integer> idsSupervisores = List.of(supervisor1.getId(), supervisor2.getId());
        Ficha ficha1 = fichaRepository.create(getFichas().get(0));
        Ficha ficha2 = fichaRepository.create(getFichas().get(0));

        GrupoTerapeutico grupoTerapeutico1 = repository.create(getGrupoTerapeuticos().get(0));
        GrupoTerapeutico grupoTerapeutico2 = repository.create(getGrupoTerapeuticos().get(1));
        repository.addFuncionario(idsSupervisores, grupoTerapeutico1.getId());
        repository.addFuncionario(idsSupervisores, grupoTerapeutico2.getId());
        repository.addFicha(List.of(ficha1.getId(), ficha2.getId()), grupoTerapeutico1.getId());

        GrupoTerapeutico foundGrupoTerapeutico = repository.findByFicha(ficha1.getId());

        assertNotNull(foundGrupoTerapeutico);
    }
    @Test
    @DisplayName("Dado um grupo terapeutico, quando removido funcionario, retornar o ID desse funcionario")
    public void givenGrupoTerapeutico_whenRemoveFuncionario_thenReturnIdFuncionario(){
        Tecnico supervisor1 = (Tecnico) funcionariosRepository.create(getTecnicos().get(0));
        Tecnico supervisor2 = (Tecnico) funcionariosRepository.create(getTecnicos().get(1));
        List<Integer> idsSupervisores = List.of(supervisor1.getId(), supervisor2.getId());
        Ficha ficha1 = fichaRepository.create(getFichas().get(0));
        Ficha ficha2 = fichaRepository.create(getFichas().get(0));

        GrupoTerapeutico grupoTerapeutico1 = repository.create(getGrupoTerapeuticos().get(0));
        GrupoTerapeutico grupoTerapeutico2 = repository.create(getGrupoTerapeuticos().get(1));
        repository.addFuncionario(idsSupervisores, grupoTerapeutico1.getId());
        repository.addFuncionario(idsSupervisores, grupoTerapeutico2.getId());
        repository.addFicha(List.of(ficha1.getId(), ficha2.getId()), grupoTerapeutico1.getId());

        int resultado = repository.removerFuncionario(supervisor1.getId(), grupoTerapeutico1.getId());

        assertEquals(1, resultado);
    }

    @Test
    @DisplayName("Dado um grupo terapeutico, quando removido uma ficha, retornar o ID dessa ficha")
    public void givenGrupoTerapeutico_whenRemoveFicha_thenReturnIdFicha(){
        Tecnico supervisor1 = (Tecnico) funcionariosRepository.create(getTecnicos().get(0));
        Tecnico supervisor2 = (Tecnico) funcionariosRepository.create(getTecnicos().get(1));
        List<Integer> idsSupervisores = List.of(supervisor1.getId(), supervisor2.getId());
        Ficha ficha1 = fichaRepository.create(getFichas().get(0));
        Ficha ficha2 = fichaRepository.create(getFichas().get(0));

        GrupoTerapeutico grupoTerapeutico1 = repository.create(getGrupoTerapeuticos().get(0));
        GrupoTerapeutico grupoTerapeutico2 = repository.create(getGrupoTerapeuticos().get(1));
        repository.addFuncionario(idsSupervisores, grupoTerapeutico1.getId());
        repository.addFuncionario(idsSupervisores, grupoTerapeutico2.getId());
        repository.addFicha(List.of(ficha1.getId(), ficha2.getId()), grupoTerapeutico1.getId());

        int resultado = repository.removerFicha(ficha2.getId());

        assertEquals(null, fichaRepository.findById(2).getGrupoTerapeutico());
    }

    @Test
    @DisplayName("Dado um grupo terapeutico, quando removido, então retornar o ID desse grupo")
    public void givenGrupoTerapeutico_whenRemoveGrupoTerapeutico_thenReturnFicha(){
        Tecnico supervisor1 = (Tecnico) funcionariosRepository.create(getTecnicos().get(0));
        Tecnico supervisor2 = (Tecnico) funcionariosRepository.create(getTecnicos().get(1));
        List<Integer> idsSupervisores = List.of(supervisor1.getId(), supervisor2.getId());
        Ficha ficha1 = fichaRepository.create(getFichas().get(0));
        Ficha ficha2 = fichaRepository.create(getFichas().get(0));

        GrupoTerapeutico grupoTerapeutico1 = repository.create(getGrupoTerapeuticos().get(0));
        GrupoTerapeutico grupoTerapeutico2 = repository.create(getGrupoTerapeuticos().get(1));
        repository.addFuncionario(idsSupervisores, grupoTerapeutico1.getId());
        repository.addFuncionario(idsSupervisores, grupoTerapeutico2.getId());
        repository.addFicha(List.of(ficha1.getId(), ficha2.getId()), grupoTerapeutico1.getId());

        int resultado = repository.delete(grupoTerapeutico1.getId());

        assertEquals(null, repository.findById(1));
    }
}

