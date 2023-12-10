package edu.es.suite.entity;

import java.util.Collection;

import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.transaction.Transaction;

@Entity
public class Product {
		
		@Id
		@GeneratedValue (strategy = GenerationType.AUTO)
		private int id;
		private String name; 
		private String description;
		private String code;
		
//		private Collection <Transaction>  transaction;
		

}
