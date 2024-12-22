package es.uah.ismael.fbm.peliculasClient.config;

import es.uah.ismael.fbm.peliculasClient.model.Rol;
import es.uah.ismael.fbm.peliculasClient.model.Usuario;
import es.uah.ismael.fbm.peliculasClient.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.authentication.AuthenticationProvider;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.AuthenticationException;
import org.springframework.security.core.GrantedAuthority;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private IUsuarioService usuarioService;

    public CustomAuthenticationProvider() {
        super();
    }

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {

        final String usuario = authentication.getName();
        String password = authentication.getCredentials().toString();

        Usuario usuarioLogueado = usuarioService.loginCorreo(usuario, password);
        if(usuarioLogueado == null) {
            usuarioLogueado = usuarioService.loginNombre(usuario, password);
        }
        if (usuarioLogueado != null) {
            final List<GrantedAuthority> grantedAuths = new ArrayList<GrantedAuthority>();
            for (Rol rol : usuarioLogueado.getRoles()) {
                grantedAuths.add(new SimpleGrantedAuthority(rol.getAuthority()));
            }
            final UserDetails principal = new User(usuario, password, grantedAuths);
            return new UsernamePasswordAuthenticationToken(principal, password, grantedAuths);
        }
        return null;
    }

    @SuppressWarnings("rawtypes")
    @Override
    public boolean supports(final Class authentication) {
        return authentication.equals(UsernamePasswordAuthenticationToken.class);
    }

}
