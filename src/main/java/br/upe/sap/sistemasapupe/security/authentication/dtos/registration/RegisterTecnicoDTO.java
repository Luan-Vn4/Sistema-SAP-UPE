package br.upe.sap.sistemasapupe.security.authentication.dtos.registration;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Tecnico;

public record RegisterTecnicoDTO(String nome, String sobrenome, String email, String senha,
                                 String urlImagem) {

    public Tecnico toTecnico() {
        return Tecnico.tecnicoBuilder()
            .nome(nome).sobrenome(sobrenome).email(email)
            .senha(senha).urlImagem(urlImagem).isAtivo(true).build();
    }

}
