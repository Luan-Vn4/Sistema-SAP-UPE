package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.data.model.grupos.GrupoTerapeutico;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.GrupoTerapeuticoRepository;
import org.jdbi.v3.core.Jdbi;

import java.util.ArrayList;
import java.util.List;
import java.util.UUID;

public class JdbiGrupoTerapeuticoRepository implements GrupoTerapeuticoRepository {
    Jdbi jdbi;

    public JdbiGrupoTerapeuticoRepository(Jdbi jdbi){
        this.jdbi = jdbi;
    }

    private String createGrupoTerapeuticoSQL(){
        return """
            INSERT INTO grupo_terapeutico(tema) VALUES
                (:tema)
                RETURNING *
        """;
    }

    private String selectGrupoTerapeuticoSQL(){
        return """
            SELECT id, uid, tema FROM grupo_terapeutico WHERE uid = :uid LIMIT 1;
        """;
    }

    @Override
    public GrupoTerapeutico create(GrupoTerapeutico grupoTerapeutico) {
        final String CREATE = createGrupoTerapeuticoSQL();

        return jdbi.withHandle(handle -> handle.createUpdate(CREATE)
                .bindBean(grupoTerapeutico)
                .executeAndReturnGeneratedKeys()
                .mapToBean(GrupoTerapeutico.class)
                .first());
    }

    @Override
    public List<GrupoTerapeutico> create(List<GrupoTerapeutico> grupoTerapeuticos) {
        final List<GrupoTerapeutico> list = new ArrayList<>();
        for (int i = 0; i <= grupoTerapeuticos.size(); i++){
            GrupoTerapeutico grupoTerapeutico = create(grupoTerapeuticos.get(i));
            list.add(grupoTerapeutico);
        }
        return list;
    }

    @Override
    public GrupoTerapeutico update(GrupoTerapeutico grupoTerapeutico) {
        return null;
    }

    @Override
    public List<GrupoTerapeutico> update(List<GrupoTerapeutico> grupoTerapeuticos) {
        return null;
    }

    // procurar o grupo pelo id dele mesmo
//    @Override
//    public GrupoTerapeutico findById(UUID uidGrupoTerapeutico) {
//        final String SELECT = selectGrupoTerapeuticoSQL();
//
//        return jdbi.withHandle(handle -> handle.createQuery(SELECT)
//                .)
//
//    }

    @Override
    public List<GrupoTerapeutico> findById(List<UUID> ids) {
        return null;
    }

    @Override
    public List<GrupoTerapeutico> findAll() {
        return null;
    }

    @Override
    public List<GrupoTerapeutico> findByFuncionario(UUID uidFuncionario) {
        return null;
    }

    @Override
    public List<GrupoTerapeutico> findByFicha(UUID idFicha) {
        return null;
    }

    @Override
    public void delete(List<UUID> uuids) {

    }

    @Override
    public GrupoTerapeutico addFuncionario(UUID uidFuncionario, UUID uidGrupoTerapeutico) {
        return null;
    }

    @Override
    public GrupoTerapeutico addFicha(UUID uidFicha, UUID uidGrupoTerapeutico) {
        return null;
    }

    @Override
    public void delete(UUID uidGrupoTerapeutico) {

    }
}
