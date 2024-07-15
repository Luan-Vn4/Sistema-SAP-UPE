package br.upe.sap.sistemasapupe.security.authentication.dtos.login;

import br.upe.sap.sistemasapupe.api.dtos.FuncionarioDTO;

public record LoginResponseDTO(FuncionarioDTO funcionario, TokenDTO token)  {
}