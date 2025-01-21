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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Component;

import java.util.ArrayList;
import java.util.List;

@Component
public class CustomAuthenticationProvider implements AuthenticationProvider {

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private PasswordEncoder passwordEncoder;

    public CustomAuthenticationProvider() {
        super();
    }

    @Override
    public Authentication authenticate(final Authentication authentication) throws AuthenticationException {

        final String usuario = authentication.getName();
        String password = authentication.getCredentials().toString();

        System.out.println("Password: " + password);
//        Usuario usuarioLogueado = usuarioService.loginCorreo(usuario, passwordEncoder.encode(password));
        Usuario usuarioLogueado = usuarioService.buscarUsuarioPorCorreo(usuario);
        System.out.println("Usuario logueado correo: " + usuarioLogueado);
        if(usuarioLogueado == null) {
//            usuarioLogueado = usuarioService.loginNombre(usuario, passwordEncoder.encode(password));
            usuarioLogueado = usuarioService.buscarUsuarioPorNombre(usuario);
        }
        System.out.println("Usuario logueado: " + usuarioLogueado);
        if (usuarioLogueado != null && passwordEncoder.matches(password, usuarioLogueado.getClave())) {
            final List<GrantedAuthority> grantedAuths = new ArrayList<>();
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
