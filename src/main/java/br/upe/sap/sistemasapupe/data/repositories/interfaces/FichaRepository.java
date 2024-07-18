package br.upe.sap.sistemasapupe.data.repositories.interfaces;

import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;

import java.util.List;

public interface FichaRepository extends BasicRepository<Ficha, Integer> {

    List<Ficha> findByFuncionario(Integer idFuncionario);

}
