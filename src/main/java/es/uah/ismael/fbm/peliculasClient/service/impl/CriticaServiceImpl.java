package es.uah.ismael.fbm.peliculasClient.service.impl;

import es.uah.ismael.fbm.peliculasClient.model.Critica;
import es.uah.ismael.fbm.peliculasClient.paginator.PageUtil;
import es.uah.ismael.fbm.peliculasClient.service.ICriticaService;
import es.uah.ismael.fbm.peliculasClient.service.IPeliculaService;
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
public class CriticaServiceImpl implements ICriticaService {

    @Autowired
    RestTemplate template;

    @Autowired
    IPeliculaService peliculaService;

    @Value("${api.usuarios.criticas.url}")
    private String url;

    @Override
    public Page<Critica> buscarTodas(Pageable pageable) {
        Critica[] criticas = template.getForObject(url, Critica[].class);
        List<Critica> listaCriticas = criticas != null ? Arrays.asList(criticas) : new ArrayList<>();
        return PageUtil.paginate(listaCriticas, pageable);
    }

    @Override
    public Page<Critica> buscarCriticasPorPelicula(Integer idPelicula, Pageable pageable) {
        Critica[] criticas = template.getForObject(url + "/pelicula/" + idPelicula, Critica[].class);
        List<Critica> listaCriticas = criticas != null ? Arrays.asList(criticas) : new ArrayList<>();
        return PageUtil.paginate(listaCriticas, pageable);
    }

    @Override
    public Page<Critica> buscarCriticasPorUsuario(Integer idUsuario, Pageable pageable) {
        Critica[] criticas = template.getForObject(url + "/usuario/" + idUsuario, Critica[].class);
        List<Critica> listaCriticas = criticas != null ? Arrays.asList(criticas) : new ArrayList<>();
        listaCriticas.sort((c1, c2) -> c2.getFecha().compareTo(c1.getFecha()));
        return PageUtil.paginate(listaCriticas, pageable);
    }

    @Override
    public Critica buscarCriticaPorId(Integer id) {
        return template.getForObject(url + "/" + id, Critica.class);
    }

    @Override
    public void guardarCritica(Critica critica) {
        if (critica.getIdCritica() != null && critica.getIdCritica() > 0) {
            template.put(url, critica);
        } else {
            critica.setIdCritica(0);
            template.postForObject(url, critica, String.class);
        }
    }

    @Override
    public void eliminarCritica(Integer id) {
        template.delete(url + "/" + id);
    }

    public Double calcularNotaMediaDePelicula(Integer idPelicula) {
        return template.getForObject(url + "/pelicula/" + idPelicula + "/notaMedia", Double.class);
    }

}
