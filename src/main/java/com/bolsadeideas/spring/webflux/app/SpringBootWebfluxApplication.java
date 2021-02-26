package com.bolsadeideas.spring.webflux.app;

import com.bolsadeideas.spring.webflux.app.models.dao.ProductoDao;
import com.bolsadeideas.spring.webflux.app.models.documents.Producto;
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
	private ProductoDao dao;

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

		Flux.just(new Producto("Iphone 11", 800.25),
				new Producto("Iphone SE", 1000.00),
				new Producto("Iphone 12", 900.20),
				new Producto("Iphone X", 600.00),
				new Producto("Iphone XR", 700.25))
				.flatMap(producto -> {
					producto.setCreateAt(new Date());
					return dao.save(producto);
				})
				.subscribe(producto -> log.info("Insert: " + producto.getId() + " " + producto.getNombre()));
	}
}
