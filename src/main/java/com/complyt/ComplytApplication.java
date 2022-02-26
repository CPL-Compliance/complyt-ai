package com.complyt;

import com.complyt.model.GroceryItem;
import com.complyt.repository.ItemRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.data.mongodb.repository.config.EnableMongoRepositories;
import org.springframework.web.client.RestTemplate;

@SpringBootApplication
@EnableMongoRepositories
public class ComplytApplication  implements CommandLineRunner {

	@Bean
	public RestTemplate restTemplate(){
		return new RestTemplate();
	}

	@Autowired
	ItemRepository itemRepository;

	public static void main(String[] args) {

		SpringApplication.run(ComplytApplication.class, args);
	}

	@Override
	public void run(String... args) throws Exception {
		itemRepository.deleteAll();
		itemRepository.save(new GroceryItem("1", "tomato", 1, "veg"));
	}
}
