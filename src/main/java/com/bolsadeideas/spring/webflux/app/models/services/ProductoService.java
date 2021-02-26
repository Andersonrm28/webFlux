package com.bolsadeideas.spring.webflux.app.models.services;

import com.bolsadeideas.spring.webflux.app.models.documents.Producto;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

public interface ProductoService {

    public Flux<Producto> findAll();

    public Flux<Producto> findAllWithNameUpperCase();

    public Flux<Producto> findAllWithNameUpperCaseRepeat();

    public Mono<Producto> findById(String id);

    public Mono<Producto> save(Producto producto);

    public Mono<Void> delete(Producto producto);
}
