package br.upe.sap.sistemasapupe.data.repositories.jdbi.atividades;

import br.upe.sap.sistemasapupe.data.model.atividades.AtendimentoGrupo;
import br.upe.sap.sistemasapupe.data.model.atividades.AtendimentoIndividual;
import br.upe.sap.sistemasapupe.data.model.atividades.Atividade;
import br.upe.sap.sistemasapupe.data.model.atividades.Encontro;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.AtendimentoGrupoRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.AtendimentoIndividualRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.AtividadeRepositoryFacade;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.EncontroRepository;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@AllArgsConstructor
public class JdbiAtividadeRepositoryFacade implements AtividadeRepositoryFacade {

    // DEPENDÊNCIAS //
    Jdbi jdbi;

    AtendimentoGrupoRepository atdGrupoRepository;

    AtendimentoIndividualRepository atdIndividualRepository;

    EncontroRepository encontroRepository;

    AuxAtividadeRepository auxRepository;


    // CREATE //
    public Atividade create(Atividade atividade) {
        if (atividade instanceof AtendimentoIndividual) {
            return atdIndividualRepository.create((AtendimentoIndividual) atividade);
        } else if (atividade instanceof AtendimentoGrupo) {
            return atdGrupoRepository.create((AtendimentoGrupo) atividade);
        }
        return encontroRepository.create((Encontro) atividade);
    }

    @Override
    public List<Atividade> create(List<Atividade> atividades) {
        return atividades.stream().map(this::create).toList();
    }

        // Relacionado - AtendimentoGrupo
    @Override
    public int addParticipanteToAtendimentoGrupo(int idFicha, int idAtividade) {
        return atdGrupoRepository.addParticipante(idFicha, idAtividade);
    }

    @Override
    public List<Integer> addParticipantesToAtendimentoGrupo(List<Integer> idFicha, int idAtividade) {
        return atdGrupoRepository.addParticipantes(idFicha, idAtividade);
    }

    @Override
    public int addMinistranteToAtendimentoGrupo(Integer idFuncionario, int idAtividade) {
        return atdGrupoRepository.addMinistrante(idFuncionario, idAtividade);
    }

    @Override
    public List<Integer> addMinistrantesToAtendimentoGrupo(List<Integer> idsFuncionarios, int idAtividade) {
        return atdGrupoRepository.addMinistrantes(idsFuncionarios, idAtividade);
    }

        // Relacionado - Encontro
    @Override
    public int addComparecimentoToEncontro(int funcionario, int idAtividade) {
        return encontroRepository.addComparecimento(funcionario, idAtividade);
    }

    @Override
    public List<Integer> addComparecimentosToEncontro(List<Integer> funcionarios, int idAtividade) {
        return encontroRepository.addComparecimentos(funcionarios, idAtividade);
    }


    // UPDATE //
    @Override
    public Atividade simpleUpdate(Atividade atividade) {
        return auxRepository.update(atividade);
    }

    @Override
    public List<Atividade> simpleUpdate(List<Atividade> atividades) {
        return atividades.stream().map(this::simpleUpdate).toList();
    }

    @Override
    public Atividade update(Atividade atividade) {
        if (atividade instanceof AtendimentoIndividual) {
            return atdIndividualRepository.update((AtendimentoIndividual) atividade);
        } else if (atividade instanceof AtendimentoGrupo) {
            return atdGrupoRepository.update((AtendimentoGrupo) atividade);
        }
        return encontroRepository.update((Encontro) atividade);
    }

    @Override
    public List<Atividade> update(List<Atividade> atividades) {
        return atividades.stream().map(this::update).toList();
    }


