package br.upe.sap.sistemasapupe.data.model.enums;

public enum TipoSala {
    GRUPO("GRUPO"),
    INDIVIDUAL("INDIVIDUAL"),
    INFANTIL("INFANTIL"),
    AUDITORIO("AUDITÓRIO"),
    TRIAGEM("TRIAGEM");

    private String label;

    TipoSala(String label) {
        this.label = label;
    }

    public String getLabel() {
        return this.label;
    }

}
