package com.bolsadeideas.spring.webflux.app.controllers;

import com.bolsadeideas.spring.webflux.app.models.dao.ProductoDao;
import com.bolsadeideas.spring.webflux.app.models.documents.Producto;
import com.bolsadeideas.spring.webflux.app.models.services.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.validation.BindingResult;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.thymeleaf.spring5.context.webflux.ReactiveDataDriverContextVariable;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

import javax.naming.Binding;
import javax.validation.Valid;
import java.time.Duration;
import java.util.Date;

@Controller
public class ProductoController {   

    // Camellando con servicios
    @Autowired
    private ProductoService service;

    private static final Logger log = LoggerFactory.getLogger(ProductoController.class);
    
    @GetMapping({"/listar","/"})
    public Mono<String> listar(Model model) {

        Flux<Producto> productos = service.findAllWithNameUpperCase();

        productos.subscribe(producto -> log.info(producto.getNombre()));

        model.addAttribute("productos", productos);
        model.addAttribute("titulo", "Listado de productos");
        return Mono.just("listar");
    }

    @GetMapping("/form")
    public Mono<String> crear(Model model){
        model.addAttribute("producto", new Producto());
        model.addAttribute("titulo", "Formulario de producto");
        model.addAttribute("boton", "Crear");
        return Mono.just("form");
    }

    @PostMapping("/form")
    public Mono<String> guardar(@Valid Producto producto, BindingResult result, Model model){

        if(result.hasErrors()){
            model.addAttribute("titulo", "Errores en formulario producto");
            model.addAttribute("boton", "Guardar");
            model.addAttribute("producto", producto);
            return  Mono.just("form");
        } else {

            if (producto.getCreateAt() == null)
                producto.setCreateAt(new Date());

            return service.save(producto).doOnNext(p -> {
                log.info("Producto Guardado: " + p.getNombre() + " Id: " + p.getId());
            }).thenReturn("redirect:/listar?success=producto+guardado+con+exito");
        }
    }

    @GetMapping("/eliminar/{id}")
    public Mono<String> eliminar(@PathVariable String id){
        return service.findById(id)
                .defaultIfEmpty(new Producto())
                .flatMap(p -> {
                    if (p.getId() == null){
                        return Mono.error(new InterruptedException("No existe el producto a eliminar"));
                    }
                    return Mono.just(p);
                })
                .flatMap(service::delete).then(Mono.just("redirect:/listar?success=producto+eliminado+con+exito"))
                .onErrorResume(ex -> Mono.just("redirect:/listar?error=no+existe+el+producto+a+eliminar"));
    }

    @GetMapping("/form-v2/{id}")
    public Mono<String> editarV2(@PathVariable String id, Model model){

        return service.findById(id).doOnNext(p -> {
            log.info("Producto: " + p.getNombre());
            model.addAttribute("boton", "Editar");
            model.addAttribute("titulo", "Editar Producto");
            model.addAttribute("Producto", p);
        }).defaultIfEmpty(new Producto())
                .flatMap(p -> {
                    if (p.getId() == null){
                        return Mono.error(new InterruptedException("No existe el producto"));
                    }
                    return Mono.just(p);
        }).then(Mono.just("form"))
                .onErrorResume(ex -> Mono.just("redirect:/listar?error=no+existe+el+producto"));
    }

    @GetMapping("/form/{id}")
    public Mono<String> editar(@PathVariable String id, Model model){
        Mono<Producto> productoMono = service.findById(id).doOnNext(p -> {
           log.info("Producto: " + p.getNombre());
        }).defaultIfEmpty(new Producto());

        model.addAttribute("boton", "Editar");
        model.addAttribute("titulo", "Editar Producto");
        model.addAttribute("producto", productoMono);

        return Mono.just("form");
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
