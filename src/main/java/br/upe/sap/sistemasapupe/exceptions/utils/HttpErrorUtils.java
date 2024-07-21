package br.upe.sap.sistemasapupe.exceptions.utils;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.gson.JsonParseException;
import jakarta.annotation.Nullable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.web.client.HttpClientErrorException;
import org.springframework.web.client.HttpClientErrorException.NotFound;
import org.springframework.web.client.HttpClientErrorException.UnprocessableEntity;
import org.springframework.web.client.HttpClientErrorException.BadRequest;
import org.springframework.web.client.HttpClientErrorException.Unauthorized;

import java.nio.charset.Charset;


/**
 * Fornece uma interface mais amigável para acessar as exceções presentes em {@link HttpClientErrorException}.
 * Essas exceções basicamente são representações dos códigos HTTP possíveis
 */
public class HttpErrorUtils {

    private HttpErrorUtils() {}

    private static HttpStatusCode httpStatusFrom(HttpStatus httpStatus) {
        return HttpStatus.valueOf(httpStatus.value());
    }

    private static byte[] toJsonBytes(Object object) throws JsonProcessingException {
        return new ObjectMapper().writeValueAsBytes(object);
    }

    /**
     * Método geral para a criação dos {@link HttpClientErrorException}. Com base no {@link HttpStatus} passado
     * será lançada uma exceção que correspondam àquele. Caso queira saber quais são as exceções disponíveis dê
     * uma olha na lista nas exceções internas presentes na classe {@link HttpClientErrorException}
     * @param status status http que será gerado
     * @param message mensagem descrevendo o problema
     * @param body corpo da requisição que causou o problema
     * @param headers cabeçalho da requisição que causou o problema
     * @return
     */
    public static HttpClientErrorException httpException(HttpStatus status, String message,
                                                         @Nullable Object body, @Nullable HttpHeaders headers) {
        try {
            return HttpClientErrorException.create(
                httpStatusFrom(status), message, headers,
                toJsonBytes(body), Charset.defaultCharset());
        } catch (JsonProcessingException e) {
            throw new JsonParseException(e.getMessage(), e);
        }
    }

    public static NotFound notFoundException(String message, @Nullable Object body,
                                             @Nullable HttpHeaders headers) {
        return (NotFound) httpException(HttpStatus.NOT_FOUND, message,body,headers);
    }

    public static UnprocessableEntity unprocessableEntityException(String message, @Nullable Object body,
                                                                   @Nullable HttpHeaders headers) {
        return (UnprocessableEntity) httpException(HttpStatus.UNPROCESSABLE_ENTITY, message,body,headers);
    }

    public static BadRequest badRequestException(String message, @Nullable Object body,
                                                 @Nullable HttpHeaders headers) {
        return (BadRequest) httpException(HttpStatus.BAD_REQUEST, message,body,headers);
    }

    public static Unauthorized unauthorizedException(String message, @Nullable Object body,
                                                     @Nullable HttpHeaders headers) {
        return (Unauthorized) httpException(HttpStatus.UNAUTHORIZED, message,body,headers);
    }

}
