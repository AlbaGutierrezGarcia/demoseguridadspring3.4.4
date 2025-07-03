package ubu.adrian.taller.controller;


import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

import ubu.adrian.taller.model.Event;
import ubu.adrian.taller.services.EventServicesImpl;

import org.springframework.ui.Model;

/**
 * Controlador de la página principal
 * 
 * @author Adrián Zamora Sánchez (azs1004@alu.ubu.es)
 */
@Controller
public class HomeController {
	// Servicio de eventos
	@Autowired
	private EventServicesImpl eventServices;
	
	/**
	 * Gestiona las solicitudes de la ruta /
	 * 
	 * @param model Modelo donde se insertan los datos
	 * @return página home
	 */
	@GetMapping("/")
    public String home(Model model) {
        
        List<Event> eventList = eventServices.findAll();
        model.addAttribute("eventList", eventList);
        
        return "home";
    }
}