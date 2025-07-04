package ubu.adrian.taller.controller;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.Iterator;
import java.util.List;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PostMapping;

import ubu.adrian.taller.dto.UserRegisterDTO;
import ubu.adrian.taller.model.User;
import ubu.adrian.taller.model.UserRol;
import ubu.adrian.taller.services.UserServicesImpl;

/**
 * Controlador de las páginas relacionadas con la gestión de usuarios
 * 
 * @author Adrián Zamora Sánchez (azs1004@alu.ubu.es)
 */
@Controller
public class UserController {
    // Servicio de usuarios
    @Autowired
    private UserServicesImpl userServices;


    /**
     * Gestiona las solicitudes de la ruta /login
     *
     * @return página de login
     */
    @GetMapping("/login")
    public String login() {
        return "login";
    }
    
    /**
     * Gestiona las solicitudes de la ruta /register
     *
     * @param model Modelo donde se insertan los datos
     * @return página de registro
     */
    @GetMapping("/register")
    public String register(Model model) {
        User user = new User();

        model.addAttribute("user", user);

        //Acceso al security context holder
        Authentication auth = SecurityContextHolder.getContext().getAuthentication();
        //¿Es el usuario administrador?
        boolean isAdmin = auth.getAuthorities().stream()
                .anyMatch(a -> a.getAuthority().equals("ROLE_ADMIN"));

        //Si soy administrador limito el listado del objeto enum


        // Si es admin, todos los roles; si no, todos menos ADMIN
        List<UserRol> roles = Arrays.stream(UserRol.values())
                .filter(rol -> isAdmin || rol != UserRol.ADMIN)
            .collect(Collectors.toList());

        /*List<String> strings = new ArrayList<>();
        //Con notación larga
        Iterator<UserRol> stringIterator = roles.iterator();
        while (stringIterator.hasNext()){
            if (isAdmin){
                strings.add(stringIterator.toString());
            }
            else {
                if(!stringIterator.toString().equals("ADMIN")){
                    strings.add(stringIterator.toString());
                }
            }
        }*/

        model.addAttribute("rolList", roles);
        
        return "register";
    }
    
    /**
     * Gestiona las solicitudes de la ruta /create-user
     *
     * Crea un nuevo usuario a partir de los datos obtenidos
     *
     * @param userDTO Objeto de transferencia de datos de usuario
     * @param result Resultado que se espera devolver
     * @param authentication Token de autenticación del usuario
     * @return página de registro o user-list
     */
    @PostMapping("/create-user")
    public String createUser(@ModelAttribute("user") UserRegisterDTO userDTO, BindingResult result
            ) {

        // Se comprueba que el usuario tiene contraseña
        if (userDTO.getPassword() == null || userDTO.getPassword().isEmpty()) {
            result.rejectValue("password", "error.user", "La contraseña es obligatoria");
            return "register";
        }

        // Se comprueba que el usuario tiene nombre
        if (userDTO.getUsername() == null || userDTO.getUsername().isEmpty()) {
            result.rejectValue("username", "error.user", "El nombre de usuario es obligatorio");
            return "register";
        }

        // Rol que se establece en el usuario
        UserRol finalRol = userDTO.getRol();

        
        // Se genera el usuario desde el DTO
        User user = new User();
        user.setUsername(userDTO.getUsername());
        user.setRol(finalRol);
        user.setPassword(userDTO.getPassword());

        // Se guarda al usuario en la db
        userServices.saveUser(user);
        
        // Si lo crea el admin devuelve user list, sino pagina de login
        return  "redirect:/login";
    }
}