    // READ //
    @Override
    public Atividade findById(Integer id) {
        final String QUERY = """
            SELECT id, CASE id
                WHEN id IN (SELECT id FROM atendimentos_individuais) THEN 'ATENDIMENTO_INDIVIDUAL'
                WHEN id IN (SELECT id FROM atendimentos_grupo) THEN 'ATENDIMENTO_GRUPO'
                ELSE 'ENCONTRO' END AS tipo_atividade
            FROM atividades WHERE id = :id LIMIT 1
        """;

        return jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .bind("id", id)
            .map(this::mapAtividadeByTipoAtividade)
            .findFirst().orElse(null));
    }

    private Atividade mapAtividadeByTipoAtividade(ResultSet rs, StatementContext ctx) throws SQLException {
        int foundId = rs.getInt("id");
        String tipo = rs.getString("tipo_atividade");

        switch (tipo) {
            case "ATENDIMENTO_INDIVIDUAL" -> {
                return atdIndividualRepository.findById(foundId);
            } case "ATENDIMENTO_GRUPO" -> {
                return atdGrupoRepository.findById(foundId);
            } default -> {
                return encontroRepository.findById(foundId);
            }
        }
    }

    @Override
    public List<Atividade> findById(List<Integer> ids) {
        final String QUERY = """
            SELECT id, CASE id
                WHEN id IN (SELECT id FROM atendimentos_individuais) THEN 'ATENDIMENTO_INDIVIDUAL'
                WHEN id IN (SELECT id FROM atendimentos_grupo) THEN 'ATENDIMENTO_GRUPO'
                ELSE 'ENCONTRO' END AS tipo_atividade
            FROM atividades WHERE id IN (%s)
        """.formatted("<ids>");

        return jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .bindList("ids", ids)
            .map(this::mapAtividadeByTipoAtividade)
            .collectIntoList());
    }

    @Override
    public List<Atividade> findAll() {
        final String QUERY = """
            SELECT id, CASE id
                WHEN id IN (SELECT id FROM atendimentos_individuais) THEN 'ATENDIMENTO_INDIVIDUAL'
                WHEN id IN (SELECT id FROM atendimentos_grupo) THEN 'ATENDIMENTO_GRUPO'
                ELSE 'ENCONTRO' END AS tipo_atividade
            FROM atividades
        """;

        return jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .map(this::mapAtividadeByTipoAtividade)
            .collectIntoList());
    }

        // Relacionado - AtendimentoGrupo
    @Override
    public List<Integer> findIdsMinistrantesFromAtendimentoGrupo(int idAtividade) {
        return atdGrupoRepository.findIdsMinistrantes(idAtividade);
    }

    @Override
    public List<Integer> findIdsParticipantesFromAtendimentoGrupo(int idAtividade) {
        return atdGrupoRepository.findIdsParticipantes(idAtividade);
    }

        // Relacionado - Encontro
    @Override
    public List<Integer> findIdsComparecidosFromEncontro(int idAtividade) {
        return encontroRepository.findIdsComparecidos(idAtividade);
    }


    // DELETE //
    @Override
    public int delete(Integer id) {
        return auxRepository.delete(id);
    }

    @Override
    public int delete(List<Integer> ids) {
        return auxRepository.delete(ids);
    }

        // Relacionado - AtendimentoGrupo
    @Override
    public int deleteMinistranteFromAtendimentoGrupo(int idMinistrante, int idAtividade) {
        return atdGrupoRepository.deleteMinistrante(idMinistrante, idAtividade);
    }

    @Override
    public int deleteMinistrantesFromAtendimentoGrupo(List<Integer> idsMinistrantes, int idAtividade) {
        return atdGrupoRepository.deleteMinistrantes(idsMinistrantes, idAtividade);
    }

        // Relacionado - AtendimentoGrupo
    @Override
    public int deleteComparecidoFromEncontro(int idFuncionario, int idAtividade) {
        return encontroRepository.deleteComparecido(idFuncionario, idAtividade);
    }

    @Override
    public int deleteComparecidosFromEncontro(List<Integer> idsFuncionarios, int idAtividade) {
        return encontroRepository.deleteComparecidos(idsFuncionarios, idAtividade);
    }

}
