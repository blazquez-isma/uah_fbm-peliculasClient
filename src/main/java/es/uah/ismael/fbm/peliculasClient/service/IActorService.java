package es.uah.ismael.fbm.peliculasClient.service;

import es.uah.ismael.fbm.peliculasClient.model.Actor;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.time.LocalDate;
import java.util.List;

public interface IActorService {

    Page<Actor> buscarTodos(Pageable pageable);

    Actor buscarActorPorId(Integer id);

    List<Actor> buscarActoresPorIds(List<Integer> ids);

    Actor buscarActorPorNombreCompleto(String nombre);

    Page<Actor> buscarActoresPorNombre(String nombre, Pageable pageable);

    List<Actor> buscarActoresPorNombre(String nombre);

    Page<Actor> buscarActoresPorFechaNacimiento(LocalDate fecha1, LocalDate fecha2, Pageable pageable);

    Page<Actor> buscarActoresPorPelicula(Integer idPelicula, Pageable pageable);

    void guardarActor(Actor actor);

    void eliminarActor(Integer id);

}
