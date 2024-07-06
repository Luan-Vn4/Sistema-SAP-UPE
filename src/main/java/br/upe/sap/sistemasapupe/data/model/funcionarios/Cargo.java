package br.upe.sap.sistemasapupe.data.model.funcionarios;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter @AllArgsConstructor
public enum Cargo {

    ESTAGIARIO("Estagiário", "ESTAGIARIO"), TECNICO("Técnico", "TECNICO");

    private final String label;

    // Utilizado na autenticação
    private final String role;

}
