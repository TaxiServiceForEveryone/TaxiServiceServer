package ua.kpi.mobiledev.web.security.jwtBasedAuthentication;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.Jws;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.web.security.JwtAuthenticationToken;
import ua.kpi.mobiledev.web.security.model.UserContext;
import ua.kpi.mobiledev.web.security.token.RawAccessJwtToken;

import java.util.List;
import java.util.stream.Collectors;

@Component("jwtProvider")
@SuppressWarnings("unchecked")
public class JwtAuthenticationProvider implements AuthenticationProvider {

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        RawAccessJwtToken rawAccessToken = (RawAccessJwtToken) authentication.getCredentials();
        Jws<Claims> jwsClaims = rawAccessToken.parseClaims("stub"); //TODO: change logic of signing key
        Claims claims = jwsClaims.getBody();

        return new JwtAuthenticationToken(mapToContext(claims), toGrantedAuthorities(getRoles(claims)));
    }

    private UserContext mapToContext(Claims claims) {
        String subject = claims.getSubject();
        Integer userId = claims.get("userId", Integer.class);
        User.UserType userType = User.UserType.valueOf(claims.get("userType", String.class));

        return new UserContext(userId, subject, userType);
    }

    private List<String> getRoles(Claims claims) {
        return claims.get("scopes", List.class);
    }

    private List<GrantedAuthority> toGrantedAuthorities(List<String> stringAuthorities) {
        return stringAuthorities.stream()
                .map(SimpleGrantedAuthority::new)
                .collect(Collectors.toList());
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (JwtAuthenticationToken.class.isAssignableFrom(authentication));
    }
}