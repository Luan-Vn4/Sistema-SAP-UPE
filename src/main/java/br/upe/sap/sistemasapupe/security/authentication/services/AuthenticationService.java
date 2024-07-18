package br.upe.sap.sistemasapupe.security.authentication.services;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Estagiario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Tecnico;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import br.upe.sap.sistemasapupe.security.authentication.dtos.login.AuthenticationDTO;
import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.security.authentication.dtos.login.LoginResponseDTO;
import br.upe.sap.sistemasapupe.security.authentication.dtos.login.TokenDTO;
import br.upe.sap.sistemasapupe.security.authentication.dtos.registration.RegisterEstagiarioDTO;
import br.upe.sap.sistemasapupe.security.authentication.dtos.registration.RegisterTecnicoDTO;
import br.upe.sap.sistemasapupe.security.authentication.jwt.TokenService;
import lombok.AllArgsConstructor;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.UUID;

@AllArgsConstructor
@Service
public class AuthenticationService {

    // Dependências
    FuncionarioRepository funcionarioRepository;

    AuthenticationManager authManager;

    TokenService tokenService;


    // Serviços
    public LoginResponseDTO authenticate(AuthenticationDTO authDTO) {
        var userNamePassword = new UsernamePasswordAuthenticationToken(authDTO.email(), authDTO.senha());

        authManager.authenticate(userNamePassword);

        UUID uid = funcionarioRepository.findByEmail(authDTO.email()).getUid();
        TokenDTO tokenDTO = tokenService.generateToken(uid.toString());
        FuncionarioDTO funcionarioDTO = FuncionarioDTO.from(funcionarioRepository.findByEmail(authDTO.email()));

        return new LoginResponseDTO(funcionarioDTO, tokenDTO);
    }

    public TokenDTO validateToken(String token) {
        if (token.contains("Bearer ")) {
            token = token.replace("Bearer ", "");
        }
        return tokenService.validateToken(token);
    }

    public void registerTecnico(RegisterTecnicoDTO registerDTO) {
        Tecnico tecnico = registerDTO.toTecnico();
        tecnico.setSenha(encodePassword(tecnico.getPassword()));

        this.funcionarioRepository.create(tecnico);
    }

    private String encodePassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    public void registerEstagiario(RegisterEstagiarioDTO registerDTO) {
        Integer idAutor = funcionarioRepository.findIds(
                registerDTO.uidTecnico()).get(registerDTO.uidTecnico());

        Tecnico tecnico = (Tecnico) this.funcionarioRepository.findById(idAutor);
        Estagiario estagiario = registerDTO.toEstagiario(tecnico);
        estagiario.setSupervisor(tecnico);
        estagiario.setSenha(encodePassword(estagiario.getPassword()));

        this.funcionarioRepository.create(estagiario);
    }

}
