package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoEstudo;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.GrupoEstudoRepository;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;


@Repository
public class JdbiGrupoEstudoRepository implements GrupoEstudoRepository {

    Jdbi jdbi;
    FuncionarioRepository funcionarioRepository;

    private GrupoEstudo mapGrupoEstudo(ResultSet rs, StatementContext stx) throws SQLException {
        GrupoEstudo grupoEstudo = BeanMapper.of(GrupoEstudo.class).map(rs, stx);
        grupoEstudo.setDono(funcionarioRepository.findById(rs.getInt("id_dono")));
        return grupoEstudo;
    }

    @Override
    public GrupoEstudo create(GrupoEstudo grupoEstudo) {
        String CREATE = """
                INSERT INTO grupos_estudo (id_dono, tema)
                VALUES (:id_dono, :tema)
                """;
        return jdbi.withHandle(handle -> handle
                .createUpdate(CREATE)
                .bind("id_dono", grupoEstudo.getDono().getId())
                .bind("tema", grupoEstudo.getTemaEstudo())
                .executeAndReturnGeneratedKeys()
                .map(this::mapGrupoEstudo)
                .findFirst()
                .orElse(null));

    }

    @Override
    public List<GrupoEstudo> create(List<GrupoEstudo> grupoEstudos) {
        return grupoEstudos.stream().map(this::create).toList();
    }

    @Override
    public GrupoEstudo update(GrupoEstudo grupoEstudo) {
        return null;
    }

    @Override
    public List<GrupoEstudo> update(List<GrupoEstudo> grupoEstudos) {
        return null;
    }


    @Override
    public GrupoEstudo findById(Integer id) {
        return null;
    }

    @Override
    public List<GrupoEstudo> findAll() {
        return null;
    }

    @Override
    public List<GrupoEstudo> findById(List<Integer> ids) {
        return null;
    }

    @Override
    public int delete(Integer id) {
        final String DELETE = "DELETE FROM grupos_estudo WHERE id = :id";

        return jdbi.withHandle(handle -> handle
                        .createUpdate(DELETE)
                        .bind("id_dono", id))
                .execute();
    }

    @Override
    public int delete(List<Integer> ids) {
        final String DELETE = "DELETE FROM grupos_estudo WHERE id IN (%s)".formatted("<ids>");

        return jdbi.withHandle(handle -> handle
                .createUpdate(DELETE)
                .bindList("ids", ids)
                .execute());
    }

    @Override
    public GrupoEstudo findByFuncionario(int idFuncionario) {
        return null;
    }

    @Override
    public Funcionario addFuncionario(Funcionario funcionario) {
        return null;
    }

    @Override
    public void deleteParticipacao(int idParticipante) {
        final String DELETE = "DELETE FROM participacao_grupos_estudo WHERE ";

    }
}


