package br.upe.sap.sistemasapupe.security.authentication.api;

import br.upe.sap.sistemasapupe.exceptions.utils.HttpErrorUtils;
import br.upe.sap.sistemasapupe.security.authentication.dtos.login.AuthenticationDTO;
import br.upe.sap.sistemasapupe.security.authentication.dtos.login.LoginResponseDTO;
import br.upe.sap.sistemasapupe.security.authentication.dtos.registration.RegisterEstagiarioDTO;
import br.upe.sap.sistemasapupe.security.authentication.dtos.registration.RegisterTecnicoDTO;
import br.upe.sap.sistemasapupe.security.authentication.services.AuthenticationService;
import jakarta.validation.Valid;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.client.HttpClientErrorException;

import javax.persistence.EntityNotFoundException;

@AllArgsConstructor
@RequestMapping("api/v1/authentication")
@RestController
public class AuthenticationController {

    // Dependências
    private AuthenticationService authService;


    // Endpoints
    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody AuthenticationDTO authDTO) {
        LoginResponseDTO loginResponseDTO = authService.authenticate(authDTO);
        return ResponseEntity.ok().body(loginResponseDTO);
    }

    @GetMapping(value = "/validate-token", params = {"token"})
    public ResponseEntity<LoginResponseDTO> validateToken(@RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        return ResponseEntity.ok(authService.validateToken(token));
    }

    @PostMapping("/register-tecnico")
    public ResponseEntity<?> registerTecnico(@RequestBody RegisterTecnicoDTO registerDTO) {
        this.authService.registerTecnico(registerDTO);
        return ResponseEntity.status(HttpStatus.CREATED.value()).build();
    }

    @PostMapping("/register-estagiario")
    public ResponseEntity<?> registerEstagiario(@Valid @RequestBody RegisterEstagiarioDTO registerDTO) {
        try {
            this.authService.registerEstagiario(registerDTO);
        } catch (EntityNotFoundException exc) {
            throw HttpClientErrorException.create(
                HttpStatusCode.valueOf(HttpStatus.UNPROCESSABLE_ENTITY.value()),
                    exc.getMessage(), null, null, null);
        }
        return ResponseEntity.status(HttpStatus.CREATED.value()).build();
    }

    @PutMapping(value = "/redefine-password", params = {"old-password", "new-password"})
    public ResponseEntity<?> changePassword(@RequestParam(name = "old-password") String oldPassword,
                                              @RequestParam(name = "new-password") String newPassword,
                                              @RequestHeader(HttpHeaders.AUTHORIZATION) String token) {
        try {
            authService.changePassword(token, oldPassword, newPassword);
        } catch (BadCredentialsException e) {
            throw HttpErrorUtils.unauthorizedException(e.getMessage(), null, null);
        }

        return ResponseEntity.ok().build();
    }

}
