package com.e_commerce.project.security.jwt;

import com.e_commerce.project.security.services.UserDetailsImpl;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.MalformedJwtException;
import io.jsonwebtoken.UnsupportedJwtException;
import io.jsonwebtoken.io.Decoders;
import io.jsonwebtoken.security.Keys;
import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletRequest;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.ResponseCookie;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;
import org.springframework.web.util.WebUtils;

import javax.crypto.SecretKey;
import java.security.Key;
import java.util.Date;


@Component
public class JwtUtils {

    private static final Logger logger = LoggerFactory.getLogger(JwtUtils.class);

    @Value("${spring.app.expiration}")
    private long expiration;

    @Value("${spring.app.jwtSecretKey}")
    private String jwtSecretKey;

    @Value("${spring.app.jwtCookieName}")
    private String jwtCookie;
//    public String getJwtFromHeader(HttpServletRequest request) {
//
//        String bearerToken = request.getHeader("Authorization");
//        logger.debug("Authorization Header {}", bearerToken);
//        if (bearerToken != null && bearerToken.startsWith("Bearer ")) {
//            return bearerToken.substring(7);
//        }
//
//        return null;
//    }

    public String getJwtFromCookies(HttpServletRequest request){

        Cookie cookie= WebUtils.getCookie(request,jwtCookie);

        if(cookie!=null){
            return cookie.getValue();
        }else{
            return null;
        }
    }

    public ResponseCookie generateJwtFromCookie(UserDetailsImpl userPrincipal){
        String jwt = generateTokenFromUserName(userPrincipal.getUsername());

        return ResponseCookie.from(
                jwtCookie,jwt
        ).maxAge(24*60*60)
                .path("/api")
                .httpOnly(false)
                .build();


    }

    public String generateTokenFromUserName(String  userName) {
        return Jwts.builder()
                .subject(userName)
                .issuedAt(new Date())
                .expiration(new Date(System.currentTimeMillis() + expiration))
                .signWith(key())
                .compact();
    }
//    public String generateTokenFromUserName(UserDetails userDetails) {
//        return Jwts.builder()
//                .subject(userDetails.getUsername())
//                .issuedAt(new Date())
//                .expiration(new Date(System.currentTimeMillis() + expiration))
//                .signWith(key())
//                .compact();
//    }

    public String getUserNameFromJwtToken(String token) {

        return Jwts.parser()
                .verifyWith( key())
                .build().parseSignedClaims(token)
                .getPayload().getSubject();
    }

    public SecretKey key() {
        return Keys.hmacShaKeyFor(
                Decoders.BASE64.decode(jwtSecretKey)

        );
    }

    public boolean validateJwtToken(String authToken){

        try{
            System.out.println("Validate");
            Jwts.parser()
                    .verifyWith((SecretKey) key())
                    .build()
                    .parseSignedClaims(authToken);
            return true;

        } catch (MalformedJwtException e) {
           logger.error("Invalid Jwt token {}",e.getMessage());
        }
        catch (ExpiredJwtException e) {
            logger.error(" Jwt token is expired {}",e.getMessage());

        }catch (UnsupportedJwtException e) {
            logger.error("Jwt token is unsupported {}",e.getMessage());

        }catch (IllegalArgumentException e) {
            logger.error(" Jwt claims string is empty {}",e.getMessage());

        }
return false;
    }
}
