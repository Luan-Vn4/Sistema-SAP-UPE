package br.upe.sap.sistemasapupe.security.authentication.jwt;

import br.upe.sap.sistemasapupe.data.model.funcionarios.Cargo;
import br.upe.sap.sistemasapupe.security.authentication.dtos.login.TokenDTO;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@ExtendWith({MockitoExtension.class, SpringExtension.class})
@ComponentScan(basePackageClasses = {TokenService.class})
public class TokenServiceTest {

    @Mock
    UserDetails userDetails;

    @Spy
    @InjectMocks
    TokenService tokenService;

    private UserDetails dummyUserDetails() {
        return User.builder()
            .username("Pedrin")
            .password("1234")
            .roles(Cargo.TECNICO.getRole())
            .build();
    }

    @Test
    public void givenUserDetails_whenGenerateToken_thenReturnTokenDTO() {
        Mockito.when(userDetails.getUsername()).thenReturn("pedrin@gmail.com");

        Mockito.when(tokenService.getIssuer()).thenReturn("SAP");
        Mockito.when(tokenService.getSecretKey()).thenReturn("1234");
        Mockito.when(tokenService.getTokenType()).thenReturn("Bearer ");
        Mockito.when(tokenService.getExpirationTimeHours()).thenReturn(1);

        TokenDTO tokenDTO = tokenService.generateToken(userDetails.getUsername());

        String subject = JWT
            .require(Algorithm.HMAC256(tokenService.getSecretKey()))
            .withIssuer("SAP")
            .build()
            .verify(tokenDTO.token())
            .getSubject();

        Assertions.assertNotNull(tokenDTO);
        Assertions.assertEquals("pedrin@gmail.com", subject);
        System.out.println(tokenDTO.token());
    }

    @Test
    public void givenValidToken_whenValidateToken_thenReturnTokenDTO() {
        Mockito.when(userDetails.getUsername()).thenReturn("pedrin@gmail.com");

        Mockito.when(tokenService.getIssuer()).thenReturn("SAP");
        Mockito.when(tokenService.getSecretKey()).thenReturn("1234");
        Mockito.when(tokenService.getTokenType()).thenReturn("Bearer ");
        Mockito.when(tokenService.getExpirationTimeHours()).thenReturn(1);

        TokenDTO tokenDTO = tokenService.generateToken(userDetails.getUsername());

        TokenDTO token = Assertions.assertDoesNotThrow(() -> tokenService.validateToken(tokenDTO.token()));
        System.out.println(token);

    }

}
