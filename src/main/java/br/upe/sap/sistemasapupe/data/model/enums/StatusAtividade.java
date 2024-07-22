package br.upe.sap.sistemasapupe.data.model.enums;

public enum StatusAtividade {

    PENDENTE("PENDENTE"),
    APROVADO("APROVADO"),
    REPROVADO("REPROVADO");

    private final String label;

    StatusAtividade(String label) {
        this.label = label;
    }

    public String getLabel() {
        return  this.label;
    }

}

