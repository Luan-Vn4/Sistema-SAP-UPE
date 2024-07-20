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
import java.util.UUID;

@Repository
public class JdbiGrupoEstudoRepository implements GrupoEstudoRepository {

    Jdbi jdbi;
    FuncionarioRepository funcionarioRepository;

    private GrupoEstudo mapGrupoEstudo (ResultSet rs, StatementContext stx) throws SQLException {
        GrupoEstudo grupoEstudo = BeanMapper.of(GrupoEstudo.class).map(rs,stx);
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
        return null;
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
    public GrupoEstudo findById(UUID id) {
        return null;
    }

    @Override
    public List<GrupoEstudo> findAll() {
        return null;
    }

    @Override
    public List<GrupoEstudo> findByIds(List<UUID> ids) {
        return null;
    }

    @Override
    public int delete(UUID id) {
        return 0;
    }

    @Override
    public int delete(List<UUID> uuids) {
        return 0;
    }

    @Override
    public GrupoEstudo findById(int idGrupoEstudo) {
        return null;
    }

    @Override
    public GrupoEstudo findByFuncionario(UUID idFuncionario) {
        return null;
    }

    @Override
    public void deleteGrupoEstudo(GrupoEstudo grupoEstudo) {

    }

    @Override
    public Funcionario addFuncionario(Funcionario funcionario) {
        return null;
    }

    @Override
    public void deleteFuncionario(UUID idFuncionario) {

    }
}
