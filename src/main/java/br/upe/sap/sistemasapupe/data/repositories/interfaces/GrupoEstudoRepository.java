package br.upe.sap.sistemasapupe.data.repositories.interfaces;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoEstudo;

import java.util.UUID;

public interface GrupoEstudoRepository extends BasicRepository<GrupoEstudo, UUID> {

    GrupoEstudo findById(int idGrupoEstudo);

    GrupoEstudo findByFuncionario(UUID idFuncionario);

    void deleteGrupoEstudo(GrupoEstudo grupoEstudo);

    Funcionario addFuncionario(Funcionario funcionario);

    void deleteFuncionario(UUID idFuncionario);

}
