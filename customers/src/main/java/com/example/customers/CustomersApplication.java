package com.example.customers;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.event.ApplicationReadyEvent;
import org.springframework.context.ApplicationListener;
import org.springframework.context.annotation.Bean;
import org.springframework.data.annotation.Id;
import org.springframework.data.repository.reactive.ReactiveCrudRepository;
import org.springframework.r2dbc.core.DatabaseClient;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RestController;
import reactor.core.publisher.Flux;

@SpringBootApplication
public class CustomersApplication {

	public static void main(String[] args) {
		SpringApplication.run(CustomersApplication.class, args);
	}

	@Bean
	ApplicationListener<ApplicationReadyEvent> ready(
		DatabaseClient dbc,
		CustomerRepository repository) {
		return event -> {
			var ddl = dbc.sql("create table if not exists customer (id serial primary key , name varchar(255) not null)").fetch().rowsUpdated();
			var saved = Flux.just("A", "B", "C").map(name -> new Customer(null, name)).flatMap(repository::save);
			ddl.thenMany(saved).subscribe(System.out::println);
		};
	}

}

@RestController
@RequiredArgsConstructor
class CustomerRestController {

	private final CustomerRepository repository;

	@GetMapping("/customers")
	Flux<Customer> get() {
		return this.repository.findAll();
	}

}

interface CustomerRepository extends ReactiveCrudRepository<Customer, Integer> {
}

@Data
@AllArgsConstructor
@NoArgsConstructor
class Customer {

	@Id
	private Integer id;
	private String name;
}
