package com.lftech.sqlapi.spring;

import com.fasterxml.jackson.databind.module.SimpleModule;
import com.lftech.sqlapi.Utils.JsonUtils;
import com.lftech.sqlapi.pojo.User;
import com.lftech.sqlapi.pojo.UserDeserializer;
import com.nimbusds.jose.*;
import com.nimbusds.jose.crypto.RSASSASigner;
import com.nimbusds.jose.crypto.RSASSAVerifier;
import com.nimbusds.jwt.JWTClaimsSet;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.context.properties.ConfigurationProperties;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.stereotype.Component;
import org.springframework.util.Base64Utils;

import javax.annotation.PostConstruct;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.interfaces.RSAPublicKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.security.spec.X509EncodedKeySpec;
import java.text.ParseException;
import java.util.Date;
import java.util.Map;

@Component
@Slf4j
public class Jwt {

    public static final String TOKEN_PREFIX = "Bearer ";
    public static final int TOKEN_PREFIX_LENGTH = 7;
    public static String AUTH_AUDIENCE = "auth";
    public static String VERIFY_EMAIL_AUDIENCE = "verify-email";
    public static String FORGOT_PASSWORD_AUDIENCE = "forgot-password";
    public static String CHANGE_EMAIL_AUDIENCE = "change-email";
    public static GateConfig gateConfig;
    static JWSSigner signer;
    static JWSVerifier verifier;
    private static String PRIVATE_KEY;
    private static String PUBLIC_KEY;

    private Jwt(GateConfig gateConfig) {
        Jwt.PRIVATE_KEY = gateConfig.rsa_private_key;
        Jwt.PUBLIC_KEY = gateConfig.rsa_public_key;
        Jwt.gateConfig = gateConfig;
        this.signer = new RSASSASigner(getPrivateKey());
        this.verifier = new RSASSAVerifier(getPublicKey());
    }

