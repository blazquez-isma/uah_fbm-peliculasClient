package es.uah.ismael.fbm.peliculasClient.controller;

import es.uah.ismael.fbm.peliculasClient.model.*;
import es.uah.ismael.fbm.peliculasClient.paginator.PageRender;
import es.uah.ismael.fbm.peliculasClient.service.ICriticaService;
import es.uah.ismael.fbm.peliculasClient.service.IPeliculaService;
import es.uah.ismael.fbm.peliculasClient.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.security.Principal;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/ccriticas")
public class CriticaController {

    @Autowired
    private ICriticaService criticaService;

    @Autowired
    private IUsuarioService usuarioService;

    @Autowired
    private IPeliculaService peliculaService;

//    @RequestMapping("/listado")
//    public String listadoCriticas(Model model, @RequestParam(name="page", defaultValue="0") int page) {
//        Pageable pageable = PageRequest.of(page, 5);
//        Page<Critica> criticas = criticaService.buscarTodas(pageable);
//        PageRender<Critica> pageRender = new PageRender<>("/ccriticas/listado", criticas);
//        model.addAttribute("titulo", "Listado de críticas");
//        model.addAttribute("page", pageRender);
//        model.addAttribute("listado", criticaService.buscarTodas(pageable));
//        return "criticas/listCriticas";
//    }

    @GetMapping("/nueva/{idPelicula}")
    public String nuevaCritica(Model model, @PathVariable("idPelicula") Integer idPelicula, Principal principal) {
        model.addAttribute("titulo", "Nueva crítica");
        CriticaPelicula critica = new CriticaPelicula();
        critica.setIdPelicula(idPelicula);
        critica.setTituloPelicula(peliculaService.buscarTituloPeliculaPorId(idPelicula));
        critica.setUsuario(usuarioService.buscarUsuarioPorNombre(principal.getName()));
        System.out.println("Critica: " + critica);
        model.addAttribute("critica", critica);
        return "criticas/formCritica";
    }

    @GetMapping("/editar/{idCritica}")
    public String editarCritica(Model model, @PathVariable(name="idCritica") Integer idCritica) {
        model.addAttribute("titulo", "Editar crítica");
        Critica critica = criticaService.buscarCriticaPorId(idCritica);
        CriticaPelicula criticaPelicula = new CriticaPelicula(peliculaService.buscarTituloPeliculaPorId(critica.getIdPelicula()), critica);
        model.addAttribute("critica", criticaPelicula);
        return "criticas/formCritica";
    }

    @PostMapping("/guardar/")
    public String guardarCritica(@ModelAttribute(name="critica") Critica critica) {
        System.out.println("Usuario: " + critica.getUsuario());
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

    @GetMapping("/buscar")
    public String buscar(Model model, Principal principal) {
        model.addAttribute("searchFields", Arrays.asList("pelicula", "usuario"));
        model.addAttribute("searchField", "pelicula");
        if (principal != null) {
            Usuario usuario = usuarioService.buscarUsuarioPorNombre(principal.getName());
            model.addAttribute("username", usuario.getNombre());
            model.addAttribute("roles", usuario.getRoles().stream().map(Rol::getAuthority).toList());
        }
        return "criticas/searchCritica";
    }

    @GetMapping("/buscarPor")
    public String buscarCriticasPor(Model model,
                                    @RequestParam("searchField") String searchField,
                                    @RequestParam("tituloPelicula") Optional<String> tituloPelicula,
                                    @RequestParam("nombreUsuario") Optional<String> nombreUsuario,
                                    @RequestParam(name="page", defaultValue="0") int page) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<CriticaPelicula> criticas = null;

        if(searchField.equals("pelicula")) {
            if(tituloPelicula.isPresent()) {
                Pelicula pelicula = peliculaService.buscarPeliculaPorTituloCompleto(tituloPelicula.get());
                if (pelicula != null) {
                    Page<Critica> pageCriticas = criticaService.buscarCriticasPorPelicula(pelicula.getIdPelicula(), pageable);

                    List<CriticaPelicula> listCriticas = pageCriticas.stream().map(critica -> new CriticaPelicula(tituloPelicula.get(), critica)).toList();

                    criticas = new PageImpl<>(listCriticas, pageable, pageCriticas.getTotalElements());
                    model.addAttribute("titulo", "Listado de críticas de la película " + tituloPelicula.get());
                    model.addAttribute("idPelicula", pelicula.getIdPelicula());
                } else {
                    model.addAttribute("msg", "No se ha encontrado la película");
                    model.addAttribute("searchFields", Arrays.asList("pelicula", "usuario"));
                    model.addAttribute("searchField", "pelicula");
                    return "criticas/searchCritica";
                }
            }
        } else if(searchField.equals("usuario")) {
            if (nombreUsuario.isPresent()) {
                Usuario usuario = usuarioService.buscarUsuarioPorNombre(nombreUsuario.get());
                if(usuario != null){
                    Page<Critica> pageCriticas = criticaService.buscarCriticasPorUsuario(usuario.getIdUsuario(), pageable);
                    List<CriticaPelicula> listCriticas = pageCriticas.stream().map(critica -> {
                        String titulo = peliculaService.buscarTituloPeliculaPorId(critica.getIdPelicula());
                        return new CriticaPelicula(titulo, critica);
                    }).toList();
                    criticas = new PageImpl<>(listCriticas, pageable, pageCriticas.getTotalElements());
                    model.addAttribute("titulo", "Listado de críticas del usuario " + nombreUsuario.get());
                } else {
                    model.addAttribute("msg", "No se ha encontrado el usuario");
                    model.addAttribute("searchFields", Arrays.asList("pelicula", "usuario"));
                    model.addAttribute("searchField", "pelicula");
                    return "criticas/searchCritica";
                }
            }
        }

        model.addAttribute("listado", criticas);
        String url = UriComponentsBuilder.fromPath("/ccriticas/buscarPor")
                .queryParam("searchField", searchField)
                .queryParamIfPresent("idPelicula", tituloPelicula)
                .queryParamIfPresent("idUsuario", nombreUsuario)
                .build()
                .toString();
        model.addAttribute("page", new PageRender<>(url, criticas));

        return "criticas/listCriticas";
    }

}
