package es.uah.ismael.fbm.peliculasClient.controller;

import es.uah.ismael.fbm.peliculasClient.model.Actor;
import es.uah.ismael.fbm.peliculasClient.model.Pelicula;
import es.uah.ismael.fbm.peliculasClient.paginator.PageRender;
import es.uah.ismael.fbm.peliculasClient.service.IActorService;
import es.uah.ismael.fbm.peliculasClient.service.IPeliculaService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.time.LocalDate;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cpeliculas/actores")
public class ActorController {

    @Autowired
    private IActorService actorService;

    @Autowired
    private IPeliculaService peliculaService;

    @GetMapping("/listado")
    public String listadoActores(Model model, @RequestParam(name="page", defaultValue="0") int page) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<Actor> actores = actorService.buscarTodos(pageable);
        PageRender<Actor> pageRender = new PageRender<>("/cpeliculas/actores/listado", actores);
        model.addAttribute("titulo", "Listado de Actores");
        model.addAttribute("listado", actores);
        model.addAttribute("page", pageRender);
        return "actores/listActores";
    }

    @GetMapping("/ver/{id}")
    public String verActor(@PathVariable("id") Integer id, Model model) {
        Actor actor = actorService.buscarActorPorId(id);
        model.addAttribute("titulo", "Detalle del Actor");
        model.addAttribute("actor", actor);
        return "actores/viewActor";
    }

    @GetMapping("/nuevo")
    public String nuevoActor(Model model) {
        Actor actor = new Actor();
        model.addAttribute("titulo", "Nuevo Actor");
        model.addAttribute("actor", actor);
        return "actores/formActor";
    }

    @GetMapping("/editar/{id}")
    public String editarActor(@PathVariable("id") Integer id, Model model) {
        Actor actor = actorService.buscarActorPorId(id);
        model.addAttribute("titulo", "Editar Actor");
        model.addAttribute("actor", actor);
        return "actores/formActor";
    }

    @PostMapping("/guardar")
    public String guardarActor(Model model, @ModelAttribute("actor") Actor actor, RedirectAttributes attributes) {
        actorService.guardarActor(actor);
        attributes.addFlashAttribute("msg", "Actor guardado correctamente");
        return "redirect:/cpeliculas/actores/listado";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarActor(@PathVariable("id") Integer id, RedirectAttributes attributes) {
        Actor actor = actorService.buscarActorPorId(id);
        if(actor != null){
            actorService.eliminarActor(id);
            attributes.addFlashAttribute("msg", "Actor eliminado correctamente");
        } else {
            attributes.addFlashAttribute("msg", "No se ha encontrado el actor");
        }
       return "redirect:/cpeliculas/actores/listado";
    }

    @GetMapping("/buscar")
    public String buscar(Model model) {
        model.addAttribute("searchFields", Arrays.asList("nombre", "pelicula", "fechaNacimiento"));
        model.addAttribute("searchField", "nombre");
        return "actores/searchActor";
    }

    @GetMapping("/buscarPor")
    public String buscarActoresPor(Model model,
                                         @RequestParam("searchField") String searchField,
                                         @RequestParam("nombre") Optional<String> nombre,
                                         @RequestParam("peliculaOpt") Optional<String> peliculaOpt,
                                         @RequestParam("fecha1") Optional<LocalDate> fecha1Opt,
                                         @RequestParam("fecha2") Optional<LocalDate> fecha2Opt,
                                         @RequestParam(name="page", defaultValue="0") int page) {

        Page<Actor> actores;
        switch (searchField) {
            case "nombre" ->
                    actores = actorService.buscarActoresPorNombre(nombre.orElse(""), PageRequest.of(page, 5));
            case "pelicula" -> {
                Pelicula pelicula = peliculaService.buscarPeliculaPorTituloCompleto(peliculaOpt.orElse(""));
                if (pelicula != null) {
                    actores = actorService.buscarActoresPorPelicula(pelicula.getIdPelicula(), PageRequest.of(page, 5));
                } else {
                    model.addAttribute("msg", "No se ha encontrado la película");
                    model.addAttribute("searchFields", Arrays.asList("nombre", "pelicula", "fechaNacimiento"));
                    model.addAttribute("searchField", "nombre");
                    return "actores/searchActor";
                }
            }
            case "fechaNacimiento" -> {
                LocalDate fecha1 = fecha1Opt.orElse(LocalDate.MIN);
                LocalDate fecha2 = fecha2Opt.orElse(LocalDate.MAX);
                if(fecha1Opt.isPresent() && fecha2Opt.isPresent() && fecha1.isAfter(fecha2)){
                    model.addAttribute("msg", "La \"Fecha Desde\" debe ser menor que la \"Fecha Hasta\"");
                    model.addAttribute("searchFields", Arrays.asList("nombre", "pelicula", "fechaNacimiento"));
                    model.addAttribute("searchField", "nombre");
                    return "actores/searchActor";
                }
                actores = actorService.buscarActoresPorFechaNacimiento(fecha1, fecha2, PageRequest.of(page, 5));
            }
            default -> actores = actorService.buscarTodos(PageRequest.of(page, 5));
        }
        model.addAttribute("titulo", "Listado de Actores");
        model.addAttribute("listado", actores);

        String url = UriComponentsBuilder.fromPath("/cpeliculas/actores/buscarPor")
                .queryParam("searchField", searchField)
                .queryParamIfPresent("nombre", nombre)
                .queryParamIfPresent("pelicula", peliculaOpt)
                .queryParamIfPresent("fecha1", fecha1Opt)
                .queryParamIfPresent("fecha2", fecha2Opt)
                .build()
                .toString();
        model.addAttribute("page", new PageRender<>(url, actores));

        return "actores/listActores";
    }


    @GetMapping("/buscarPorNombre")
    @ResponseBody
    public List<Actor> buscarActoresPorNombre(@RequestParam("nombre") String nombre) {
        return actorService.buscarActoresPorNombre(nombre);
    }


}
