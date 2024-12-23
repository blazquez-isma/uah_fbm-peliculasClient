package es.uah.ismael.fbm.peliculasClient.controller;

import es.uah.ismael.fbm.peliculasClient.model.Actor;
import es.uah.ismael.fbm.peliculasClient.model.Pelicula;
import es.uah.ismael.fbm.peliculasClient.paginator.PageRender;
import es.uah.ismael.fbm.peliculasClient.service.IActorService;
import es.uah.ismael.fbm.peliculasClient.service.ICriticaService;
import es.uah.ismael.fbm.peliculasClient.service.IPeliculaService;
import es.uah.ismael.fbm.peliculasClient.service.IUploadFileService;
import org.slf4j.Logger;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.http.HttpHeaders;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.MalformedURLException;
import java.security.Principal;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cpeliculas")
public class PeliculaController {

    Logger log = org.slf4j.LoggerFactory.getLogger(PeliculaController.class);

    public static final List<String> GENEROS_PELICULA = Arrays.asList("Acción", "Comedia", "Drama", "Terror", "Musical", "Ciencia Ficción", "Animación", "Aventuras", "Fantástico");
    @Autowired
    private IPeliculaService peliculasService;

    @Autowired
    private IUploadFileService uploadFileService;

    @Autowired
    private IActorService actorService;

    @Autowired
    private ICriticaService criticaService;

    @GetMapping("/listado")
    public String listadoPeliculas(Model model, @RequestParam(name="page", defaultValue="0") int page){
        Pageable pageable = PageRequest.of(page, 5);
        Page<Pelicula> peliculas = peliculasService.buscarTodas(pageable);
        PageRender<Pelicula> pageRender = new PageRender<>("/cpeliculas/listado", peliculas);
        model.addAttribute("titulo", "Listado de Películas");
        model.addAttribute("listado", peliculas);
        model.addAttribute("page", pageRender);
        return "peliculas/listPeliculas";
    }

    @GetMapping("/ver/{id}")
    public String verPelicula(@PathVariable("id") Integer id, Model model) {
        Pelicula pelicula = peliculasService.buscarPeliculaPorId(id);
        model.addAttribute("pelicula", pelicula);
        model.addAttribute("titulo", "Detalle de la Película");
        model.addAttribute("NotaMedia", criticaService.calcularNotaMediaDePelicula(id));
        return "peliculas/viewPelicula";
    }

    @GetMapping("/nueva")
    public String nuevaPelicula(Model model) {
        Pelicula pelicula = new Pelicula();
        model.addAttribute("pelicula", pelicula);
        model.addAttribute("titulo", "Nueva Película");
        model.addAttribute("generos", GENEROS_PELICULA);
        return "peliculas/formPelicula";
    }

    @GetMapping("/editar/{id}")
    public String editarPelicula(@PathVariable("id") Integer id, Model model) {
        Pelicula pelicula = peliculasService.buscarPeliculaPorId(id);
        model.addAttribute("pelicula", pelicula);
        model.addAttribute("titulo", "Editar Película");
        model.addAttribute("actores", pelicula.getActores());
        model.addAttribute("generos", GENEROS_PELICULA);
        return "peliculas/formPelicula";
    }

