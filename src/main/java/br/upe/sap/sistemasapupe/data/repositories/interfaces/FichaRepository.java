package br.upe.sap.sistemasapupe.data.repositories.interfaces;

import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;

public interface FichaRepository extends BasicRepository<Ficha, Integer> {

    Ficha findByFuncionario(Integer idFuncionario);

}
