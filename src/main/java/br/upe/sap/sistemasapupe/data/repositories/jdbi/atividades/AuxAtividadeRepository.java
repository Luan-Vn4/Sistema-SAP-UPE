package br.upe.sap.sistemasapupe.data.repositories.jdbi.atividades;

import br.upe.sap.sistemasapupe.data.model.atividades.Atividade;
import br.upe.sap.sistemasapupe.data.model.enums.StatusAtividade;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FichaRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.atividades.sala.SalaRepository;
import lombok.AllArgsConstructor;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;

@AllArgsConstructor
@Repository
class AuxAtividadeRepository {

    // DEPENDÊNCIAS //
    Jdbi jdbi;

    FuncionarioRepository funcionarioRepository;

    SalaRepository salaRepository;

    FichaRepository fichaRepository;

    // AUXILIARES //
    final static String returningAtividadeColumns = "atividades.id, atividades.uid, atividades.id_sala, " +
        "atividades.id_funcionario, atividades.tempo_inicio, atividades.tempo_fim, atividades.status";

    List<Integer> getIdsFichas(List<Ficha> fichas) {
        return fichas.stream().map(Ficha::getId).toList();
    }

    List<Integer> getIdsFuncionarios(List<? extends Funcionario> funcionarios) {
        return funcionarios.stream().map(Funcionario::getId).toList();
    }


    // CREATE //
    Atividade create(Atividade atividade) {
        final String CREATE = """
            INSERT INTO atividades (id_funcionario, id_sala, tempo_inicio, tempo_fim, status)
                VALUES (:idAutor, :idSala, :tempoInicio, :tempoFim, CAST(:status AS status_atividade))
                    RETURNING %s
            """.formatted(returningAtividadeColumns);

        return jdbi.withHandle(handle -> handle
                .createUpdate(CREATE)
                .bindBean(atividade)
                .bind("idSala", atividade.getSala().getId())
                .bind("idAutor", atividade.getFuncionario().getId())
                .executeAndReturnGeneratedKeys()
                .map((rs, ctx) -> mapAtividade(rs, ctx, atividade.getClass(), true))
                .findFirst().orElse(null));
    }

    Atividade mapAtividade(ResultSet rs, StatementContext ctx,
                                   Class<? extends Atividade> clazz, boolean eager) throws SQLException {
        Atividade atividade = BeanMapper.of(clazz).map(rs, ctx);
        atividade.setSala(salaRepository.findById(rs.getInt("id_sala")));
        atividade.setStatus(StatusAtividade.valueOf(rs.getString("status")));
        if (eager) fillAtividadeFields(atividade, rs);
        return atividade;
    }

    void fillAtividadeFields(Atividade atividade, ResultSet rs) throws SQLException {
        atividade.setSala(salaRepository.findById(
                rs.getInt("id_sala")));
        atividade.setFuncionario(funcionarioRepository.findById(
                rs.getInt("id_funcionario")));
    }


    // UPDATE //
    Atividade update(Atividade atividade) {
        final String UPDATE = """
            UPDATE atividades set id_sala = :idSala, id_funcionario = :idFuncionario, tempo_fim = :tempoInicio,
                tempo_fim = :tempo_fim, status = CAST(:status AS status_atividade)
                    RETURNING %s
            """.formatted(returningAtividadeColumns);

        return jdbi.withHandle(handle -> handle
                .createUpdate(UPDATE)
                .bindBean(atividade)
                .executeAndReturnGeneratedKeys()
                .map((rs, ctx) -> mapAtividade(rs, ctx, atividade.getClass(), true))
                .findFirst().orElse(null));
    }


    // DELETE //
    int delete(Integer id) {
        final String DELETE = "DELETE FROM atividades WHERE id = :id";

        return jdbi.withHandle(handle -> handle
            .createUpdate(DELETE)
            .bind("id", id)
            .execute());
    }

    int delete(List<Integer> ids) {
        final String DELETE = "DELETE FROM atividades WHERE id IN (%s)".formatted("<ids>");

        return jdbi.withHandle(handle -> handle
            .createUpdate(DELETE)
            .bindList("ids", ids)
            .execute());
    }

}
