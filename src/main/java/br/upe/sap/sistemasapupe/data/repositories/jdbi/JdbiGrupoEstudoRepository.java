package br.upe.sap.sistemasapupe.data.repositories.jdbi;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoEstudo;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.GrupoEstudoRepository;
import org.springframework.stereotype.Repository;

import java.util.List;
import java.util.UUID;

@Repository
public class JdbiGrupoEstudoRepository implements GrupoEstudoRepository {
    @Override
    public GrupoEstudo create(GrupoEstudo grupoEstudo) {
        return null;
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
