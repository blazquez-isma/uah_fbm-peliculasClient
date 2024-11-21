package es.uah.ismael.fbm.peliculasClient.service;

import es.uah.ismael.fbm.peliculasClient.model.Pelicula;
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
public class PeliculasServiceImpl implements IPeliculaService{

    @Autowired
    RestTemplate template;

    String url = "http://localhost:8001/peliculas";

    @Override
    public Page<Pelicula> buscarTodas(Pageable pageable) {
        Pelicula[] peliculas = template.getForObject(url, Pelicula[].class);
        List<Pelicula> listaPeliculas = peliculas != null ? Arrays.asList(peliculas) : new ArrayList<>();
        return PageUtil.paginate(listaPeliculas, pageable);
    }

    @Override
    public Pelicula buscarPeliculaPorId(Integer idPelicula) {
        return template.getForObject(url + "/" + idPelicula, Pelicula.class);
    }

    @Override
    public Page<Pelicula> buscarPeliculasPorTitulo(String titulo, Pageable pageable) {
        Pelicula[] peliculas = template.getForObject(url + "/titulo/" + titulo, Pelicula[].class);
        List<Pelicula> listaPeliculas = peliculas != null ? Arrays.asList(peliculas) : new ArrayList<>();
        return PageUtil.paginate(listaPeliculas, pageable);
    }

    @Override
    public Page<Pelicula> buscarPeliculasPorAnio(Integer anio1, Integer anio2, Pageable pageable) {
        Pelicula[] peliculas = template.getForObject(url + "/anio/" + anio1 + "/" + anio2, Pelicula[].class);
        List<Pelicula> listaPeliculas = peliculas != null ? Arrays.asList(peliculas) : new ArrayList<>();
        return PageUtil.paginate(listaPeliculas, pageable);
    }

    @Override
    public Page<Pelicula> buscarPeliculasPorGenero(String genero, Pageable pageable) {
        Pelicula[] peliculas = template.getForObject(url + "/genero/" + genero, Pelicula[].class);
        List<Pelicula> listaPeliculas = peliculas != null ? Arrays.asList(peliculas) : new ArrayList<>();
        return PageUtil.paginate(listaPeliculas, pageable);
    }

    @Override
    public Page<Pelicula> buscarPeliculasPorDireccion(String direccion, Pageable pageable) {
        Pelicula[] peliculas = template.getForObject(url + "/direccion/" + direccion, Pelicula[].class);
        List<Pelicula> listaPeliculas = peliculas != null ? Arrays.asList(peliculas) : new ArrayList<>();
        return PageUtil.paginate(listaPeliculas, pageable);
    }

    @Override
    public Page<Pelicula> buscarPeliculasPorActor(Integer idActor, Pageable pageable) {
        Pelicula[] peliculas = template.getForObject(url + "/actor/" + idActor, Pelicula[].class);
        List<Pelicula> listaPeliculas = peliculas != null ? Arrays.asList(peliculas) : new ArrayList<>();
        return PageUtil.paginate(listaPeliculas, pageable);
    }

    @Override
    public void guardarPelicula(Pelicula pelicula) {
        if(pelicula.getIdPelicula() != null && pelicula.getIdPelicula() > 0) {
            template.put(url, pelicula);
        } else {
            pelicula.setIdPelicula(0);
            template.postForObject(url, pelicula, Void.class);
        }
    }

    @Override
    public void eliminarPelicula(Integer idPelicula) {
        template.delete(url + "/" + idPelicula);
    }

    @Override
    public void asignarActor(Integer idPelicula, Integer idActor) {
        template.getForObject(url + "/asignar/" + idPelicula + "/" + idActor, Void.class);
    }

    @Override
    public void desasignarActor(Integer idPelicula, Integer idActor) {
        template.getForObject(url + "/desasignar/" + idPelicula + "/" + idActor, Void.class);
    }
}
