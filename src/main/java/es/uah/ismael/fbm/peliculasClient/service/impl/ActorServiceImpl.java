package es.uah.ismael.fbm.peliculasClient.service.impl;

import es.uah.ismael.fbm.peliculasClient.model.Actor;
import es.uah.ismael.fbm.peliculasClient.paginator.PageUtil;
import es.uah.ismael.fbm.peliculasClient.service.IActorService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ActorServiceImpl implements IActorService {

    @Autowired
    RestTemplate template;

    @Value("${api.peliculas.actores.url}")
    private String url;

    @Override
    public Page<Actor> buscarTodos(Pageable pageable) {
        Actor[] actores = template.getForObject(url, Actor[].class);
        List<Actor> listaActores = actores != null ? Arrays.asList(actores) : new ArrayList<>();
        return PageUtil.paginate(listaActores, pageable);
    }

    @Override
    public Actor buscarActorPorId(Integer id) {
        return template.getForObject(url + "/" + id, Actor.class);
    }

    @Override
    public List<Actor> buscarActoresPorIds(List<Integer> ids) {
        Actor[] actores = template.postForObject(url + "/ids", ids, Actor[].class);
        return actores != null ? Arrays.asList(actores) : new ArrayList<>();
    }

    @Override
    public Actor buscarActorPorNombreCompleto(String nombreCompleto) {
        return template.getForObject(url + "/nombreCompleto/" + nombreCompleto, Actor.class);
    }

    @Override
    public List<Actor> buscarActoresPorNombre(String nombre) {
        Actor[] actores = template.getForObject(url + "/nombre/" + nombre, Actor[].class);
        return actores != null ? Arrays.asList(actores) : new ArrayList<>();
    }

    @Override
    public Page<Actor> buscarActoresPorNombre(String nombre, Pageable pageable) {
        Actor[] actores = template.getForObject(url + "/nombre/" + nombre, Actor[].class);
        List<Actor> listaActores = actores != null ? Arrays.asList(actores) : new ArrayList<>();
        return PageUtil.paginate(listaActores, pageable);
    }

    @Override
    public Page<Actor> buscarActoresPorFechaNacimiento(LocalDate fecha1, LocalDate fecha2, Pageable pageable){
        Actor[] actores = template.getForObject(url + "/fechaNacimiento/" + fecha1 + "/" + fecha2, Actor[].class);
        List<Actor> listaActores = actores != null ? Arrays.asList(actores) : new ArrayList<>();
        return PageUtil.paginate(listaActores, pageable);
    }

    @Override
    public Page<Actor> buscarActoresPorPelicula(Integer idPelicula, Pageable pageable) {
        Actor[] actores = template.getForObject(url + "/pelicula/" + idPelicula, Actor[].class);
        List<Actor> listaActores = actores != null ? Arrays.asList(actores) : new ArrayList<>();
        return PageUtil.paginate(listaActores, pageable);
    }

    @Override
    public void guardarActor(Actor actor) {
        if(actor.getIdActor() != null && actor.getIdActor() > 0) {
            template.put(url, actor);
        } else {
            actor.setIdActor(0);
            template.postForObject(url, actor, Void.class);
        }
    }

    @Override
    public void eliminarActor(Integer id) {
        template.delete(url + "/" + id);
    }

}
