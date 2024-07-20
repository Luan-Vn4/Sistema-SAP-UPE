package br.upe.sap.sistemasapupe.data.repositories.interfaces;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.grupos.GrupoEstudo;

import java.util.UUID;

public interface GrupoEstudoRepository extends BasicRepository<GrupoEstudo, Integer> {

    GrupoEstudo findByFuncionario(Integer idFuncionario);


    Funcionario addFuncionario(Integer idFuncionario, Integer idGrupoEstudo);

    void deleteParticipacao(int idParticipante);

}
