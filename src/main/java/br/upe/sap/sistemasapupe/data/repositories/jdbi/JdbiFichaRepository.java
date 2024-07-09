package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FichaRepository;
import org.jdbi.v3.core.Jdbi;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

@Repository
public class JdbiFichaRepository implements FichaRepository {

    Jdbi jdbi;

    public JdbiFichaRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
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
                .mapToBean(Ficha.class)
                .findFirst()
        );

        return  resultado.orElse(null);
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
            SELECT id, uid, id_responsavel, id_grupo_terapeutico
            FROM fichas
            WHERE uid = :uid
            """;

        Optional<Ficha> resultado = jdbi.withHandle(handle -> handle
            .createQuery(QUERY)
            .bind("uid", uid)
            .mapToBean(Ficha.class)
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
    public void delete(UUID id) {

    }

    @Override
    public void delete(List<UUID> uuids) {

    }

    @Override
    public Ficha findByFuncionario(UUID uidFuncionario) {
        return null;
    }
}
