package br.upe.sap.sistemasapupe.security.authentication.dtos.registration;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Estagiario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Tecnico;
import java.util.UUID;

public record RegisterEstagiarioDTO(String nome, String sobrenome, String email, String senha,
                                    String urlImagem, UUID uidTecnico) {

    public Estagiario toEstagiario(Tecnico supervisor) {
        return Estagiario.estagiarioBuilder()
            .nome(nome).sobrenome(sobrenome).email(email)
            .senha(senha).urlImagem(urlImagem).isAtivo(true).build();
    }

}
