package es.uah.ismael.fbm.peliculasClient.service;

import es.uah.ismael.fbm.peliculasClient.model.Pelicula;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface IPeliculaService {

    Page<Pelicula> buscarTodas(Pageable pageable);

    Pelicula buscarPeliculaPorId(Integer idPelicula);

    Pelicula buscarPeliculaPorTituloCompleto(String titulo);

    Page<Pelicula> buscarPeliculasPorTitulo(String titulo, Pageable pageable);

    Page<Pelicula> buscarPeliculasPorAnio(Integer anio1, Integer anio2, Pageable pageable);

    Page<Pelicula> buscarPeliculasPorGenero(String genero, Pageable pageable);

    Page<Pelicula> buscarPeliculasPorDireccion(String direccion, Pageable pageable);

    Page<Pelicula> buscarPeliculasPorActor(Integer idActor, Pageable pageable);

    String buscarTituloPeliculaPorId(Integer idPelicula);

    void guardarPelicula(Pelicula pelicula);

    void eliminarPelicula(Integer idPelicula);

}
