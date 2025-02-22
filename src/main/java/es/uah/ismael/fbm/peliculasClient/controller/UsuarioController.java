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
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.servlet.mvc.support.RedirectAttributes;

import java.util.Arrays;
import java.util.Collections;
import java.util.Optional;

@Controller
@RequestMapping("/cusuarios")
public class UsuarioController {

    @Autowired
    IUsuarioService usuarioService;

    @Autowired
    IRolService rolService;

    @Autowired
    private PasswordEncoder passwordEncoder;

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

    @GetMapping("/registrar")
    public String nuevoRegistro(Model model) {
        model.addAttribute("titulo", "Nuevo registro");
        Usuario usuario = new Usuario();
        model.addAttribute("usuario", usuario);
        return "registro";
    }

    @PostMapping("/registrar")
    public String registro(Model model, Usuario usuario, RedirectAttributes attributes) {
        //si existe un usuario con el mismo correo no lo guardamos
        if (usuarioService.buscarUsuarioPorCorreo(usuario.getCorreo())!=null) {
            attributes.addFlashAttribute("msga", "Error al guardar, ya existe el correo!");
            return "redirect:/login";
        }
        if(usuarioService.buscarUsuarioPorNombre(usuario.getNombre())!=null){
            attributes.addFlashAttribute("msga", "Error al guardar, ya existe el nombre!");
            return "redirect:/login";
        }

        Rol rol = rolService.buscarTodos().stream().filter(r -> r.getAuthority().contains("USER")).findFirst().orElse(null);
        usuario.setRoles(Collections.singletonList(rol));
        usuario.setEnable(true);
        usuario.setClave(passwordEncoder.encode(usuario.getClave()));

        usuarioService.guardarUsuario(usuario);
        attributes.addFlashAttribute("msg", "Los datos del registro fueron guardados!");
        return "redirect:/login";
    }

    @GetMapping("/editar/{id}")
    public String editarUsuario(Model model, @PathVariable("id") Integer id) {
        Usuario usuario = usuarioService.buscarUsuarioPorId(id);
        model.addAttribute("titulo", "Editar usuario");
        model.addAttribute("usuario", usuario);
        model.addAttribute("allRoles", rolService.buscarTodos());
        return "usuarios/formUsuario";
    }

    @GetMapping("/activar/{id}")
    public String activarUsuario(@PathVariable("id") Integer id, RedirectAttributes attributes) {
        Usuario usuario = usuarioService.buscarUsuarioPorId(id);
        usuario.setEnable(!usuario.isEnable());
        usuarioService.guardarUsuario(usuario);
        String activado = usuario.isEnable() ? "activado" : "desactivado";
        attributes.addFlashAttribute("msg", "Usuario " + activado + " correctamente");
        return "redirect:/cusuarios/listado";
    }

    @PostMapping("/guardar/")
    public String guardarUsuario(@ModelAttribute Usuario usuario, RedirectAttributes attributes) {
        if((usuario.getIdUsuario() == null || usuario.getIdUsuario() == 0)){
            if (usuarioService.buscarUsuarioPorCorreo(usuario.getCorreo()) != null){
                attributes.addFlashAttribute("msg", "El correo ya está registrado");
                return "redirect:/cusuarios/nuevo";
            } else if (usuarioService.buscarUsuarioPorNombre(usuario.getNombre()) != null){
                attributes.addFlashAttribute("msg", "El nombre ya está registrado");
                return "redirect:/cusuarios/nuevo";
            }
        }
        usuario.setClave(passwordEncoder.encode(usuario.getClave()));

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

    @GetMapping("/buscar")
    public String buscar(Model model) {
        model.addAttribute("searchFields", Arrays.asList("nombre", "correo"));
        model.addAttribute("searchField", "nombre");
        return "usuarios/searchUsuario";
    }

    @GetMapping("/buscarPor")
    public String buscarUsuariosPor(Model model,
                                    @RequestParam("searchField") String searchField,
                                    @RequestParam("nombre") Optional<String> nombreOpt,
                                    @RequestParam("correo") Optional<String> correoOpt) {
        Usuario usuario = null;
        String fieldValue;
        if (searchField.equals("nombre")) {
            fieldValue = nombreOpt.orElse("");
            usuario = usuarioService.buscarUsuarioPorNombre(nombreOpt.orElse(""));
        } else if (searchField.equals("correo")) {
            fieldValue = correoOpt.orElse("");
            usuario = usuarioService.buscarUsuarioPorCorreo(correoOpt.orElse(""));
        } else {
            model.addAttribute("msg", "Campo de búsqueda no válido");
            model.addAttribute("searchFields", Arrays.asList("nombre", "correo"));
            return "usuarios/searchUsuario";
        }
        if (usuario == null) {
            model.addAttribute("msg", "Usuario con " + searchField + " \"" + fieldValue + "\" no encontrado");
            model.addAttribute("searchFields", Arrays.asList("nombre", "correo"));
            return "usuarios/searchUsuario";
        }
        return "redirect:/cusuarios/ver/" + usuario.getIdUsuario();
    }

}
