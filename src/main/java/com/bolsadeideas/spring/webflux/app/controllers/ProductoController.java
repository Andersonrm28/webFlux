package com.bolsadeideas.spring.webflux.app.controllers;

import com.bolsadeideas.spring.webflux.app.models.dao.ProductoDao;
import com.bolsadeideas.spring.webflux.app.models.documents.Producto;
import com.bolsadeideas.spring.webflux.app.models.services.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;

import java.time.Duration;

@Controller
public class ProductoController {   
    
    @Autowired
    private ProductoService service;

    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);
    
    @GetMapping({"/listar","/"})
    public String listar(Model model) {

        Flux<Producto> productos = service.findAllWithNameUpperCase();

        productos.subscribe(producto -> log.info(producto.getNombre()));

        model.addAttribute("productos", productos);
        model.addAttribute("titulo", "Listado de productos");
        return "listar";
    }

    @GetMapping("/listar-datadriver")
    public String listarDataDriver(Model model) {

        Flux<Producto> productos = service.findAllWithNameUpperCase().delayElements(Duration.ofSeconds(1));

        productos.subscribe(producto -> log.info(producto.getNombre()));

        model.addAttribute("productos", new ReactiveDataDriverContextVariable(productos,2));
        model.addAttribute("titulo", "Listado de productos");
        return "listar";
    }

    @GetMapping("/listar-full")
    public String listarFull(Model model) {

        Flux<Producto> productos = service.findAllWithNameUpperCaseRepeat();

        model.addAttribute("productos", productos);
        model.addAttribute("titulo", "Listado de productos");
        return "listar";
    }

    @GetMapping("/listar-chunked")
    public String listarChunked(Model model) {

        Flux<Producto> productos = service.findAllWithNameUpperCaseRepeat();

        model.addAttribute("productos", productos);
        model.addAttribute("titulo", "Listado de productos");
        return "listar-chunked";
    }
}