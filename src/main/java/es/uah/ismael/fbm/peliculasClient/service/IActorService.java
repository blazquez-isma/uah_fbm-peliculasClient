package es.uah.ismael.fbm.peliculasClient.service;

import es.uah.ismael.fbm.peliculasClient.model.Actor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface IActorService {

    Page<Actor> buscarTodos(Pageable pageable);

    Actor buscarActorPorId(Integer id);

    Page<Actor> buscarActoresPorNombre(String nombre, Pageable pageable);

    Page<Actor> buscarActoresPorPelicula(Integer idPelicula, Pageable pageable);

    void guardarActor(Actor actor);

    void eliminarActor(Integer id);

    void asignarPelicula(Integer idActor, Integer idPelicula);

    void desasignarPelicula(Integer idActor, Integer idPelicula);
}
