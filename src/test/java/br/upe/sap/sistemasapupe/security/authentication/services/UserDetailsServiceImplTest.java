package br.upe.sap.sistemasapupe.security.authentication.services;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Estagiario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Tecnico;
import br.upe.sap.sistemasapupe.data.repositories.interfaces.FuncionarioRepository;
import br.upe.sap.sistemasapupe.security.authentication.dtos.login.AuthenticationDTO;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.UUID;

@ExtendWith(MockitoExtension.class)
public class UserDetailsServiceImplTest {

    // Mocks
    @Mock
    FuncionarioRepository funcionarioRepository;

    @InjectMocks
    UserDetailsServiceImpl authService;

    // Dummys
    private static Tecnico dummyTecnico() {
        return Tecnico.tecnicoBuilder()
            .id(1).uid(UUID.randomUUID())
            .nome("Carlinhos").sobrenome("Carlos")
            .email("carlos@gmail.com").senha("123456")
            .isAtivo(true).urlImagem("www.com").build();
    }

    private static Estagiario dummyEstagiario() {
        return Estagiario.estagiarioBuilder()
            .id(2).uid(UUID.randomUUID())
            .nome("Luan").sobrenome("Vilaça")
            .email("luan@gmail.com").senha("1234")
            .isAtivo(true).urlImagem("www.com").build();
    }

    private static AuthenticationDTO dummyAuthDTO() {
        return new AuthenticationDTO("carlos@gmail.com", "1234");
    }

    @Test
    @DisplayName("Dado email, quando carregar usuário por username (email), retorne UserDetails")
    public void givenEmail_whenLoadUserByUsername_thenReturnUserDetails() {
        Funcionario dummy = dummyEstagiario();

        Mockito.when(funcionarioRepository.findByEmail(Mockito.anyString())).thenReturn(dummy);

        UserDetails userDetails = authService.loadUserByUsername("carlos@gmail.com");

        Assertions.assertEquals(userDetails.getUsername(), dummy.getEmail());
        Assertions.assertEquals(userDetails.getPassword(), dummy.getSenha());
        Assertions.assertEquals(userDetails.getAuthorities(), dummy.getAuthorities());
    }

    @Test
    @DisplayName("Dado um email não registrado, quando carregar usuário por username (email), " +
                 "lance a exceção: UserNameNotFoundException")
    public void givenUnregisteredEmail_whenLoadUserByUsername_thenThrowException() {
        Mockito.when(funcionarioRepository.findByEmail(Mockito.anyString())).thenReturn(null);

        Assertions.assertThrows(UsernameNotFoundException.class, () ->
                authService.loadUserByUsername("carlos@gmail.com"));
    }

}
