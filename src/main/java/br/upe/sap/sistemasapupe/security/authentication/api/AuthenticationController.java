package br.upe.sap.sistemasapupe.security.authentication.api;

import br.upe.sap.sistemasapupe.security.authentication.dtos.login.AuthenticationDTO;
import br.upe.sap.sistemasapupe.security.authentication.dtos.login.LoginResponseDTO;
import br.upe.sap.sistemasapupe.security.authentication.dtos.login.TokenDTO;
import br.upe.sap.sistemasapupe.security.authentication.dtos.registration.RegisterEstagiarioDTO;
import br.upe.sap.sistemasapupe.security.authentication.dtos.registration.RegisterTecnicoDTO;
import br.upe.sap.sistemasapupe.security.authentication.services.AuthenticationService;
import lombok.AllArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

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

    @GetMapping("/validate-token")
    public ResponseEntity<TokenDTO> validateToken(@RequestParam String token) {
        return ResponseEntity.ok(authService.validateToken(token));
    }

    @PostMapping("/register-tecnico")
    public ResponseEntity<?> registerTecnico(@RequestBody RegisterTecnicoDTO registerDTO) {
        this.authService.registerTecnico(registerDTO);
        return ResponseEntity.status(HttpStatus.CREATED.value()).build();
    }

    @PostMapping("/register-estagiario")
    public ResponseEntity<?> registerEstagiario(@RequestBody RegisterEstagiarioDTO registerDTO) {
        this.authService.registerEstagiario(registerDTO);
        return ResponseEntity.status(HttpStatus.CREATED.value()).build();
    }

}
