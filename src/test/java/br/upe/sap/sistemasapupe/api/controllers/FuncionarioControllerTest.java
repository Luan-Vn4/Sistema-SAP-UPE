package br.upe.sap.sistemasapupe.api.controllers;

import br.upe.sap.sistemasapupe.api.dtos.funcionarios.FuncionarioDTO;
import br.upe.sap.sistemasapupe.configuration.DataSourceTestConfiguration;
import br.upe.sap.sistemasapupe.data.model.funcionarios.Funcionario;
import br.upe.sap.sistemasapupe.security.authentication.dtos.login.AuthenticationDTO;
import br.upe.sap.sistemasapupe.security.authentication.dtos.login.LoginResponseDTO;
import br.upe.sap.sistemasapupe.security.authentication.dtos.registration.RegisterEstagiarioDTO;
import br.upe.sap.sistemasapupe.security.authentication.dtos.registration.RegisterTecnicoDTO;
import org.jdbi.v3.core.Jdbi;
import org.junit.jupiter.api.*;
import org.junit.jupiter.api.extension.ExtendWith;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.client.TestRestTemplate;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.*;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.TestPropertySource;
import org.springframework.test.context.junit.jupiter.SpringExtension;

import java.util.List;
import java.util.UUID;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {DataSourceTestConfiguration.class})
@ComponentScan(basePackages = "br.upe.sap.sistemasapupe")
@TestPropertySource(properties =
    {"sap.security.jwt.key=1234"})
@ActiveProfiles("Test")
public class FuncionarioControllerTest {

    private final Logger log = LoggerFactory.getLogger(this.getClass());

    @Autowired
    TestRestTemplate restTemplate;

    @Autowired
    @Qualifier("jdbiTest")
    Jdbi jdbi;

    private static LoginResponseDTO userInfo;

    @BeforeAll
    public static void setUp(@Autowired TestRestTemplate restTemplate) {
        Logger log = LoggerFactory.getLogger(FuncionarioController.class);

        var register = new RegisterTecnicoDTO("Pedrin", "Doido", "frifa@gmail.com", "1234", "www.com");
        var login = new AuthenticationDTO("frifa@gmail.com", "1234");


        var response = restTemplate.postForEntity(
                "/api/v1/authentication/register-tecnico", register, void.class);
        var response2 = restTemplate.postForEntity(
                "/api/v1/authentication/login", login, LoginResponseDTO.class);

        log.info("Usuário registrado com as seguintes informações: {}", response2.getBody());
        Assertions.assertTrue(response2.getStatusCode().is2xxSuccessful());
        Assertions.assertNotNull(response2.getBody());
        FuncionarioControllerTest.userInfo = response2.getBody();
    }

    @AfterEach
    public void clean() {
        jdbi.useHandle(handle -> handle.execute("DELETE FROM funcionarios WHERE uid <> ?",
            userInfo.funcionario().id()));
    }

    private void registerTecnico(RegisterTecnicoDTO dto) {
        var response = restTemplate.postForEntity(
                "/api/v1/authentication/register-tecnico", dto, void.class);
        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful(),
                "Não foi possível cadastrar o técnico");
    }

    private void registerEstagiario(RegisterEstagiarioDTO dto, String token) {
        HttpHeaders headers = new HttpHeaders();
        headers.add("Authorization", "Bearer " + token);

        HttpEntity<RegisterEstagiarioDTO> entity = new HttpEntity<>(dto, headers);

        var response = restTemplate.postForEntity(
                "/api/v1/authentication/register-estagiario", entity, void.class);
        Assertions.assertTrue(response.getStatusCode().is2xxSuccessful(),
                "Não foi possível cadastrar o estagiário");
    }

    private RegisterEstagiarioDTO getRegistroEstagiario() {
        return RegisterEstagiarioDTO.builder()
            .nome("Junin").sobrenome("Do Grau")
            .email("pop100@gmail.com").senha("123")
            .urlImagem("abc").uidTecnico(userInfo.funcionario().id())
            .build();
    }

    private String getToken() {
        return userInfo.token().token();
    }

    // findAll()
    @Test
    @DisplayName("Dados uids, quando procurar por todos, retornar todos os funcionários")
    public void whenSearchAll_thenAllFuncionarios() {
        var estagiario = getRegistroEstagiario();
        registerEstagiario(estagiario, getToken());

        HttpEntity<?> request = createAuthorizedHttpEntity(null);

        var response =  restTemplate.exchange("/api/v1/funcionarios/many/all", HttpMethod.GET, request,
            new ParameterizedTypeReference<List<FuncionarioDTO>>() {});

        Assertions.assertNotNull(response, "Resposta nula");
        Assertions.assertNotNull(response.getBody(), "Corpo vazio");
        logFuncionariosRetornados(response.getBody());

        List<FuncionarioDTO> body = response.getBody();
        Assertions.assertFalse(body.isEmpty(), "Corpo da requisição vazio");
        Assertions.assertEquals(userInfo.funcionario(),body.get(0),
                        "Informações do técnico divergem");
        assertEqualsFuncionarioWithoutIdsAndSenha(estagiario.toEstagiario(null),
                                                  body.get(1).toFuncionario());
    }

    private void logFuncionariosRetornados(List<FuncionarioDTO> funcionarios) {
        log.info("Funcionários retornados: {}", funcionarios);
    }

    private void assertEqualsFuncionarioWithoutIdsAndSenha(Funcionario expected, Funcionario actual) {
        Assertions.assertEquals(expected.getNome(), actual.getNome(), "Nome diferente");
        Assertions.assertEquals(expected.getSobrenome(), actual.getSobrenome(), "sobrenome diferente");
        Assertions.assertEquals(expected.getEmail(), actual.getEmail(), "email diferente");
        Assertions.assertEquals(expected.getUrlImagem(), actual.getUrlImagem(), "url da imagem diferente");
    }

    private <T> HttpEntity<T> createAuthorizedHttpEntity(T body) {
        HttpHeaders header = new HttpHeaders();
        header.add("Authorization", "Bearer " + userInfo.token().token());
        return new HttpEntity<>(body, header);
    }

    @Test
    public void givenUids_whenSearchByUids_thenReturnCorrespondingFuncionarios() {
        registerEstagiario(getRegistroEstagiario(), getToken());

        HttpEntity<Void> request1 = createAuthorizedHttpEntity(null);
        List<FuncionarioDTO> funcionarios = restTemplate.exchange("/api/v1/funcionarios/many/all",
                HttpMethod.GET, request1, new ParameterizedTypeReference<List<FuncionarioDTO>>() {}).getBody();

        List<UUID> uuids = funcionarios.stream().map(FuncionarioDTO::id).toList();

        HttpEntity<?> request2 = createAuthorizedHttpEntity(uuids);
        List<FuncionarioDTO> funcionarios2 = restTemplate.exchange("/api/v1/funcionarios/many/uids",
                HttpMethod.POST, request2, new ParameterizedTypeReference<List<FuncionarioDTO>>() {}).getBody();

        logFuncionariosRetornados(funcionarios2);
        List<UUID> returned = funcionarios2.stream().map(FuncionarioDTO::id).toList();

        Assertions.assertEquals(uuids, returned, "Uuids retornados são diferentes");
    }

}
