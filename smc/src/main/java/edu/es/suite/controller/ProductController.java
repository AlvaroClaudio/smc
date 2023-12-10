package edu.es.suite.controller;

import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import edu.es.suite.service.ProductServiceImpl;

@RestController
@RequestMapping(value = "products")
@CrossOrigin
public class ProductController {
	
	@Autowired
	private ProductServiceImpl service;
	
	@GetMapping(value = "/{id}")
	public ResponseEntity<?> findById(@PathVariable Integer id) {

		Optional<?> data = service.findById(Integer.valueOf(id));

		if (data.isPresent()) {

			return ResponseEntity.ok(data.get());

		} else {

			return new ResponseEntity<>(HttpStatus.NOT_FOUND);
		}

	}
}