    @PostMapping("/guardar/")
    public String guardarPelicula(Model model, @ModelAttribute("pelicula") Pelicula pelicula,
                                  @RequestParam("imagen") MultipartFile imagen,
                                  @RequestParam("actoresIds") Optional<List<Integer>> actoresIds,
                                  RedirectAttributes attributes) {

        if (!imagen.isEmpty()) {
            if(pelicula.getIdPelicula() != null && pelicula.getIdPelicula() > 0 &&
                    pelicula.getImagenPortada() != null && !pelicula.getImagenPortada().isEmpty()) {
                uploadFileService.delete(pelicula.getImagenPortada());
            }
            String newImagenFilename = null;
            try {
                newImagenFilename = uploadFileService.copy(imagen);
            } catch (Exception e) {
                e.printStackTrace();
            }
            pelicula.setImagenPortada(newImagenFilename);
        }

        pelicula.setActores(new ArrayList<>());
        if (actoresIds.isPresent()) {
            List<Actor> newActores = actorService.buscarActoresPorIds(actoresIds.get());
            if(pelicula.getActores() != null) pelicula.getActores().clear();
            pelicula.setActores(newActores);
        }

        peliculasService.guardarPelicula(pelicula);

        return "redirect:/cpeliculas/listado";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPelicula(@PathVariable("id") Integer id, RedirectAttributes attributes) {
        Pelicula pelicula = peliculasService.buscarPeliculaPorId(id);
        if(pelicula != null) {
            if(pelicula.getImagenPortada() != null && !pelicula.getImagenPortada().isEmpty()) {
                uploadFileService.delete(pelicula.getImagenPortada());
            }
            peliculasService.eliminarPelicula(id);
            attributes.addFlashAttribute("msg", "Película eliminada correctamente");
        } else {
            attributes.addFlashAttribute("msg", "No se ha encontrado la película");
        }
        return "redirect:/cpeliculas/listado";
    }

    @GetMapping("/buscar")
    public String buscar(Model model) {
        model.addAttribute("searchFields", Arrays.asList("titulo", "año", "genero", "direccion", "actor"));
        model.addAttribute("searchField", "titulo");
        model.addAttribute("generos", GENEROS_PELICULA);
        return "peliculas/searchPelicula";
    }

    @GetMapping("/buscarPor")
    public String buscarPeliculasPor(Model model,
                                         @RequestParam("searchField") String searchField,
                                         @RequestParam("titulo") Optional<String> titulo,
                                         @RequestParam("anio1") Optional<Integer> anio1Opt,
                                         @RequestParam("anio2") Optional<Integer> anio2Opt,
                                         @RequestParam("genero") Optional<String> genero,
                                         @RequestParam("direccion") Optional<String> direccion,
                                         @RequestParam("actor") Optional<String> actorOpt,
                                         @RequestParam(name="page", defaultValue="0") int page) {

        Page<Pelicula> peliculas;
        switch (searchField) {
            case "titulo" ->
                    peliculas = peliculasService.buscarPeliculasPorTitulo(titulo.orElse(""), PageRequest.of(page, 5));
            case "año" ->{
                Integer anio1 = anio1Opt.orElse(0);
                Integer anio2 = anio2Opt.orElse(0);
                if(anio1Opt.isPresent() && anio2Opt.isPresent() && (anio1 > anio2)) {
                    model.addAttribute("msg", "El \"Año Desde\" debe ser menor que el \"Año Hasta\"");
                    model.addAttribute("searchFields", Arrays.asList("titulo", "año", "género", "director", "actor"));
                    model.addAttribute("searchField", "titulo");
                    model.addAttribute("generos", GENEROS_PELICULA);
                    return "peliculas/searchPelicula";
                }
                peliculas = peliculasService.buscarPeliculasPorAnio(anio1, anio2, PageRequest.of(page, 5));
            }
            case "genero" ->
                    peliculas = peliculasService.buscarPeliculasPorGenero(genero.orElse(""), PageRequest.of(page, 5));
            case "direccion" ->
                    peliculas = peliculasService.buscarPeliculasPorDireccion(direccion.orElse(""), PageRequest.of(page, 5));
            case "actor" -> {
                Actor actor = actorService.buscarActorPorNombreCompleto(actorOpt.orElse(""));
                if (actor != null) {
                    peliculas = peliculasService.buscarPeliculasPorActor(actor.getIdActor(), PageRequest.of(page, 5));
                } else {
                    model.addAttribute("msg", "No se ha encontrado el actor");
                    model.addAttribute("searchFields", Arrays.asList("titulo", "año", "género", "director", "actor"));
                    model.addAttribute("searchField", "titulo");
                    model.addAttribute("generos", GENEROS_PELICULA);
                    return "peliculas/searchPelicula";
                }
            }
            default -> peliculas = peliculasService.buscarTodas(PageRequest.of(page, 5));
        }
        model.addAttribute("titulo", "Listado de Peliculas");
        model.addAttribute("listado", peliculas);

        String url = UriComponentsBuilder.fromPath("/cpeliculas/buscarPor")
                .queryParam("searchField", searchField)
                .queryParamIfPresent("titulo", titulo)
                .queryParamIfPresent("anio1", anio1Opt)
                .queryParamIfPresent("anio2", anio2Opt)
                .queryParamIfPresent("genero", genero)
                .queryParamIfPresent("direccion", direccion)
                .queryParamIfPresent("actor", actorOpt)
                .build()
                .toString();
        model.addAttribute("page", new PageRender<>(url, peliculas));

        return "peliculas/listPeliculas";
    }

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> verImagen(@PathVariable String filename) {
        Resource recurso = null;
        try {
            recurso = uploadFileService.load(filename);
        } catch (MalformedURLException e) {
            return ResponseEntity.notFound()
                    .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + filename + "\"")
                    .build();
        }
        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
                .body(recurso);
    }

    @GetMapping(value = {"/", "/home", ""})
    public String home(Model model, Principal principal) {
        if (principal != null) {
            model.addAttribute("username", principal.getName());
        }
        return "home";
    }
}
