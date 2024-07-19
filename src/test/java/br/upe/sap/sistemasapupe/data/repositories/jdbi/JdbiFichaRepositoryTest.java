package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.configuration.DataSourceTestConfiguration;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Tecnico;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.autoconfigure.domain.EntityScan;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.jdbc.JdbcTest;
import org.springframework.test.context.ContextConfiguration;

import java.util.List;

import static org.junit.jupiter.api.Assertions.*;

@JdbcTest
@ContextConfiguration(classes = {DataSourceTestConfiguration.class})
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@EntityScan(basePackages = {"br.upe.sap.sistemasapupe.data"})

public class JdbiFichaRepositoryTest {
    @Autowired
    Jdbi jdbi;

    @Autowired
    JdbiFichaRepository repository;

    @Autowired
    JdbiFuncionariosRepository funcionariosRepository;


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
                .nome("Pedin")
                .funcionario(1)
                .build();
        Ficha ficha2 = Ficha.fichaBuilder()
                .nome("Luan")
                .funcionario(1)
                .build();

        return List.of(ficha1, ficha2);
    }

    @AfterEach
    public void truncateTables() {
        jdbi.withHandle(handle -> handle.execute("TRUNCATE TABLE fichas, funcionarios, grupos_terapeuticos CASCADE"));
    }

   @Test
   @DisplayName("Dado uma ficha, quando criada, então retornar ficha com chaves auto-generadas")
    public void givenRecord_whenCreate_thenReturnRecordWithGeneratedKeys (){
       Tecnico supervisor = (Tecnico) funcionariosRepository.create(getTecnicos().get(0));

       Ficha ficha = Ficha.fichaBuilder()
               .nome("Pedin")
               .funcionario(supervisor.getId())
               .build();

       Ficha createdFicha = repository.create(ficha);

       assertNotNull(createdFicha);
       assertIdsAreNotNull(createdFicha);
       assertEqualsWithoutIds(ficha, createdFicha);

    }
    private void assertIdsAreNotNull(Ficha ficha) {
        assertNotNull(ficha.getId(), "ID não deve ser nulo");
        assertNotNull(ficha.getUid(), "UUID não deve ser nulo");
    }

    private void assertEqualsWithoutIds(Ficha expected, Ficha actual) {
        assertEquals(expected.getNome(), actual.getNome(), "Nome não coincide");
        assertEquals(expected.getIdResponsavel(), actual.getIdResponsavel(), "Responsavel não coincide");
    }

    @Test
    @DisplayName("Dado o ID de uma ficha, quando buscar, então retornar essa ficha")
    public void givenRecord_whenFindById_thenReturnRecord() {
        Tecnico supervisor = (Tecnico) funcionariosRepository.create(getTecnicos().get(0));

        Ficha ficha = Ficha.fichaBuilder()
                .nome("Pedin")
                .funcionario(supervisor.getId())
                .build();

        Ficha createdFicha = repository.create(ficha);
        Ficha foundFicha = repository.findById(createdFicha.getId());

        assertNotNull(foundFicha);
        assertIdsAreNotNull(foundFicha);
        assertEqualsWithoutIds(createdFicha, foundFicha);
    }

    @Test
    @DisplayName("Dado uma ficha, quando atualizada, retornar essa ficha atualizada")
    public void givenRecord_whenUpdate_thenReturnUpdatedRecord() {
        Tecnico supervisor = (Tecnico) funcionariosRepository.create(getTecnicos().get(0));

        Ficha ficha = Ficha.fichaBuilder()
                .nome("Pedin")
                .funcionario(supervisor.getId())
                .build();
        Ficha createdFicha = repository.create(ficha);

        Ficha fichaParaAtualizar  = Ficha.fichaBuilder()
                .nome("Pedrita")
                .funcionario(supervisor.getId())
                .build();
        fichaParaAtualizar.setId(createdFicha.getId());
        fichaParaAtualizar.setUid(createdFicha.getUid());

        Ficha fichaAtualizada = repository.update(fichaParaAtualizar);
        assertNotNull(fichaAtualizada);
        assertIdsAreNotNull(fichaAtualizada);
        assertEquals(createdFicha.getId(), fichaAtualizada.getId());
        assertEquals(createdFicha.getUid(), fichaAtualizada.getUid());
    }

    @Test
    @DisplayName("Dado um ID de uma ficha, quando deletar, então remover a ficha correspondente")
    public void givenRecordId_whenDelete_thenRemoveRecord() {
        Tecnico supervisor = (Tecnico) funcionariosRepository.create(getTecnicos().get(0));
        Ficha ficha = Ficha.fichaBuilder()
                .nome("Pedin")
                .funcionario(supervisor.getId())
                .build();

        Ficha createdFicha = repository.create(ficha);
        int id = createdFicha.getId();

        int rowsAffected = repository.delete(id);

        assertEquals(1, rowsAffected, "Número de linhas afetadas deve ser 1");
        assertNull(repository.findById(id), "Ficha não deve ser encontrado após deleção");
    }

    @Test
    @DisplayName("Dada o ID de um funcionário, quando buscar por esse ID, então retornar fichas correspondentes")
    public void givenPublishDate_whenFindByTempo_thenReturnMatchingPost() {
        Tecnico supervisor = (Tecnico) funcionariosRepository.create(getTecnicos().get(0));
        List<Ficha> createdFichas = repository.create(getFichas());
        List<Ficha> foundFichas = repository.findByFuncionario(supervisor.getId());

        assertNotNull(foundFichas, "Fichas não encontradas");
        //assertEquals(foundFichas.get(0), createdFichas.get(0), "Fichas não correspondentes");
        //assertEquals(foundFichas.get(1), createdFichas.get(1), "Fichas não correspondentes");
    }

    @Test
    @DisplayName("Dado uma lista de fichas, quando buscar todas as fichas, então retornar a lista completa")
    public void givenRecords_whenFindAll_thenReturnAllRecords() {
        funcionariosRepository.create(getTecnicos().get(0));
        repository.create(getFichas());

        List<Ficha> foundFichas = repository.findAll();

        assertNotNull(foundFichas, "Lista de posts não deve ser nula");
        assertEquals(getFichas().size(), foundFichas.size(), "Quantidade de posts encontrados não corresponde");
    }

    @Test
    @DisplayName("Dado uma lista de IDs, quando deletar fichas, retornar o número de fichas deletadas")
    public void givenListOfIds_whenDelete_thenReturnNumberOfDeletedRecords() {
        Tecnico supervisor = (Tecnico) funcionariosRepository.create(getTecnicos().get(0));
        List<Ficha> fichas = repository.create(getFichas());

        List<Integer> idsParaDeletar = List.of(fichas.get(0).getId(), fichas.get(1).getId());
        int deletedCount = repository.delete(idsParaDeletar);

        assertEquals(2, deletedCount);

        Ficha ficha1FromDb = repository.findById(fichas.get(0).getId());
        Ficha ficha2FromDb = repository.findById(fichas.get(1).getId());
        assertNull(ficha1FromDb);
        assertNull(ficha2FromDb);
    }

}
