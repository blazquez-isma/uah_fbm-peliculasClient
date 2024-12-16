package es.uah.ismael.fbm.peliculasClient.service.impl;

import es.uah.ismael.fbm.peliculasClient.model.Usuario;
import es.uah.ismael.fbm.peliculasClient.paginator.PageUtil;
import es.uah.ismael.fbm.peliculasClient.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class UsuarioServiceImpl implements IUsuarioService {

    @Autowired
    RestTemplate template;

    String url = "http://localhost:8090/api/usuarios/usuarios";

    @Override
    public Page<Usuario> buscarTodos(Pageable pageable) {
        Usuario[] usuarios = template.getForObject(url, Usuario[].class);
        List<Usuario> listaUsuarios = usuarios != null ? Arrays.asList(usuarios) : new ArrayList<>();
        System.out.println("Usuarios: " + listaUsuarios.size());
        return PageUtil.paginate(listaUsuarios, pageable);
    }

    @Override
    public Usuario buscarUsuarioPorId(Integer idUsuario) {
        return template.getForObject(url + "/" + idUsuario, Usuario.class);
    }

    @Override
    public Usuario buscarUsuarioPorNombre(String nombre) {
        return template.getForObject(url + "/nombre/" + nombre, Usuario.class);
    }

    @Override
    public Usuario buscarUsuarioPorCorreo(String correo) {
        return template.getForObject(url + "/correo/" + correo, Usuario.class);
    }

    @Override
    public void guardarUsuario(Usuario usuario) {
        if (usuario.getIdUsuario() != null && usuario.getIdUsuario() > 0) {
            System.out.println("Usuario a actualizar: " + usuario);
            template.put(url, usuario);
        } else {
            usuario.setIdUsuario(0);
            System.out.println("Usuario a guardar: " + usuario);
            template.postForObject(url, usuario, String.class);
        }
    }

    @Override
    public void eliminarUsuario(Integer idUsuario) {
        template.delete(url + "/" + idUsuario);
    }
}
