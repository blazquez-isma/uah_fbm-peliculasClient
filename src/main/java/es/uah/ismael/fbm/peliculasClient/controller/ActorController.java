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
        attributes.addFlashAttribute("mensaje", "Actor guardado correctamente");
        return "redirect:/cpeliculas/actores/listado";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarActor(@PathVariable("id") Integer id, RedirectAttributes attributes) {
        actorService.eliminarActor(id);
        attributes.addFlashAttribute("mensaje", "Actor eliminado correctamente");
        return "redirect:/cpeliculas/actores/listado";
    }

    @GetMapping("/buscar")
    public String buscar(Model model) {
        model.addAttribute("searchFields", Arrays.asList("nombre", "pelicula"));
        model.addAttribute("searchField", "nombre");
        return "actores/searchActor";
    }

    @GetMapping("/buscarPor")
    public String buscarActoresPor(Model model,
                                         @RequestParam("searchField") String searchField,
                                         @RequestParam("nombre") Optional<String> nombreOpt,
                                         @RequestParam("pelicula") Optional<String> peliculaOpt,
                                         @RequestParam(name="page", defaultValue="0") int page) {

        Page<Actor> actores;
        if(searchField.equals("nombre")) {
            actores = actorService.buscarActoresPorNombre(nombreOpt.orElse(""), PageRequest.of(page, 5));
        } else if(searchField.equals("pelicula")) {
            Pelicula pelicula = peliculaService.buscarPeliculaPorTituloCompleto(peliculaOpt.orElse(""));
            if(pelicula != null) {
                actores = actorService.buscarActoresPorPelicula(pelicula.getIdPelicula(), PageRequest.of(page, 5));
            } else {
                model.addAttribute("msg", "No se ha encontrado la película");
                model.addAttribute("searchFields", Arrays.asList("nombre", "pelicula"));
                model.addAttribute("searchField", "nombre");
                return "actores/searchActor";
            }
        } else {
            actores = actorService.buscarTodos(PageRequest.of(page, 5));
        }
        model.addAttribute("titulo", "Listado de Actores");
        model.addAttribute("listado", actores);
        model.addAttribute("page", new PageRender<>("/cpeliculas/actores/listado", actores));
        return "actores/listActores";
    }


    @GetMapping("/buscarPorNombre")
    @ResponseBody
    public List<Actor> buscarActoresPorNombre(@RequestParam("nombre") String nombre) {
        return actorService.buscarActoresPorNombre(nombre);
    }


}
