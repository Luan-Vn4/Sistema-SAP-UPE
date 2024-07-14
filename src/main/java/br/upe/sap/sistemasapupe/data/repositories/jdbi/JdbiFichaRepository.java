package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FichaRepository;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.jdbi.v3.core.statement.StatementContext;
import org.springframework.stereotype.Repository;

import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbiFichaRepository implements FichaRepository {

    Jdbi jdbi;
    JdbiFuncionariosRepository funcionariosRepository;

    public JdbiFichaRepository(Jdbi jdbi, JdbiFuncionariosRepository funcionariosRepository) {
        this.jdbi = jdbi;
        this.funcionariosRepository = funcionariosRepository;
    }

    @Override
    public Ficha create(Ficha ficha) {
        final String CREATE = """
            INSERT INTO fichas(id, uid, id_responsavel, id_grupo_terapeutico)
            VALUES (:id, :uid, :id_responsavel, :id_grupo)
            """;

        Optional<Ficha> resultado = jdbi.withHandle(handle -> handle
                .createUpdate(CREATE)
                .bindBean(ficha)
                .bind("id_responsavel", ficha.getResponsavel().getId())
                .bind("id_grupo", ficha.getGrupoTerapeutico().getId())
                .executeAndReturnGeneratedKeys()
                .map(this::mapFicha)
                .findFirst()
        );

        return  resultado.orElse(null);
    }

    private Ficha mapFicha(ResultSet rs, StatementContext ctx) throws SQLException {
        Ficha ficha = BeanMapper.of(Ficha.class).map(rs,ctx);
        ficha.setResponsavel(funcionariosRepository.findById((UUID) rs.getObject("resp_uid")));
        return ficha;
    }

    @Override
    public List<Ficha> create(List<Ficha> fichas) {
        return null;
    }

    @Override
    public Ficha update(Ficha ficha) {
        return null;
    }

    @Override
    public List<Ficha> update(List<Ficha> fichas) {
        return null;
    }

    @Override
    public Ficha findById(UUID uid) {
        final String QUERY = """
            SELECT id, uid, funcionarios.uid as resp_uid, id_grupo_terapeutico
            FROM fichas
            INNER JOIN funcionarios ON id_responsavel = funcionarios.id
            WHERE uid = :uid
            """;


        Optional<Ficha> resultado = jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .bind("uid", uid)
            .map(this::mapFicha)
            .findFirst());

        return resultado.orElse(null);
    }

    @Override
    public List<Ficha> findAll() {
        final String QUERY = """
                SELECT * 
                FROM fichas
                """;
        return jdbi.withHandle(handle -> handle
                .createQuery(QUERY)
                .mapTo(Ficha.class)
                .list());
    }

    @Override
    public List<Ficha> findById(List<UUID> ids) {
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
    public Ficha findByFuncionario(UUID uidFuncionario) {
        return null;
    }
}
