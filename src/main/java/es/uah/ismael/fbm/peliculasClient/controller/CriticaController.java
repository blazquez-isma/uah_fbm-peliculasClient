package es.uah.ismael.fbm.peliculasClient.controller;

import es.uah.ismael.fbm.peliculasClient.model.Critica;
import es.uah.ismael.fbm.peliculasClient.paginator.PageRender;
import es.uah.ismael.fbm.peliculasClient.service.ICriticaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Optional;

@Controller
@RequestMapping("/ccriticas")
public class CriticaController {

    @Autowired
    private ICriticaService criticaService;

    @RequestMapping("/listado")
    public String listadoCriticas(Model model, @RequestParam(name="page", defaultValue="0") int page) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<Critica> criticas = criticaService.buscarTodas(pageable);
        PageRender<Critica> pageRender = new PageRender<>("/ccriticas/listado", criticas);
        model.addAttribute("titulo", "Listado de críticas");
        model.addAttribute("page", pageRender);
        model.addAttribute("listado", criticaService.buscarTodas(pageable));
        return "criticas/listCriticas";
    }

    @GetMapping("/nueva")
    public String nuevaCritica(Model model) {
        model.addAttribute("titulo", "Nueva crítica");
        model.addAttribute("critica", new Critica());
        return "criticas/formCritica";
    }

    @GetMapping("/editar/{idCritica}")
    public String editarCritica(Model model, @PathVariable(name="idCritica") Integer idCritica) {
        model.addAttribute("titulo", "Editar crítica");
        model.addAttribute("critica", criticaService.buscarCriticaPorId(idCritica));
        return "criticas/formCritica";
    }

    @GetMapping("/guardar/")
    public String guardarCritica(@RequestParam(name="critica") Critica critica) {
        critica.setFecha(LocalDate.now());
        criticaService.guardarCritica(critica);
        return "redirect:/cpeliculas/ver/" + critica.getIdPelicula();
    }

    @GetMapping("/eliminar/{idCritica}")
    public String eliminarCritica(@PathVariable("idCritica") Integer idCritica, RedirectAttributes attributes) {
        Critica critica = criticaService.buscarCriticaPorId(idCritica);
        if(critica != null) {
            criticaService.eliminarCritica(idCritica);
            attributes.addFlashAttribute("msg", "La crítica ha sido eliminada correctamente");
            return "redirect:/cpeliculas/ver/" + critica.getIdPelicula();
        } else {
            attributes.addFlashAttribute("msg", "La crítica no existe");
        }
        //TODO: devolver a la pagina anterior (listado de criticas de una pelicula o de un usuario)
        return "redirect:/ccriticas/buscarPor/" + critica.getIdPelicula();
    }

    @GetMapping("/buscarPor")
    public String buscarCriticasPor(Model model,
                                    @RequestParam("searchField") String searchField,
                                    @RequestParam("idPelicula") Optional<Integer> idPeliculaOpt,
                                    @RequestParam("idUsuario") Optional<Integer> idUsuarioOpt,
                                    @RequestParam(name="page", defaultValue="0") int page) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<Critica> criticas = null;
        if(searchField.equals("pelicula")) {
            if(idPeliculaOpt.isPresent()) {
                criticas = criticaService.buscarCriticasPorPelicula(idPeliculaOpt.get(), pageable);
            } else {
                model.addAttribute("msg", "No se ha encontrado la película");
                return "redirect:/ccriticas/listado";
            }
        } else if(searchField.equals("usuario")) {
            if (idUsuarioOpt.isPresent()) {
                criticas = criticaService.buscarCriticasPorUsuario(idUsuarioOpt.get(), pageable);
            } else {
                model.addAttribute("msg", "No se ha encontrado el usuario");
                return "redirect:/ccriticas/listado";
            }
        } else {
            model.addAttribute("msg", "No se ha encontrado la crítica");
            return "redirect:/ccriticas/listado";
        }

        model.addAttribute("titulo", "Listado de críticas");
        model.addAttribute("listado", criticas);
        String url = UriComponentsBuilder.fromPath("/ccriticas/buscarPor")
                .queryParam("searchField", searchField)
                .queryParamIfPresent("idPelicula", idPeliculaOpt)
                .queryParamIfPresent("idUsuario", idUsuarioOpt)
                .build()
                .toString();
        model.addAttribute("page", new PageRender<>(url, criticas));
        return "criticas/listCriticas";
    }

}
