package es.uah.ismael.fbm.peliculasClient.service.impl;

import es.uah.ismael.fbm.peliculasClient.model.Rol;
import es.uah.ismael.fbm.peliculasClient.service.IRolService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class RolServiceImpl implements IRolService {

    @Autowired
    RestTemplate template;

    @Value("${api.usuarios.roles.url}")
    private String url;

    @Override
    public List<Rol> buscarTodos() {
        Rol[] roles = template.getForObject(url, Rol[].class);
        return roles != null ? Arrays.asList(roles) : new ArrayList<>();
    }

    @Override
    public Rol buscarRolPorId(Integer idRol) {
        return template.getForObject(url + "/" + idRol, Rol.class);
    }

    @Override
    public void guardarRol(Rol rol) {
        if (rol.getIdRol() != null && rol.getIdRol() > 0) {
            template.put(url, rol);
        } else {
            rol.setIdRol(0);
            template.postForObject(url, rol, Void.class);
        }
    }

    @Override
    public void eliminarRol(Integer idRol) {
        template.delete(url + "/" + idRol);
    }
}
