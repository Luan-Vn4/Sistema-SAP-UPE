package br.upe.sap.sistemasapupe.security.authentication.dtos.registration;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Estagiario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Tecnico;
import jakarta.validation.constraints.Email;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Size;
import lombok.Builder;

import java.util.UUID;

@Builder
public record RegisterEstagiarioDTO(
        @NotNull
        @Size(max = 50)
        String nome,

        @NotNull
        @Size(max = 50)
        String sobrenome,

        @NotNull
        @Email
        @Size(max = 255)
        String email,

        @NotNull
        @Size(max = 60)
        String senha,

        @NotNull
        @Size(max = 255)
        String urlImagem,

        @NotNull
        UUID uidTecnico) {

    public Estagiario toEstagiario(Tecnico supervisor) {
        return Estagiario.estagiarioBuilder()
            .nome(nome).sobrenome(sobrenome).email(email)
            .senha(senha).urlImagem(urlImagem).isAtivo(true).build();
    }

}
