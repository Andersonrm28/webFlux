package com.bolsadeideas.spring.webflux.app.models.dao;

import com.bolsadeideas.spring.webflux.app.models.documents.Producto;
import org.springframework.data.mongodb.repository.ReactiveMongoRepository;

public interface ProductoDao extends ReactiveMongoRepository<Producto, String> {

}
