package es.uah.ismael.fbm.peliculasClient.service;

import es.uah.ismael.fbm.peliculasClient.model.Usuario;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IUsuarioService {

    Page<Usuario> buscarTodos(Pageable pageable);

    Usuario buscarUsuarioPorId(Integer id);

    Usuario buscarUsuarioPorNombre(String nombre);

    Usuario buscarUsuarioPorCorreo(String correo);

    Usuario loginNombre(String nombre, String clave);

    Usuario loginCorreo(String correo, String clave);

    void guardarUsuario(Usuario usuario);

    void eliminarUsuario(Integer id);

}