    public static RSAPublicKey getPublicKey() {
        byte[] keyBytes = getKeyBytes(PUBLIC_KEY);
        X509EncodedKeySpec keySpec = new X509EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return (RSAPublicKey) keyFactory.generatePublic(keySpec);
        } catch (Exception e) {
            log.error("JWT.getPublicKey", e);
        }
        return null;
    }

    public static PrivateKey getPrivateKey() {
        byte[] keyBytes = getKeyBytes(PRIVATE_KEY);
        PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(keyBytes);
        try {
            KeyFactory keyFactory = KeyFactory.getInstance("RSA");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            log.error("JWT.getPrivateKey", e);
        }
        return null;
    }

    private static byte[] getKeyBytes(String pkcs8Pem) {
        pkcs8Pem = pkcs8Pem.replace("-----BEGIN PRIVATE KEY-----", "");
        pkcs8Pem = pkcs8Pem.replace("-----END PRIVATE KEY-----", "");
        pkcs8Pem = pkcs8Pem.replace("-----BEGIN PUBLIC KEY-----", "");
        pkcs8Pem = pkcs8Pem.replace("-----END PUBLIC KEY-----", "");
        pkcs8Pem = pkcs8Pem.replaceAll("\\s+", "");

        // Base64 decode the result
        return Base64Utils.decodeFromString(pkcs8Pem);
    }

    protected static Payload createPayload(String aud, String subject, Map<String, Object> claimMap) {

        JWTClaimsSet.Builder builder = new JWTClaimsSet.Builder();

        builder.issuer("lftech.com")
                .issueTime(new Date())
                .expirationTime(new Date(System.currentTimeMillis() + Jwt.gateConfig.getExpirationMillis()))
                .audience(aud)
                .subject(subject);

        claimMap.forEach(builder::claim);

        JWTClaimsSet claims = builder.build();

        return new Payload(claims.toJSONObject());
    }

    private static String createToken(String aud, User user) {

        Payload payload = createPayload(aud, User.class.getCanonicalName(), JsonUtils.obj2Map(user));
        JWSObject jwsObject = new JWSObject(new JWSHeader(JWSAlgorithm.RS256), payload);
        try {
            jwsObject.sign(signer);
        } catch (JOSEException e) {
            throw new RuntimeException(e);
        }

        // To serialize to compact form, produces something like
        // https://jwt.io/#debugger-io?token=eyJhbGciOiJSUzI1NiJ9.eyJhdWQiOiJhdXRoIiwic3ViIjoiY29tLmtjaHguZ2F0ZXdheS5wb2pvLlVzZXIiLCJyb2xlcyI6WyJST0xFX0FETUlOIiwiUk9MRV9VU0VSIl0sImlzcyI6ImtjaHguY29tIiwiaWQiOjIsImV4cCI6MTY0NTcxNzUwMywiaWF0IjoxNjQ0MjE3NTAzLCJlbmFibGVkIjp0cnVlLCJ1c2VybmFtZSI6ImFkbWluQGtjaHguY29tIn0.BHVFFOa5cHnITvbPRMZ5xS22wLKvHfi06FPXl6SS8X6cDVhGAapoM-y8I3aFXQnNEfY8eSEwJU2bR_H1HpJ2a2xK_RXLAf5xvbSGjxCxzPJ5pomPS-ae13DlD6YNjTmbym0-b7q1UVqUEQ3PL_d4jJ49h93sR3RZBwR1WD4yspzJ5hlDHGSwvaE247YzuTJpEvZDACgE60g7YsgzmIAUW8caQmnlN7pPXrh2-Fv9DmXnwOHYwswUbv0OUgiwiP-vi6EbZxJTdPjdm25hWHMzPgLQ0KNx50hgt7TEETYoRGx2SEppAgOh54FDDghYDdu3v4BX0LGS3hpQlDei8215oA&publicKey=-----BEGIN%20PUBLIC%20KEY-----%0AMIIBIjANBgkqhkiG9w0BAQEFAAOCAQ8AMIIBCgKCAQEAr5n3RPZj8DMgAGWMgUNB%0AHs8Pci%2BqypAw2Krv%2FrPk9S%2BtxGAfm4Ql78Gwb6PQvdk1wpni4d8AANcYjSn7XOjA%0AkYdmebCY09Mns0HcjTFq7fmeonrArBCOXbG%2BwyC74rRRt9lYrptNiEmFCwlWzK6Y%0AmzeYx29DElPh5KbuF%2FCaHjThujaUntkM93af2h48d1TQ6xxP3frS%2BENZHKZiixxx%0A2FsdT5%2FEGWmEbZWqplia0vAxoVNahoDSTsfk4KaRXLVCylbAm1lNKcXVC3Ld0q0f%0A60oHbyyJA1hZl3eosRzcMj%2FAmFk%2FbvjGo%2FZoNnnZe1tsCT8T67r%2F5a0KgAl87fpI%0AJQIDAQAB%0A-----END%20PUBLIC%20KEY-----
        return jwsObject.serialize();
    }

    public static String createAutToken(User user) {
        return TOKEN_PREFIX + createToken(AUTH_AUDIENCE, user);
    }

    private static JWTClaimsSet parseToken(String token) {
        JWSObject jwsObject;
        try {
            jwsObject = JWSObject.parse(token);
            if (jwsObject.verify(verifier))
                return JWTClaimsSet.parse(jwsObject.getPayload().toJSONObject());

        } catch (JOSEException | ParseException e) {

            throw new BadCredentialsException(e.getMessage());
        }

        throw new BadCredentialsException("JWS verification failed!");
    }

    public static JWTClaimsSet parseToken(String token, String audience) {

        JWTClaimsSet claims = parseToken(token);
        if (audience == null ||
                !claims.getAudience().contains(audience)) {
            throw new BadCredentialsException("登录信息有误");
        }

        long expirationTime = claims.getExpirationTime().getTime();
        long currentTime = System.currentTimeMillis();

        if (currentTime >= expirationTime) {
            throw new BadCredentialsException("登录信息已失效");
        }
        return claims;
    }

    public static User parseAutToken(String token) {
        Map<String, Object> claims = parseToken(token, AUTH_AUDIENCE).toJSONObject();
        return JsonUtils.json2Object(claims, User.class);
    }

    @PostConstruct
    public void postConstruct() {
        SimpleModule module = new SimpleModule();
        module.addDeserializer(User.class, new UserDeserializer());
        JsonUtils.objectMapper.registerModule(module);
    }

    @Component
    @ConfigurationProperties(prefix = "sqlapi")
    @Data
    public static class GateConfig {
        private String rsa_private_key;
        private String rsa_public_key;
        private long expirationMillis;
    }
}
