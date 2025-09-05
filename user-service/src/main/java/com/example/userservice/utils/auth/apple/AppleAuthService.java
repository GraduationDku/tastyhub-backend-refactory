package com.example.userservice.utils.auth.apple;

import com.fasterxml.jackson.core.JsonProcessingException;
import com.fasterxml.jackson.databind.ObjectMapper;
import io.jsonwebtoken.JwsHeader;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.ClassPathResource;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.util.LinkedMultiValueMap;
import org.springframework.util.MultiValueMap;
import org.springframework.web.client.RestTemplate;

import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Paths;
import java.security.KeyFactory;
import java.security.PrivateKey;
import java.security.spec.PKCS8EncodedKeySpec;
import java.util.Base64;
import java.util.Date;
import java.util.Map;

@Component
@RequiredArgsConstructor
public class AppleAuthService {

    private final AppleProperties appleProperties;
    private final RestTemplate restTemplate;
    private final ObjectMapper objectMapper;

    /**
     * 클라이언트로부터 받은 code를 Apple 서버에 보내 access_token 및 id_token을 받습니다.
     * @param code 클라이언트에서 Apple 로그인 성공 후 받은 authorization_code
     * @return Apple로부터 받은 토큰 정보
     */
    public AppleTokenResponse getAppleToken(String code) {
        String clientSecret = createClientSecret();
        HttpHeaders headers = new HttpHeaders();
        headers.setContentType(MediaType.APPLICATION_FORM_URLENCODED);

        MultiValueMap<String, String> body = new LinkedMultiValueMap<>();
        body.add("client_id", appleProperties.getClientId());
        body.add("client_secret", clientSecret);
        body.add("code", code);
        body.add("grant_type", "authorization_code");

        HttpEntity<MultiValueMap<String, String>> request = new HttpEntity<>(body, headers);

        ResponseEntity<AppleTokenResponse> response = restTemplate.postForEntity(
                "https://appleid.apple.com/auth/token", request, AppleTokenResponse.class);

        return response.getBody();
    }

    /**
     * id_token을 디코딩하여 Apple 사용자 정보를 가져옵니다.
     * @param idToken Apple로부터 받은 id_token
     * @return Apple 사용자 정보
     */
    public AppleUserInfo getAppleUserInfo(String idToken) {
        try {
            String[] chunks = idToken.split("\\.");
            String payload = new String(Base64.getUrlDecoder().decode(chunks[1]), StandardCharsets.UTF_8);
            return objectMapper.readValue(payload, AppleUserInfo.class);
        } catch (JsonProcessingException e) {
            throw new RuntimeException("Failed to parse Apple user info", e);
        }
    }

    /**
     * Apple 서버에 요청을 보내기 위한 client_secret(JWT)을 생성합니다.
     * @return 생성된 client_secret
     */
    public String createClientSecret() {
        try {
            Date now = new Date();
            return Jwts.builder()
                    .setHeaderParam(JwsHeader.KEY_ID, appleProperties.getKeyId())
                    .setIssuer(appleProperties.getTeamId())
                    .setAudience("https://appleid.apple.com")
                    .setSubject(appleProperties.getClientId())
                    .setIssuedAt(now)
                    .setExpiration(new Date(now.getTime() + 3600 * 1000)) // 1시간 유효
                    .signWith(getPrivateKey(), SignatureAlgorithm.ES256)
                    .compact();
        } catch (IOException e) {
            throw new RuntimeException("Failed to create Apple client secret", e);
        }
    }

    /**
     * Apple Developer에서 받은 .p8 비공개 키 파일을 읽어 PrivateKey 객체를 생성합니다.
     * @return PrivateKey 객체
     */
    private PrivateKey getPrivateKey() throws IOException {
        ClassPathResource resource = new ClassPathResource(appleProperties.getKeyPath());
        String privateKeyContent = new String(resource.getInputStream().readAllBytes());

        String key = privateKeyContent
                .replace("-----BEGIN PRIVATE KEY-----", "")
                .replace("-----END PRIVATE KEY-----", "")
                .replaceAll("\\s", "");

        byte[] decodedKey = Base64.getDecoder().decode(key);

        try {
            PKCS8EncodedKeySpec keySpec = new PKCS8EncodedKeySpec(decodedKey);
            KeyFactory keyFactory = KeyFactory.getInstance("EC");
            return keyFactory.generatePrivate(keySpec);
        } catch (Exception e) {
            throw new RuntimeException("Failed to generate private key", e);
        }
    }
}
