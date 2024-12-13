package es.uah.ismael.fbm.peliculasClient.controller;

import es.uah.ismael.fbm.peliculasClient.model.Rol;
import es.uah.ismael.fbm.peliculasClient.model.Usuario;
import es.uah.ismael.fbm.peliculasClient.paginator.PageRender;
import es.uah.ismael.fbm.peliculasClient.service.IRolService;
import es.uah.ismael.fbm.peliculasClient.service.IUsuarioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;
import org.springframework.web.util.UriComponentsBuilder;

import java.util.List;
import java.util.Optional;

@Controller
@RequestMapping("/cusuarios")
public class UsuarioController {

    @Autowired
    IUsuarioService usuarioService;

    @Autowired
    IRolService rolService;


    @GetMapping("/listado")
    public String listarUsuarios(Model model, @RequestParam(name="page", defaultValue="0") int page) {
        Pageable pageable = PageRequest.of(page, 5);
        Page<Usuario> usuarios = usuarioService.buscarTodos(pageable);
        PageRender<Usuario> pageRender = new PageRender<>("/cusuarios/listado", usuarios);
        model.addAttribute("titulo", "Listado de usuarios");
        model.addAttribute("listado", usuarios);
        model.addAttribute("page", pageRender);
        return "usuarios/listUsuarios";
    }

    @GetMapping("/ver/{id}")
    public String verUsuario(Model model, @PathVariable("id") Integer id) {
        Usuario usuario = usuarioService.buscarUsuarioPorId(id);
        model.addAttribute("usuario", usuario);
        model.addAttribute("titulo", "Detalle del usuario: " + usuario.getNombre());
        return "usuarios/viewUsuario";
    }

    @GetMapping("/nuevo")
    public String nuevoUsuario(Model model) {
        model.addAttribute("titulo", "Nuevo usuario");
        model.addAttribute("allRoles", rolService.buscarTodos());
        model.addAttribute("usuario", new Usuario());
        return "usuarios/formUsuario";
    }

    @GetMapping("/editar/{id}")
    public String editarUsuario(Model model, @PathVariable("id") Integer id) {
        Usuario usuario = usuarioService.buscarUsuarioPorId(id);
        model.addAttribute("titulo", "Editar usuario");
        model.addAttribute("usuario", usuario);
        model.addAttribute("allRoles", rolService.buscarTodos());
        return "usuarios/formUsuario";
    }

    @PostMapping("/guardar/")
    public String guardarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes attributes) {
        if(usuarioService.buscarUsuarioPorCorreo(usuario.getCorreo()) != null) {
            attributes.addFlashAttribute("msg", "El correo ya está registrado");
            return "redirect:/cusuarios/nuevo";
        }
        usuarioService.guardarUsuario(usuario);
        attributes.addFlashAttribute("msg", "Usuario guardado correctamente");
        return "redirect:/cusuarios/listado";
    }

    @GetMapping("/eliminar/{id}")
    public String eliminarUsuario(@PathVariable("id") Integer id, RedirectAttributes attributes) {
        usuarioService.eliminarUsuario(id);
        attributes.addFlashAttribute("msg", "Usuario eliminado correctamente");
        return "redirect:/cusuarios/listado";
    }

    @GetMapping("/buscarPor")
    public String buscarUsuariosPor(Model model,
                                    @RequestParam("searchField") String searchField,
                                    @RequestParam("nombre") Optional<String> nombreOpt,
                                    @RequestParam("correo") Optional<String> correoOpt) {
        Usuario usuario = null;
        if (searchField.equals("nombre")) {
            usuario = usuarioService.buscarUsuarioPorNombre(nombreOpt.orElse(""));
        } else if (searchField.equals("correo")) {
            usuario = usuarioService.buscarUsuarioPorCorreo(correoOpt.orElse(""));
        } else {
            model.addAttribute("msg", "Campo de búsqueda no válido");
            return "redirect:/cusuarios/searchUsuario";
        }
        if (usuario == null) {
            model.addAttribute("msg", "Usuario no encontrado");
            return "redirect:/cusuarios/searchUsuario";
        }
        return "redirect:/cusuarios/ver/" + usuario.getIdUsuario();
    }

}
