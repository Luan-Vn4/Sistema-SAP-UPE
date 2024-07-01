package br.upe.sap.sistemasapupe.data.repositories.interfaces;

import br.upe.sap.sistemasapupe.data.model.pacientes.Ficha;

import java.util.UUID;

public interface FichaRepository extends BasicRepository<Ficha, UUID> {

    Ficha findByFuncionario(UUID uidFuncionario);

}
