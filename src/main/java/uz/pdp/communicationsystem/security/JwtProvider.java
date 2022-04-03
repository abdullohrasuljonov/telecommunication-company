package uz.pdp.communicationsystem.security;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import org.springframework.stereotype.Component;
import uz.pdp.communicationsystem.entity.Role;

import java.util.Date;
import java.util.Set;

@Component
public class JwtProvider {

    private final String SECRET_KEY = "SecretKey";
    private static final long expireTime = 1000 * 60 * 60 * 24;

    public String generateToken(String username, Set<Role> role) {
        Date EXPIRE_TIME = new Date(System.currentTimeMillis() + JwtProvider.expireTime);
        return Jwts
                .builder()
                .setSubject(username)
                .setIssuedAt(new Date())
                .setExpiration(EXPIRE_TIME)
                .claim("roles", role)
                .signWith(SignatureAlgorithm.HS512, SECRET_KEY)
                .compact();
    }

    public String getUsernameFromToken(String token) {
        try {
            return Jwts.parser()
                    .setSigningKey(SECRET_KEY)
                    .parseClaimsJws(token)
                    .getBody()
                    .getSubject();
        } catch (Exception e) {
            return null;
        }
    }
}
