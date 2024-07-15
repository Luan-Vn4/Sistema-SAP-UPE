package br.upe.sap.sistemasapupe.data.model.funcionarios;

import lombok.Getter;
import org.springframework.security.core.authority.SimpleGrantedAuthority;

@Getter
public enum Cargo {

    ESTAGIARIO("Estagiário", "ESTAGIARIO"),
    TECNICO("Técnico", "TECNICO");

    private final String label;

    // Utilizado na autenticação
    private final String role;

    Cargo(String label, String role) {
        this.label = label;
        this.role = role;
    }

    /**
     * Utilizado quando for necessário criar um {@link SimpleGrantedAuthority}
     * @return <code>this.role</code> com o prefixo <b>"ROLE_"</b>
     */
    public String getPrefixedRole() {
        return "ROLE_" + this.role;
    }

}
