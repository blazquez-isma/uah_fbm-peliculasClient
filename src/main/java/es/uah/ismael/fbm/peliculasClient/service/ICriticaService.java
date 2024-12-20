package es.uah.ismael.fbm.peliculasClient.service;

import es.uah.ismael.fbm.peliculasClient.model.Critica;
import es.uah.ismael.fbm.peliculasClient.model.CriticaPelicula;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;

public interface ICriticaService {

    Page<Critica> buscarTodas(Pageable pageable);

    Page<Critica> buscarCriticasPorPelicula(Integer idPelicula, Pageable pageable);

    Page<Critica> buscarCriticasPorUsuario(Integer idUsuario, Pageable pageable);

    Critica buscarCriticaPorId(Integer id);

    void guardarCritica(Critica critica);

    void eliminarCritica(Integer id);

    Double calcularNotaMediaDePelicula(Integer idPelicula);
}
