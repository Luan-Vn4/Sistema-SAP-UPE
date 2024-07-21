package br.upe.sap.sistemasapupe.security.authentication.dtos.registration;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Tecnico;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;

public record RegisterTecnicoDTO(
    @NotNull
    @Size(max = 50)
    String nome,

    @NotNull
    @Size(max = 50)
    String sobrenome,

    @NotNull
    @Size(max = 60)
    String email,

    @NotNull
    @Size(max = 60)
    String senha,

    @NotNull
    @Size(max = 255)
    String urlImagem) {

    public Tecnico toTecnico() {
        return Tecnico.tecnicoBuilder()
            .nome(nome).sobrenome(sobrenome).email(email)
            .senha(senha).urlImagem(urlImagem).isAtivo(true).build();
    }

}
