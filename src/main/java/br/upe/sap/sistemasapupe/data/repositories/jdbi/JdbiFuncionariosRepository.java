package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Estagiario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Tecnico;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import jakarta.persistence.EntityNotFoundException;
import org.jdbi.v3.core.Jdbi;
import org.jdbi.v3.core.mapper.reflect.BeanMapper;
import org.springframework.stereotype.Repository;
import java.util.List;
import java.util.UUID;

@Repository
public class JdbiFuncionariosRepository implements FuncionarioRepository {

    Jdbi jdbi;

    public JdbiFuncionariosRepository(Jdbi jdbi) {
        this.jdbi = jdbi;
    }


    // CREATE
    private String createFuncionarioSQL(boolean isTecnico) {
        return """
            INSERT INTO funcionarios (nome, sobrenome, email, senha, is_tecnico, url_imagem, is_ativo) VALUES
                (:nome, :sobrenome, :email, :senha, %s, :urlImagem, :ativo)
                RETURNING *
            """.formatted(isTecnico);
    }

    @Override
    public Estagiario createEstagiario(Estagiario estagiario) {
        final String CREATE = createFuncionarioSQL(false);

        return jdbi.withHandle(handle -> handle.createUpdate(CREATE)
            .bindBean(estagiario)
            .executeAndReturnGeneratedKeys()
            .mapToBean(Estagiario.class)
            .first());
    }

    @Override
    public Tecnico createTecnico(Tecnico tecnico) {
        final String CREATE = createFuncionarioSQL(true);

        return jdbi.withHandle(handle -> handle.createUpdate(CREATE)
            .bindBean(tecnico)
            .executeAndReturnGeneratedKeys()
            .mapToBean(Tecnico.class))
            .first();
    }

    @Override
    public Funcionario create(Funcionario funcionario) {
        return funcionario instanceof Tecnico ? createTecnico((Tecnico) funcionario) :
                                                createEstagiario((Estagiario) funcionario);
    }

    @Override
    public List<Funcionario> create(List<Funcionario> funcionarios) {
        return List.of();
    }


    // UPDATE
    @Override
    public Estagiario updateSupervisao(UUID uidEstagiario, UUID uidSupervisor) {
        return null;
    }

    @Override
    public Funcionario updateAtivo(UUID uidFuncionario, boolean isAtivo) {
        return null;
    }

    @Override
    public Funcionario update(Funcionario funcionario) {
        return null;
    }

    @Override
    public List<Funcionario> update(List<Funcionario> funcionarios) {
        return List.of();
    }


    // READ
    @Override
    public Funcionario findById(UUID id) {
        final String QUERY = """
            SELECT uid, id, nome, sobrenome, email, senha, url_imagem, is_tecnico, is_ativo FROM funcionarios
                WHERE uid = :uid LIMIT 1;
        """;

        Funcionario funcionario = jdbi.withHandle(handle -> handle
            .createUpdate(QUERY)
            .executeAndReturnGeneratedKeys()
            .map((rs, ctx) -> rs.getBoolean("is_Tecnico") ?
                BeanMapper.of(Tecnico.class).map(rs, ctx) :
                BeanMapper.of(Estagiario.class).map(rs, ctx)
            ).first());

        if (funcionario instanceof Estagiario estagiario) {
            estagiario.setSupervisor(findSupervisor(estagiario.getUid()));
        }

        return funcionario;
    }

    @Override
    public List<Funcionario> findAll() {
        return List.of();
    }

    @Override
    public List<Funcionario> findById(List<UUID> ids) {
        return List.of();
    }

    @Override
    public List<Estagiario> findSupervisionados(UUID uidTecnico) {
        return List.of();
    }

    public Tecnico findSupervisor(UUID uidEstagiario) {
        final String QUERY = """
           WITH supervisao AS (
               SELECT id_estagiario, id_supervisor FROM supervisoes
                   WHERE id_estagiario = :uid LIMIT 1
           )
           SELECT id_estagiario, sp.id, sp.uid, sp.nome, sp.sobrenome, sp.email, sp.senha,
               sp.url_imagem, sp.is_tecnico, sp.is_ativo FROM supervisao INNER JOIN
                   (SELECT * FROM funcionarios) sp ON id = id_supervisor
           """;

        return jdbi.withHandle(handle -> handle.createQuery(QUERY))
            .map((rs, ctx) -> {
               if (rs.getInt("id_estagiario") == 0)
                   throw new EntityNotFoundException("Não foi possível identificar um " +
                                                     "estagiário com o uid: " + uidEstagiario);
               return BeanMapper.of(Tecnico.class).map(rs, ctx);
            }).first();
    }

    @Override
    public List<Funcionario> findFuncionariosAtivos() {
        return List.of();
    }

    @Override
    public List<Tecnico> findTecnicos() {
        return List.of();
    }


    // DELETE
    @Override
    public void delete(UUID id) {

    }

    @Override
    public void delete(List<UUID> uuids) {

    }


}
