package es.uah.ismael.fbm.peliculasClient.service.impl;

import es.uah.ismael.fbm.peliculasClient.model.Pelicula;
import es.uah.ismael.fbm.peliculasClient.paginator.PageUtil;
import es.uah.ismael.fbm.peliculasClient.service.IPeliculaService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

@Service
public class PeliculasServiceImpl implements IPeliculaService {

    Logger log = org.slf4j.LoggerFactory.getLogger(PeliculasServiceImpl.class);

    @Autowired
    RestTemplate template;

    @Value("${api.peliculas.peliculas.url}")
    private String url;

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
    public Pelicula buscarPeliculaPorTituloCompleto(String titulo) {
        return template.getForObject(url + "/tituloCompleto/" + titulo, Pelicula.class);
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
    public String buscarTituloPeliculaPorId(Integer idPelicula) {
        return template.getForObject(url + "/titulobyid/" + idPelicula, String.class);
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

}
