package ua.kpi.mobiledev.web.security.loginBasedAuthentication;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.stereotype.Component;
import ua.kpi.mobiledev.domain.User;
import ua.kpi.mobiledev.service.UserService;
import ua.kpi.mobiledev.web.security.model.UserContext;

import java.util.Collection;
import java.util.Objects;

@Component(value = "userCredentialsProvider")
public class UsernamePasswordAuthenticationProvider implements AuthenticationProvider {

//    private final BCryptPasswordEncoder passwordEncoder;

    private final UserService userService;

    private final UserDetailsService userDetailsService;

    @Autowired
    public UsernamePasswordAuthenticationProvider(/*BCryptPasswordEncoder passwordEncoder,*/ UserService userService, UserDetailsService userDetailsService) {
//        this.passwordEncoder = passwordEncoder;
        this.userService = userService;
        this.userDetailsService = userDetailsService;
    }

    @Override
    public Authentication authenticate(Authentication authentication) throws AuthenticationException {
        Objects.requireNonNull(authentication, "No authentication data provided");
        String userName = (String) authentication.getPrincipal();
        final UserDetails userDetails = getByUserName(userName);
        final String password = (String) authentication.getCredentials();

        if (invalidPassword(password, userDetails.getPassword())) {
            throw new BadCredentialsException("Authentication Failed. Username or Password not valid.");
        }
        User user = getFullInfoByUserName(userName);
        UserContext userContext = new UserContext(user.getId(), userDetails.getUsername(), user.getUserType());
        return new UsernamePasswordAuthenticationToken(userContext, null, getRoles(userDetails)); //no password
    }

    @SuppressWarnings("unchecked")
    private Collection<GrantedAuthority> getRoles(UserDetails userDetails) {
        return (Collection<GrantedAuthority>) userDetails.getAuthorities();
    }

    private boolean invalidPassword(String password, String storedPassword) {
//        return !passwordEncoder.matches(password, encodedPassword);
        return !password.equals(storedPassword);
    }

    @Override
    public boolean supports(Class<?> authentication) {
        return (UsernamePasswordAuthenticationToken.class.isAssignableFrom(authentication));
    }

    private UserDetails getByUserName(String userName) {
        return userDetailsService.loadUserByUsername(userName);
    }

    private User getFullInfoByUserName(String userName) {
        return userService.getByUsername(userName);
    }
}