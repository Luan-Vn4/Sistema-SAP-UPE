package br.upe.sap.sistemasapupe.data.model.enums;

public enum TipoSala {
    GRUPO("Grupo"),
    INDIVIDUAL("Individual"),
    INFANTIL("Infantil"),
    AUDITORIO("Auditório"),
    TRIAGEM("Triagem");

    private String label;

    TipoSala(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

}
