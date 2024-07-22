package br.upe.sap.sistemasapupe.data.repositories.jdbi.atividades;

import br.upe.sap.sistemasapupe.data.model.atividades.AtendimentoIndividual;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FichaRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.AtendimentoIndividualRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.sala.SalaRepository;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@Repository
@AllArgsConstructor
class JdbiAtendimentoIndividualRepository implements AtendimentoIndividualRepository {

    // DEPENDÊNCIAS //
    Jdbi jdbi;

    FuncionarioRepository funcionarioRepository;

    SalaRepository salaRepository;

    FichaRepository fichaRepository;

    AuxAtividadeRepository atividadeCreator;


    // CREATE //
    @Override
    public AtendimentoIndividual create(AtendimentoIndividual atendimentoIndividual) {
        final String CREATE = """
            INSERT INTO atendimentos_individuais (id, id_ficha, id_terapeuta)
                VALUES (:id, :idFicha, :idTerapeuta) RETURNING id, id_ficha, id_terapeuta
            """;

        var result = (AtendimentoIndividual) atividadeCreator.create(atendimentoIndividual);
        return jdbi.withHandle(handle -> handle
            .createUpdate(CREATE)
            .bind("id", result.getId())
            .bind("idFicha", atendimentoIndividual.getFicha().getId())
            .bind("idTerapeuta", atendimentoIndividual.getTerapeuta().getId())
            .executeAndReturnGeneratedKeys()
            .map((rs, ctx) -> fillAtendimentoIndividual(rs, ctx, result))
            .findFirst().orElse(null));
    }

    private AtendimentoIndividual fillAtendimentoIndividual(ResultSet rs, StatementContext ctx,
                                                            AtendimentoIndividual atv) throws SQLException {
        atv.setFicha(fichaRepository.findById(rs.getInt("id_ficha")));
        atv.setTerapeuta(funcionarioRepository.findById(rs.getInt("id_terapeuta")));
        return atv;
    }

    @Override
    public List<AtendimentoIndividual> create(List<AtendimentoIndividual> atendimentoIndividuals) {
        return atendimentoIndividuals.stream().map(this::create).toList();
    }


    // UPDATE //
    @Override
    public AtendimentoIndividual update(AtendimentoIndividual atendimentoIndividual) {
        final String UPDATE = """
        UPDATE atendimentos_individuais
        SET id_ficha = :id_ficha, id_terapeuta = :id_terapeuta
        WHERE id = :id
        RETURNING id_ficha, id_terapeuta
    """;

        return jdbi.withHandle(handle -> handle
                .createUpdate(UPDATE)
                .bind("id", atendimentoIndividual.getId())
                .bind("id_ficha", atendimentoIndividual.getFicha().getId())
                .bind("id_terapeuta", atendimentoIndividual.getTerapeuta().getId())
                .executeAndReturnGeneratedKeys()
                .mapToMap()
                .findFirst()
                .map(map -> {
                    atendimentoIndividual.setFicha(fichaRepository.findById((Integer) map.get("id_ficha")));
                    atendimentoIndividual.setTerapeuta(funcionarioRepository.findById((Integer) map.get("id_terapeuta")));
                    return atendimentoIndividual;
                })
                .orElse(null)
        );
    }

    @Override
    public List<AtendimentoIndividual> update(List<AtendimentoIndividual> atendimentoIndividuals) {
        return atendimentoIndividuals.stream().map(this::update).toList();
    }


    // READ //
    @Override
    public AtendimentoIndividual findById(Integer id) {
        final String QUERY = """
            SELECT id_ficha, id_terapeuta, %s
                FROM atendimentos_individuais INNER JOIN atividades
                    ON atividades.id = :id AND atendimentos_individuais.id = atividades.id
        """.formatted(AuxAtividadeRepository.returningAtividadeColumns);

        return jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .bind("id", id)
            .map((rs, ctx) -> {
                var atividade =
                        BeanMapper.of(AtendimentoIndividual.class).map(rs, ctx);

                atividadeCreator.fillAtividadeFields(atividade, rs);
                atividade.setFicha(
                        fichaRepository.findById(rs.getInt("id_ficha")));
                atividade.setTerapeuta(atividade.getFuncionario());

                return atividade;
            })
            .findFirst().orElse(null));
    }

    @Override
    public List<AtendimentoIndividual> findAll() {
        final String QUERY = """
            SELECT id_ficha, id_terapeuta, %s
                FROM atendimentos_individuais INNER JOIN atividades
                    ON atendimentos_individuais.id = atividades.id
        """.formatted(AuxAtividadeRepository.returningAtividadeColumns);

        return jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .map((rs, ctx) -> {
                var atividade =
                        BeanMapper.of(AtendimentoIndividual.class).map(rs, ctx);

                atividadeCreator.fillAtividadeFields(atividade, rs);
                atividade.setFicha(
                        fichaRepository.findById(rs.getInt("id_ficha")));
                atividade.setTerapeuta(atividade.getFuncionario());

                return atividade;
            })
            .collectIntoList());
    }

    @Override
    public List<AtendimentoIndividual> findById(List<Integer> ids) {
        final String QUERY = """
            SELECT id_ficha, id_terapeuta, %s
                FROM atendimentos_individuais INNER JOIN atividades
                    ON atividades.id IN (%s) AND atendimentos_individuais.id = atividades.id
        """.formatted(AuxAtividadeRepository.returningAtividadeColumns, "ids");

        return jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .bindList("ids", ids)
            .map((rs, ctx) -> {
                var atividade =
                        BeanMapper.of(AtendimentoIndividual.class).map(rs, ctx);

                atividadeCreator.fillAtividadeFields(atividade, rs);
                atividade.setFicha(
                        fichaRepository.findById(rs.getInt("id_ficha")));
                atividade.setTerapeuta(atividade.getFuncionario());

                return atividade;
            })
            .collectIntoList());
    }


    // DELETE
    @Override
    public int delete(Integer id) {
        return atividadeCreator.delete(id);
    }

    @Override
    public int delete(List<Integer> ids) {
        return atividadeCreator.delete(ids);
    }

}
