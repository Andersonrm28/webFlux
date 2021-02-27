package com.bolsadeideas.spring.webflux.app;

import com.bolsadeideas.spring.webflux.app.models.documents.Categoria;
import com.bolsadeideas.spring.webflux.app.models.documents.Producto;
import com.bolsadeideas.spring.webflux.app.models.services.ProductoService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.data.mongodb.core.ReactiveMongoTemplate;
import reactor.core.publisher.Flux;

import java.util.Date;

@SpringBootApplication
public class SpringBootWebfluxApplication implements CommandLineRunner {

	@Autowired
	private ProductoService service;

	@Autowired
	private ReactiveMongoTemplate mongoTemplate;

	private static final Logger log = LoggerFactory.getLogger(SpringBootWebfluxApplication.class);

	public static void main(String[] args) {
		SpringApplication.run(SpringBootWebfluxApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {

		// Eliminar desde desarrollo los datos de una coleccion
		mongoTemplate.dropCollection("productos").subscribe();
		mongoTemplate.dropCollection("categorias").subscribe();

		Categoria celulares = new Categoria("Celulares");
		Categoria deporte = new Categoria("Deporte");
		Categoria computacion = new Categoria("Computacion");
		Categoria muebles = new Categoria("Muebles");

		Flux.just(celulares, deporte, computacion, muebles)
				.flatMap(service::saveCategory)
				.doOnNext(c -> {
					log.info("Categoria creada: " + c.getNombre() + ", Id: " + c.getId());
				}).thenMany(
						Flux.just(new Producto("Iphone 11", 800.25, celulares),
								new Producto("Iphone SE", 1000.00, celulares),
								new Producto("Iphone 12", 900.20, celulares),
								new Producto("Iphone X", 600.00, celulares),
								new Producto("Hp pro book", 700.25, computacion))
								.flatMap(producto -> {
									producto.setCreateAt(new Date());
									return service.save(producto);
								})
				)
				.subscribe(producto -> log.info("Insert: " + producto.getId() + " " + producto.getNombre()));
	}
}
