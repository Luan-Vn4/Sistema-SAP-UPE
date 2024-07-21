package br.upe.sap.sistemasapupe.security.authentication.services;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Estagiario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
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
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;
import javax.persistence.EntityNotFoundException;
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

    public LoginResponseDTO validateToken(String token) {
        TokenDTO tokenDTO = tokenService.validateToken(extractToken(token));

        UUID userUid = UUID.fromString(tokenDTO.subject());
        Integer id = funcionarioRepository.findIds(userUid).get(userUid);

        FuncionarioDTO funcionario = FuncionarioDTO.from(funcionarioRepository.findById(id));

        return new LoginResponseDTO(funcionario, tokenDTO);
    }

    private String extractToken(String token) {
        if (token.contains("Bearer ")) {
            return token.replace("Bearer ", "");
        }
        return token;
    }

    public void registerTecnico(RegisterTecnicoDTO registerDTO) {
        Tecnico tecnico = registerDTO.toTecnico();
        tecnico.setSenha(encodePassword(tecnico.getPassword()));

        this.funcionarioRepository.create(tecnico);
    }

    private String encodePassword(String password) {
        return new BCryptPasswordEncoder().encode(password);
    }

    /**
     * Registra um estagiário com base no {@link RegisterEstagiarioDTO} passado
     * @param registerDTO {@link RegisterTecnicoDTO} com as informações do estagiário
     * @throws EntityNotFoundException caso não existe um técnico com o uid recebido do DTO
     */
    public void registerEstagiario(RegisterEstagiarioDTO registerDTO) {
        Integer idAutor = funcionarioRepository.findIds(
                registerDTO.uidTecnico()).get(registerDTO.uidTecnico());

        if (idAutor == null) throw new EntityNotFoundException(
                "Não há um técnico com o UID: " + registerDTO.uidTecnico());

        Tecnico tecnico = (Tecnico) this.funcionarioRepository.findById(idAutor);
        Estagiario estagiario = registerDTO.toEstagiario(tecnico);
        estagiario.setSupervisor(tecnico);
        estagiario.setSenha(encodePassword(estagiario.getPassword()));


        this.funcionarioRepository.create(estagiario);
    }

    public void changePassword(String token, String oldPassword, String newPassword) {
        TokenDTO tokenDTO = tokenService.validateToken(extractToken(token));

        UUID userUid = UUID.fromString(tokenDTO.subject());
        int userId = funcionarioRepository.findIds(userUid).get(userUid);

        Funcionario funcionario = funcionarioRepository.findById(userId);

        if (!passwordsMatch(oldPassword, funcionario.getPassword())) throw new BadCredentialsException(
                "A senha fornecida está incorreta");

        funcionarioRepository.updatePassword(userId, encodePassword(newPassword));
    }

    private boolean passwordsMatch(String decodedPassword, String encodedPassword) {
        return new BCryptPasswordEncoder().matches(decodedPassword, encodedPassword);
    }

}
