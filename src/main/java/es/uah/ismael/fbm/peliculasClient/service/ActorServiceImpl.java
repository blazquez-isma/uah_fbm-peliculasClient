package es.uah.ismael.fbm.peliculasClient.service;

import es.uah.ismael.fbm.peliculasClient.model.Actor;
import es.uah.ismael.fbm.peliculasClient.paginator.PageUtil;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class ActorServiceImpl implements IActorService {

    @Autowired
    RestTemplate template;

    String url = "http://localhost:8001/actores";

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
    public Page<Actor> buscarActoresPorNombre(String nombre, Pageable pageable) {
        Actor[] actores = template.getForObject(url + "/nombre/" + nombre, Actor[].class);
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

    @Override
    public void asignarPelicula(Integer idActor, Integer idPelicula) {
        template.getForObject(url + "/asignar/" + idActor + "/" + idPelicula, Void.class);
    }

    @Override
    public void desasignarPelicula(Integer idActor, Integer idPelicula) {
        template.getForObject(url + "/desasignar/" + idActor + "/" + idPelicula, Void.class);
    }
}
