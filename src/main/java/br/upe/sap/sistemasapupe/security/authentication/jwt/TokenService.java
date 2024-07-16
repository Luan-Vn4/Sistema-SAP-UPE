package br.upe.sap.sistemasapupe.security.authentication.jwt;

import br.upe.sap.sistemasapupe.security.authentication.dtos.login.TokenDTO;
import com.auth0.jwt.JWT;
import com.auth0.jwt.algorithms.Algorithm;
import com.auth0.jwt.exceptions.JWTCreationException;
import com.auth0.jwt.exceptions.JWTVerificationException;
import com.auth0.jwt.exceptions.TokenExpiredException;
import com.auth0.jwt.interfaces.DecodedJWT;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Service;

import java.time.Instant;

@Service
public class TokenService {

    @Value("${sap.security.jwt.key}")
    private String SECRET_KEY;

    @Value("${sap.security.jwt.issuer}")
    private String ISSUER;

    @Value("${sap.security.jwt.expiration-time-hours}")
    private int EXPIRATION_TIME_HOURS;

    private final String TOKEN_TYPE = "Bearer";

    public String getSecretKey() {
        return SECRET_KEY;
    }

    public String getIssuer() {
        return ISSUER;
    }

    public int getExpirationTimeHours() {
        return EXPIRATION_TIME_HOURS;
    }

    public String getTokenType() {
        return TOKEN_TYPE;
    }

    private Algorithm getAlgorithm() {
        return Algorithm.HMAC256(getSecretKey());
    }

    /**
     * Gera um token com o sujeito especificado como o <code>username</code> do {@link UserDetails} fornecido
     * @param subject subject que será inserido no token
     * @return {@link TokenDTO} com as informações do token gerado
     * @throws JWTCreationException caso não seja possível criar o token
     */
    public TokenDTO generateToken(String subject) {
        try {
            Instant currentInstant = getCurrentInstant();
            Instant expirationInstant = getExpirationTime(currentInstant);

            String token = JWT.create()
                    .withIssuer(getIssuer())
                    .withIssuedAt(currentInstant)
                    .withExpiresAt(expirationInstant)
                    .withSubject(subject)
                    .sign(getAlgorithm());

            return new TokenDTO(true, token, getTokenType(), subject, expirationInstant);
        } catch (JWTCreationException exception) {
            throw new JWTCreationException("Não foi possível gerar um token válido", exception);
        }
    }

    /**
     * Valida o token fornecido
     * @param token que deseja verificar
     * @return {@link TokenDTO} com as informações do token retornado
     * @throws TokenExpiredException caso o token esteja expirado
     * @throws JWTVerificationException caso o token seja inválido
     */
    public TokenDTO validateToken(String token) {
        try {
            DecodedJWT decoded = JWT
                .require(getAlgorithm())
                .withIssuer(getIssuer())
                .build()
                .verify(token);

            Instant expiration = decoded.getExpiresAtAsInstant();

            return new TokenDTO(true, decoded.getSubject(), getTokenType(), decoded.getSubject(), expiration);
        } catch (TokenExpiredException exception) {
            throw new JWTVerificationException("Token expirado", exception);
        } catch (JWTVerificationException exception) {
            throw new JWTVerificationException("Token inválido", exception);
        }
    }

    private Instant getCurrentInstant() {
        return Instant.now();
    }

    private Instant getExpirationTime(Instant currentInstant) {
        return currentInstant.plusSeconds((long) getExpirationTimeHours() * 3600);
    }

}
