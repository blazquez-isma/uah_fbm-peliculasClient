package es.uah.ismael.fbm.peliculasClient.controller;

import es.uah.ismael.fbm.peliculasClient.model.Pelicula;
import es.uah.ismael.fbm.peliculasClient.paginator.PageRender;
import es.uah.ismael.fbm.peliculasClient.service.IPeliculaService;
import es.uah.ismael.fbm.peliculasClient.service.IUploadFileService;
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

import java.net.MalformedURLException;

@Controller
@RequestMapping("/cpeliculas")
public class PeliculaController {

    @Autowired
    private IPeliculaService peliculasService;

    @Autowired
    private IUploadFileService uploadFileService;

    @GetMapping("/uploads/{filename:.+}")
    public ResponseEntity<Resource> verImagen(@PathVariable String filename) {

        Resource recurso = null;

        try {
            recurso = uploadFileService.load(filename);
        } catch (MalformedURLException e) {
            e.printStackTrace();
        }

        return ResponseEntity.ok()
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + recurso.getFilename() + "\"")
                .body(recurso);
    }

    @GetMapping(value = {"/", "/home", ""})
    public String home() {
        return "home";
    }

    @GetMapping("/buscar")
    public String buscar(Model model) {
        return "peliculas/searchPelicula";
    }


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
        return "peliculas/viewPelicula";
    }

    @GetMapping("/nueva")
    public String nuevaPelicula(Model model) {
        Pelicula pelicula = new Pelicula();
        model.addAttribute("pelicula", pelicula);
        model.addAttribute("titulo", "Nueva Película");
        return "peliculas/formPelicula";
    }

    @GetMapping("/editar/{id}")
    public String editarPelicula(@PathVariable("id") Integer id, Model model) {
        Pelicula pelicula = peliculasService.buscarPeliculaPorId(id);
        model.addAttribute("pelicula", pelicula);
        model.addAttribute("titulo", "Editar Película");
        return "peliculas/formPelicula";
    }

    @PostMapping("/guardar/")
    public String guardarPelicula(Model model, @ModelAttribute("pelicula") Pelicula pelicula,
                                  @RequestParam("imagen") MultipartFile imagen, RedirectAttributes attributes) {

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

        peliculasService.guardarPelicula(pelicula);
        return "redirect:/cpeliculas/listado";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarPelicula(@PathVariable("id") Integer id, RedirectAttributes attributes) {
        Pelicula pelicula = peliculasService.buscarPeliculaPorId(id);
        if(pelicula.getImagenPortada() != null && !pelicula.getImagenPortada().isEmpty()) {
            uploadFileService.delete(pelicula.getImagenPortada());
        }
        peliculasService.eliminarPelicula(id);
        attributes.addFlashAttribute("mensaje", "Película eliminada correctamente");
        return "redirect:/cpeliculas/listado";
    }

    @GetMapping("/buscarTitulo")
    public String buscarPeliculasPorTitulo(Model model, @RequestParam(name="page", defaultValue="0") int page, @RequestParam("titulo") String titulo) {
        Page<Pelicula> peliculas = peliculasService.buscarPeliculasPorTitulo(titulo, PageRequest.of(page, 5));

        model.addAttribute("titulo", "Listado de Películas");
        model.addAttribute("listado", peliculas);
        model.addAttribute("page", new PageRender<>("/listado", peliculas));
        return "peliculas/listPeliculas";
    }



}
