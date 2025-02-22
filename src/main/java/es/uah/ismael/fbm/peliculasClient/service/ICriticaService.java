package es.uah.ismael.fbm.peliculasClient.service;

import es.uah.ismael.fbm.peliculasClient.model.Critica;
import es.uah.ismael.fbm.peliculasClient.model.CriticaPelicula;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

import java.util.List;

public interface ICriticaService {

    Page<Critica> buscarTodas(Pageable pageable);

    Page<Critica> buscarCriticasPorPelicula(Integer idPelicula, Pageable pageable);

    List<Critica> buscarCriticasPorPelicula(Integer idPelicula);

    Page<Critica> buscarCriticasPorUsuario(Integer idUsuario, Pageable pageable);

    List<Critica> buscarCriticasPorUsuario(Integer idUsuario);

    Critica buscarCriticaPorId(Integer id);

    void guardarCritica(Critica critica);

    void eliminarCritica(Integer id);

    Double calcularNotaMediaDePelicula(Integer idPelicula);
}
