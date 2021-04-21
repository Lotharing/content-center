package top.lothar.contentcenter.util;

import io.jsonwebtoken.*;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

import javax.crypto.spec.SecretKeySpec;
import javax.xml.bind.DatatypeConverter;
import java.security.Key;
import java.util.Date;
import java.util.Map;

/**
 * <h2>JWT 加密 解密</h2>
 *
 * 第一段
 *
 *  {"alg":"HS256"} 加密算法
 *
 * 第二段
 *  iss：发行人
 *  exp：到期时间
 *  sub：主题
 *  aud：用户
 *  nbf：在此之前不可用
 *  iat：发布时间
 *  jti：JWT ID用于标识该JWT
 *  除以上默认字段外，我们还可以自定义私有字段
 *  {"id":"1","exp":1620126286,"iat":1618916686,"username":"LT"}
 *
 * @author zhaolutong
 */
@Slf4j
@RequiredArgsConstructor
@SuppressWarnings("WeakerAccess")
@Component
public class JwtOperator {
    /**
     * 秘钥默认
     */
    @Value("${secret:aaabbbcccdddeeefffggghhhiiijjjkkklllmmmnnnooopppqqqrrrsssttt}")
    private String secret;
    /**
     * 有效期，单位秒
     * - 默认2周
     */
    @Value("${expire-time-in-second:1209600}")
    private Long expirationTimeInSecond;

    /**
     * 从token中获取claim
     *
     * @param token token
     * @return claim {包含用户信息, 过期时间等}
     */
    public Claims getClaimsFromToken(String token) {
        try {
            return Jwts.parser()
                .setSigningKey(DatatypeConverter.parseBase64Binary(secret))
                .parseClaimsJws(token)
                .getBody();
        } catch (ExpiredJwtException | UnsupportedJwtException | MalformedJwtException | IllegalArgumentException e) {
            log.error("token解析错误", e);
            throw new IllegalArgumentException("Token invalided.");
        }
    }

    /**
     * 获取token的过期时间
     *
     * @param token token
     * @return 过期时间
     */
    public Date getExpirationDateFromToken(String token) {
        return getClaimsFromToken(token)
            .getExpiration();
    }

    /**
     * 判断token是否过期
     *
     * @param token token
     * @return 已过期返回true，未过期返回false
     */
    private Boolean isTokenExpired(String token) {
        Date expiration = getExpirationDateFromToken(token);
        return expiration.before(new Date());
    }

    /**
     * 判断token是否非法
     *
     * @param token token
     * @return 未过期返回true，否则返回false
     */
    public Boolean validateToken(String token) {
        return !isTokenExpired(token);
    }

    /**
     * 计算token的过期时间
     *
     * @return 过期时间
     */
    public Date getExpirationTime() {
        return new Date(System.currentTimeMillis() + this.expirationTimeInSecond * 1000);
    }


    /**
     * 为指定用户生成token
     *
     * @param claims 用户信息 {@link Map 包含用户信息}
     * @return token
     */
    public String generateToken(Map<String, Object> claims) {
        Date createdTime = new Date();
        Date expirationTime = this.getExpirationTime();
        // 签名使用的算法
        SignatureAlgorithm signatureAlgorithm = SignatureAlgorithm.HS512;
        // 对设置的密钥设置
        byte[] apiKeySecretBytes = DatatypeConverter.parseBase64Binary(secret);
        Key signingKey = new SecretKeySpec(apiKeySecretBytes, signatureAlgorithm.getJcaName());
        return Jwts.builder()
            .setClaims(claims)
            // 发行时间
            .setIssuedAt(createdTime)
            .setExpiration(expirationTime)
            // 你也可以改用你喜欢的算法
            // 支持的算法详见：https://github.com/jwtk/jjwt#features
            .signWith(SignatureAlgorithm.HS256, signingKey)
            .compact();
    }

}